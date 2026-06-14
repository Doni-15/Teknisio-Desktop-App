package com.teknisio.services;

import com.teknisio.common.exception.BadRequestException;
import com.teknisio.common.exception.ConflictException;
import com.teknisio.common.exception.ResourceNotFoundException;
import com.teknisio.common.exception.UnauthorizedException;
import com.teknisio.common.util.EnumParser;
import com.teknisio.common.util.TextUtil;
import com.teknisio.dto.requests.CancelServiceRequestRequest;
import com.teknisio.dto.requests.CreateServiceRequestRequest;
import com.teknisio.dto.responses.DeviceCategoryResponse;
import com.teknisio.dto.responses.ServiceRequestResponse;
import com.teknisio.dto.responses.ServiceRequestStatusHistoryResponse;
import com.teknisio.dto.requests.CreateReviewRequest;
import com.teknisio.dto.responses.ReviewResponse;
import com.teknisio.model.entities.KategoriLayanan;
import com.teknisio.model.entities.PermintaanLayanan;
import com.teknisio.model.entities.PermintaanLayananKategori;
import com.teknisio.model.entities.PermintaanLayananKategoriId;
import com.teknisio.model.entities.TeknisiKategoriLayanan;
import com.teknisio.model.entities.TeknisiProfile;
import com.teknisio.model.entities.User;
import com.teknisio.model.enums.RequestStatus;
import com.teknisio.model.enums.TeknisiStatus;
import com.teknisio.model.enums.UserRole;
import com.teknisio.model.enums.UserStatus;
import com.teknisio.model.entities.RiwayatStatus;
import com.teknisio.model.entities.Review;
import com.teknisio.repositories.KategoriLayananRepository;
import com.teknisio.repositories.PermintaanLayananKategoriRepository;
import com.teknisio.repositories.PermintaanLayananRepository;
import com.teknisio.repositories.TeknisiKategoriLayananRepository;
import com.teknisio.repositories.TeknisiProfileRepository;
import com.teknisio.repositories.UserRepository;
import com.teknisio.repositories.ReviewRepository;
import com.teknisio.repositories.RiwayatStatusRepository;
import com.teknisio.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class CustomerServiceRequestService {

  private final CurrentUserService currentUserService;
  private final UserRepository userRepository;
  private final TeknisiProfileRepository teknisiProfileRepository;
  private final KategoriLayananRepository kategoriLayananRepository;
  private final TeknisiKategoriLayananRepository teknisiKategoriLayananRepository;
  private final PermintaanLayananRepository permintaanLayananRepository;
  private final PermintaanLayananKategoriRepository permintaanLayananKategoriRepository;
  private final RiwayatStatusRepository riwayatStatusRepository;
  private final ReviewRepository reviewRepository;

  @Transactional
  public ServiceRequestResponse createServiceRequest(CreateServiceRequestRequest request) {
    User customer = getCurrentActiveCustomer();
    TeknisiProfile technicianProfile = getActiveTechnicianProfile(request.technicianProfileId());

    List<UUID> deviceCategoryIds = parseDeviceCategoryIds(request.deviceCategoryIds());
    List<KategoriLayanan> selectedCategories = getActiveDeviceCategories(deviceCategoryIds);

    validateTechnicianAvailableForOrder(technicianProfile);
    validateTechnicianSupportsSelectedCategories(technicianProfile, selectedCategories);

    PermintaanLayanan serviceRequest = PermintaanLayanan.builder()
      .pengguna(customer)
      .teknisiProfile(technicianProfile)
      .alamat(TextUtil.trim(request.address()))
      .detailAlamat(TextUtil.trim(request.addressDetail()))
      .deskripsiMasalah(TextUtil.trim(request.issueDescription()))
      .latitude(request.latitude())
      .longitude(request.longitude())
      .status(RequestStatus.WAITING)
      .diubahOlehTerakhir(customer)
      .build();

    PermintaanLayanan savedServiceRequest = permintaanLayananRepository.saveAndFlush(serviceRequest);

    List<PermintaanLayananKategori> selectedCategoryEntities = selectedCategories.stream()
      .map(category -> PermintaanLayananKategori.builder()
        .id(new PermintaanLayananKategoriId(
          savedServiceRequest.getIdPermintaan(),
          category.getIdKategori()
        ))
        .permintaan(savedServiceRequest)
        .kategori(category)
        .build()
      )
      .toList();

    permintaanLayananKategoriRepository.saveAll(selectedCategoryEntities);

    return toResponse(savedServiceRequest, selectedCategories);
  }

  @Transactional(readOnly = true)
  public List<ServiceRequestResponse> getMyServiceRequests(String status) {
    User customer = getCurrentActiveCustomer();

    RequestStatus parsedStatus = EnumParser.parseOptional(
      RequestStatus.class,
      status,
      "status"
    );

    List<PermintaanLayanan> serviceRequests = parsedStatus == null
      ? permintaanLayananRepository.findByPengguna_IdUserOrderByWaktuPermintaanDesc(
          customer.getIdUser()
        )
      : permintaanLayananRepository.findByPengguna_IdUserAndStatusOrderByWaktuPermintaanDesc(
          customer.getIdUser(),
          parsedStatus
        );

    return serviceRequests.stream()
      .map(this::toResponse)
      .toList();
  }

  @Transactional(readOnly = true)
  public ServiceRequestResponse getMyServiceRequestDetail(String serviceRequestId) {
    User customer = getCurrentActiveCustomer();
    UUID idPermintaan = parseServiceRequestId(serviceRequestId);

    PermintaanLayanan serviceRequest = getOwnedServiceRequest(idPermintaan, customer);

    return toResponse(serviceRequest);
  }

  @Transactional(readOnly = true)
  public List<ServiceRequestStatusHistoryResponse> getMyServiceRequestStatusHistory(
    String serviceRequestId
  ) {
    User customer = getCurrentActiveCustomer();
    UUID idPermintaan = parseServiceRequestId(serviceRequestId);

    PermintaanLayanan serviceRequest = getOwnedServiceRequest(idPermintaan, customer);

    return riwayatStatusRepository
      .findByPermintaan_IdPermintaanOrderByCreatedAtAsc(serviceRequest.getIdPermintaan())
      .stream()
      .map(this::toStatusHistoryResponse)
      .toList();
  }

  @Transactional
  public ServiceRequestResponse cancelMyServiceRequest(
    String serviceRequestId,
    CancelServiceRequestRequest request
  ) {
    User customer = getCurrentActiveCustomer();
    UUID idPermintaan = parseServiceRequestId(serviceRequestId);

    PermintaanLayanan serviceRequest = getOwnedServiceRequest(idPermintaan, customer);

    RequestStatus previousStatus = serviceRequest.getStatus();

    validateCancellableStatus(previousStatus);

    serviceRequest.setAlasanBatal(TextUtil.trim(request.cancelReason()));
    serviceRequest.setStatus(RequestStatus.CANCELLED);
    serviceRequest.setWaktuDibatalkan(OffsetDateTime.now());
    serviceRequest.setDiubahOlehTerakhir(customer);

    if (previousStatus == RequestStatus.ACCEPTED
      || previousStatus == RequestStatus.ON_PROGRESS) {
      setTechnicianAvailability(serviceRequest.getTeknisiProfile(), TeknisiStatus.ONLINE);
    }

    PermintaanLayanan savedServiceRequest = permintaanLayananRepository.saveAndFlush(serviceRequest);

    return toResponse(savedServiceRequest);
  }

  @Transactional
  public ReviewResponse createReview(
    String serviceRequestId,
    CreateReviewRequest request
  ) {
    User customer = getCurrentActiveCustomer();
    UUID idPermintaan = parseServiceRequestId(serviceRequestId);

    PermintaanLayanan serviceRequest = getOwnedServiceRequest(idPermintaan, customer);

    validateReviewableStatus(serviceRequest);
    validateReviewDoesNotExist(serviceRequest);

    TeknisiProfile technicianProfile = serviceRequest.getTeknisiProfile();

    Review review = Review.builder()
      .permintaan(serviceRequest)
      .customer(customer)
      .teknisiProfile(technicianProfile)
      .rating(request.rating())
      .comment(TextUtil.trim(request.comment()))
      .build();

    Review savedReview = reviewRepository.saveAndFlush(review);

    updateTechnicianRating(technicianProfile, request.rating());

    return toReviewResponse(savedReview);
  }

  private void validateTechnicianAvailableForOrder(TeknisiProfile technicianProfile) {
    if (technicianProfile == null
      || technicianProfile.getStatusKetersediaan() != TeknisiStatus.ONLINE) {
      throw new ConflictException("Technician is not available");
    }
  }

  private void setTechnicianAvailability(
    TeknisiProfile technicianProfile,
    TeknisiStatus status
  ) {
    if (technicianProfile == null || status == null) {
      return;
    }

    technicianProfile.setStatusKetersediaan(status);
    teknisiProfileRepository.save(technicianProfile);
  }

  private User getCurrentActiveCustomer() {
    UUID currentUserId = currentUserService.getCurrentUserId();

    return userRepository.findByIdUserAndDeletedAtIsNull(currentUserId)
      .filter(user -> user.getStatusAkun() == UserStatus.ACTIVE)
      .filter(user -> user.getRole() == UserRole.CUSTOMER)
      .orElseThrow(() -> new UnauthorizedException("Unauthorized"));
  }

  private PermintaanLayanan getOwnedServiceRequest(UUID idPermintaan, User customer) {
    return permintaanLayananRepository.findById(idPermintaan)
      .filter(request -> request.getPengguna() != null)
      .filter(request -> request.getPengguna().getIdUser().equals(customer.getIdUser()))
      .orElseThrow(() -> new ResourceNotFoundException("Service request not found"));
  }

  private TeknisiProfile getActiveTechnicianProfile(String technicianProfileId) {
    UUID idTeknisiProfile = parseTechnicianProfileId(technicianProfileId);

    return teknisiProfileRepository.findById(idTeknisiProfile)
      .filter(this::isActiveTechnician)
      .orElseThrow(() -> new ResourceNotFoundException("Technician not found"));
  }

  private boolean isActiveTechnician(TeknisiProfile technicianProfile) {
    return technicianProfile != null
      && technicianProfile.getUser() != null
      && technicianProfile.getUser().getDeletedAt() == null
      && technicianProfile.getUser().getStatusAkun() == UserStatus.ACTIVE
      && technicianProfile.getUser().getRole() == UserRole.TECHNICIAN;
  }

  private UUID parseTechnicianProfileId(String technicianProfileId) {
    if (TextUtil.isBlank(technicianProfileId)) {
      throw new BadRequestException("Technician profile id is required");
    }

    try {
      return UUID.fromString(technicianProfileId);
    } catch (IllegalArgumentException exception) {
      throw new BadRequestException("Invalid technician profile id");
    }
  }

  private UUID parseServiceRequestId(String serviceRequestId) {
    if (TextUtil.isBlank(serviceRequestId)) {
      throw new BadRequestException("Service request id is required");
    }

    try {
      return UUID.fromString(serviceRequestId);
    } catch (IllegalArgumentException exception) {
      throw new BadRequestException("Invalid service request id");
    }
  }

  private List<UUID> parseDeviceCategoryIds(List<String> rawDeviceCategoryIds) {
    Set<UUID> uniqueIds = new LinkedHashSet<>();
    List<String> normalizedRawIds = new ArrayList<>();

    for (String rawId : rawDeviceCategoryIds) {
      String trimmedId = TextUtil.trim(rawId);
      normalizedRawIds.add(trimmedId);

      try {
        uniqueIds.add(UUID.fromString(trimmedId));
      } catch (IllegalArgumentException exception) {
        throw new BadRequestException("Invalid device category id");
      }
    }

    if (uniqueIds.size() != normalizedRawIds.size()) {
      throw new BadRequestException("Device category ids must not contain duplicate values");
    }

    return new ArrayList<>(uniqueIds);
  }

  private List<KategoriLayanan> getActiveDeviceCategories(List<UUID> deviceCategoryIds) {
    List<KategoriLayanan> categories = new ArrayList<>();

    for (UUID deviceCategoryId : deviceCategoryIds) {
      KategoriLayanan category = kategoriLayananRepository
        .findByIdKategoriAndAktifTrueAndDeletedAtIsNull(deviceCategoryId)
        .orElseThrow(() -> new ResourceNotFoundException("Device category not found"));

      categories.add(category);
    }

    return categories;
  }

  private void validateTechnicianSupportsSelectedCategories(
    TeknisiProfile technicianProfile,
    List<KategoriLayanan> selectedCategories
  ) {
    Set<UUID> supportedCategoryIds = teknisiKategoriLayananRepository
      .findByTeknisiProfile_IdTeknisiProfileAndAktifTrue(technicianProfile.getIdTeknisiProfile())
      .stream()
      .map(TeknisiKategoriLayanan::getKategori)
      .filter(category -> category != null && category.getDeletedAt() == null && Boolean.TRUE.equals(category.getAktif()))
      .map(KategoriLayanan::getIdKategori)
      .collect(Collectors.toSet());

    for (KategoriLayanan selectedCategory : selectedCategories) {
      if (!supportedCategoryIds.contains(selectedCategory.getIdKategori())) {
        throw new BadRequestException(
          "Technician does not support selected device category: " + selectedCategory.getNamaKategori()
        );
      }
    }
  }

  private void validateCancellableStatus(RequestStatus status) {
    if (
      status == RequestStatus.WAITING
        || status == RequestStatus.ACCEPTED
        || status == RequestStatus.ON_PROGRESS
    ) {
      return;
    }

    throw new ConflictException("Service request cannot be cancelled from status " + status);
  }

  private void validateReviewableStatus(PermintaanLayanan serviceRequest) {
    if (serviceRequest.getStatus() == RequestStatus.COMPLETED) {
      return;
    }

    throw new ConflictException(
      "Service request can only be reviewed after completed"
    );
  }

  private void validateReviewDoesNotExist(PermintaanLayanan serviceRequest) {
    boolean reviewExists = reviewRepository.existsByPermintaan_IdPermintaan(
      serviceRequest.getIdPermintaan()
    );

    if (reviewExists) {
      throw new ConflictException("Service request already has a review");
    }
  }

  private void updateTechnicianRating(
    TeknisiProfile technicianProfile,
    Integer newRating
  ) {
    Integer currentCount = technicianProfile.getRatingCount() == null
      ? 0
      : technicianProfile.getRatingCount();

    BigDecimal currentAverage = technicianProfile.getRatingAvg() == null
      ? BigDecimal.ZERO
      : technicianProfile.getRatingAvg();

    int newCount = currentCount + 1;

    BigDecimal totalRating = currentAverage
      .multiply(BigDecimal.valueOf(currentCount))
      .add(BigDecimal.valueOf(newRating));

    BigDecimal newAverage = totalRating
      .divide(BigDecimal.valueOf(newCount), 2, RoundingMode.HALF_UP);

    technicianProfile.setRatingCount(newCount);
    technicianProfile.setRatingAvg(newAverage);

    teknisiProfileRepository.save(technicianProfile);
  }

  private ReviewResponse toReviewResponse(Review review) {
    return new ReviewResponse(
      review.getIdReview(),
      review.getPermintaan().getIdPermintaan(),
      review.getCustomer().getIdUser(),
      review.getTeknisiProfile().getIdTeknisiProfile(),
      review.getRating(),
      review.getComment(),
      review.getCreatedAt(),
      review.getUpdatedAt()
    );
  }

  private ServiceRequestStatusHistoryResponse toStatusHistoryResponse(RiwayatStatus history) {
    User changedBy = history.getDiubahOleh();

    return new ServiceRequestStatusHistoryResponse(
      history.getIdRiwayat(),
      history.getStatusSebelum(),
      history.getStatusSesudah(),
      history.getCatatan(),
      changedBy == null ? null : changedBy.getIdUser(),
      history.getCreatedAt()
    );
  }

  private ServiceRequestResponse toResponse(PermintaanLayanan serviceRequest) {
    List<KategoriLayanan> selectedCategories = permintaanLayananKategoriRepository
      .findByPermintaan_IdPermintaan(serviceRequest.getIdPermintaan())
      .stream()
      .map(PermintaanLayananKategori::getKategori)
      .filter(category -> category != null && category.getDeletedAt() == null && Boolean.TRUE.equals(category.getAktif()))
      .toList();

    return toResponse(serviceRequest, selectedCategories);
  }

  private ServiceRequestResponse toResponse(
    PermintaanLayanan serviceRequest,
    List<KategoriLayanan> selectedCategories
  ) {
    List<DeviceCategoryResponse> selectedDeviceCategories = selectedCategories.stream()
      .map(category -> new DeviceCategoryResponse(
        category.getIdKategori(),
        category.getNamaKategori(),
        category.getIcon()
      ))
      .toList();

    return new ServiceRequestResponse(
      serviceRequest.getIdPermintaan(),
      serviceRequest.getKodePermintaan(),
      serviceRequest.getPengguna().getIdUser(),
      serviceRequest.getPengguna().getNama(),
      serviceRequest.getPengguna().getNoTelepon(),
      serviceRequest.getPengguna().getFotoProfil(),
      serviceRequest.getTeknisiProfile().getIdTeknisiProfile(),
      serviceRequest.getStatus(),
      serviceRequest.getDeskripsiMasalah(),
      serviceRequest.getAlamat(),
      serviceRequest.getDetailAlamat(),
      serviceRequest.getLatitude(),
      serviceRequest.getLongitude(),
      serviceRequest.getEstimasiBiaya(),
      serviceRequest.getBiayaAkhir(),
      serviceRequest.getCatatanTeknisi(),
      serviceRequest.getAlasanBatal(),
      serviceRequest.getAlasanTolak(),
      selectedDeviceCategories,
      serviceRequest.getWaktuPermintaan(),
      serviceRequest.getWaktuDiterima(),
      serviceRequest.getWaktuDiproses(),
      serviceRequest.getWaktuSelesai(),
      serviceRequest.getWaktuDibatalkan(),
      serviceRequest.getWaktuDitolak()
    );
  }
}
