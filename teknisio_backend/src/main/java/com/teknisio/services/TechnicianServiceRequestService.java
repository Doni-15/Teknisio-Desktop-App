package com.teknisio.services;

import com.teknisio.common.exception.BadRequestException;
import com.teknisio.common.exception.ConflictException;
import com.teknisio.common.exception.ResourceNotFoundException;
import com.teknisio.common.util.EnumParser;
import com.teknisio.common.util.TextUtil;
import com.teknisio.dto.requests.CompleteServiceRequestRequest;
import com.teknisio.dto.requests.RejectServiceRequestRequest;
import com.teknisio.dto.responses.DeviceCategoryResponse;
import com.teknisio.dto.responses.TechnicianServiceRequestResponse;
import com.teknisio.model.entities.KategoriLayanan;
import com.teknisio.model.entities.PermintaanLayanan;
import com.teknisio.model.entities.PermintaanLayananKategori;
import com.teknisio.model.entities.TeknisiProfile;
import com.teknisio.model.entities.User;
import com.teknisio.model.enums.RequestStatus;
import com.teknisio.model.enums.UserRole;
import com.teknisio.model.enums.UserStatus;
import com.teknisio.repositories.PermintaanLayananKategoriRepository;
import com.teknisio.repositories.PermintaanLayananRepository;
import com.teknisio.repositories.TeknisiKategoriLayananRepository;
import com.teknisio.repositories.TeknisiProfileRepository;
import com.teknisio.security.CurrentUserService;
import com.teknisio.dto.responses.ServiceRequestStatusHistoryResponse;
import com.teknisio.model.entities.RiwayatStatus;
import com.teknisio.repositories.RiwayatStatusRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TechnicianServiceRequestService {

  private static final String SORT_LATEST = "latest";
  private static final String SORT_OLDEST = "oldest";

  private final CurrentUserService currentUserService;
  private final TeknisiProfileRepository teknisiProfileRepository;
  private final PermintaanLayananRepository permintaanLayananRepository;
  private final PermintaanLayananKategoriRepository permintaanLayananKategoriRepository;
  private final TeknisiKategoriLayananRepository teknisiKategoriLayananRepository;
  private final RiwayatStatusRepository riwayatStatusRepository;
  private final EntityManager entityManager;

  @Transactional(readOnly = true)
  public List<TechnicianServiceRequestResponse> getMyServiceRequests(String status, String sort) {
    TeknisiProfile technicianProfile = getCurrentActiveTechnicianProfile();

    RequestStatus parsedStatus = EnumParser.parseOptional(
      RequestStatus.class,
      status,
      "status"
    );

    String parsedSort = parseSort(sort);

    List<PermintaanLayanan> serviceRequests = parsedStatus == null
      ? permintaanLayananRepository.findByTeknisiProfile_IdTeknisiProfileOrderByWaktuPermintaanDesc(
          technicianProfile.getIdTeknisiProfile()
        )
      : permintaanLayananRepository.findByTeknisiProfile_IdTeknisiProfileAndStatusOrderByWaktuPermintaanDesc(
          technicianProfile.getIdTeknisiProfile(),
          parsedStatus
        );

    if (SORT_OLDEST.equals(parsedSort)) {
      serviceRequests = new ArrayList<>(serviceRequests);
      java.util.Collections.reverse(serviceRequests);
    }

    return serviceRequests.stream()
      .map(this::toResponse)
      .toList();
  }

  @Transactional(readOnly = true)
  public TechnicianServiceRequestResponse getMyServiceRequestDetail(String serviceRequestId) {
    TeknisiProfile technicianProfile = getCurrentActiveTechnicianProfile();

    PermintaanLayanan serviceRequest = getOwnedServiceRequest(
      technicianProfile,
      serviceRequestId
    );

    return toResponse(serviceRequest);
  }

  @Transactional(readOnly = true)
  public List<ServiceRequestStatusHistoryResponse> getMyServiceRequestStatusHistory(
    String serviceRequestId
  ) {
    TeknisiProfile technicianProfile = getCurrentActiveTechnicianProfile();

    PermintaanLayanan serviceRequest = getOwnedServiceRequest(
      technicianProfile,
      serviceRequestId
    );

    return riwayatStatusRepository
      .findByPermintaan_IdPermintaanOrderByCreatedAtAsc(serviceRequest.getIdPermintaan())
      .stream()
      .map(this::toStatusHistoryResponse)
      .toList();
  }

  @Transactional
  public TechnicianServiceRequestResponse acceptMyServiceRequest(String serviceRequestId) {
    TeknisiProfile technicianProfile = getCurrentActiveTechnicianProfile();

    PermintaanLayanan serviceRequest = getOwnedServiceRequest(
      technicianProfile,
      serviceRequestId
    );

    validateStatus(serviceRequest, RequestStatus.WAITING, "accepted");
    validateTechnicianStillSupportsSelectedCategories(technicianProfile, serviceRequest);

    serviceRequest.setStatus(RequestStatus.ACCEPTED);
    serviceRequest.setDiubahOlehTerakhir(technicianProfile.getUser());

    PermintaanLayanan savedServiceRequest =
      permintaanLayananRepository.saveAndFlush(serviceRequest);

    entityManager.refresh(savedServiceRequest);

    return toResponse(savedServiceRequest);
  }

  @Transactional
  public TechnicianServiceRequestResponse rejectMyServiceRequest(
    String serviceRequestId,
    RejectServiceRequestRequest request
  ) {
    TeknisiProfile technicianProfile = getCurrentActiveTechnicianProfile();

    PermintaanLayanan serviceRequest = getOwnedServiceRequest(
      technicianProfile,
      serviceRequestId
    );

    validateStatus(serviceRequest, RequestStatus.WAITING, "rejected");

    String rejectReason = request == null
      ? null
      : TextUtil.trim(request.rejectReason());

    serviceRequest.setAlasanTolak(rejectReason);
    serviceRequest.setStatus(RequestStatus.REJECTED);
    serviceRequest.setDiubahOlehTerakhir(technicianProfile.getUser());

    PermintaanLayanan savedServiceRequest =
      permintaanLayananRepository.saveAndFlush(serviceRequest);

    entityManager.refresh(savedServiceRequest);

    return toResponse(savedServiceRequest);
  }

  @Transactional
  public TechnicianServiceRequestResponse startMyServiceRequest(String serviceRequestId) {
    TeknisiProfile technicianProfile = getCurrentActiveTechnicianProfile();

    PermintaanLayanan serviceRequest = getOwnedServiceRequest(
      technicianProfile,
      serviceRequestId
    );

    validateStatus(serviceRequest, RequestStatus.ACCEPTED, "started");

    serviceRequest.setStatus(RequestStatus.ON_PROGRESS);
    serviceRequest.setDiubahOlehTerakhir(technicianProfile.getUser());

    PermintaanLayanan savedServiceRequest =
      permintaanLayananRepository.saveAndFlush(serviceRequest);

    entityManager.refresh(savedServiceRequest);

    return toResponse(savedServiceRequest);
  }

  @Transactional
  public TechnicianServiceRequestResponse completeMyServiceRequest(
    String serviceRequestId,
    CompleteServiceRequestRequest request
  ) {
    TeknisiProfile technicianProfile = getCurrentActiveTechnicianProfile();

    PermintaanLayanan serviceRequest = getOwnedServiceRequest(
      technicianProfile,
      serviceRequestId
    );

    validateStatus(serviceRequest, RequestStatus.ON_PROGRESS, "completed");

    serviceRequest.setBiayaAkhir(request.finalCost());
    serviceRequest.setCatatanTeknisi(TextUtil.trim(request.technicianNote()));
    serviceRequest.setStatus(RequestStatus.COMPLETED);
    serviceRequest.setDiubahOlehTerakhir(technicianProfile.getUser());

    PermintaanLayanan savedServiceRequest =
      permintaanLayananRepository.saveAndFlush(serviceRequest);

    entityManager.refresh(savedServiceRequest);

    return toResponse(savedServiceRequest);
  }

  private TeknisiProfile getCurrentActiveTechnicianProfile() {
    UUID currentUserId = currentUserService.getCurrentUserId();

    return teknisiProfileRepository.findByUser_IdUser(currentUserId)
      .filter(this::isActiveTechnician)
      .orElseThrow(() -> new ResourceNotFoundException("Technician profile not found"));
  }

  private boolean isActiveTechnician(TeknisiProfile technicianProfile) {
    return technicianProfile != null
      && technicianProfile.getUser() != null
      && technicianProfile.getUser().getDeletedAt() == null
      && technicianProfile.getUser().getStatusAkun() == UserStatus.ACTIVE
      && technicianProfile.getUser().getRole() == UserRole.TECHNICIAN;
  }

  private PermintaanLayanan getOwnedServiceRequest(
    TeknisiProfile technicianProfile,
    String serviceRequestId
  ) {
    UUID idPermintaan = parseServiceRequestId(serviceRequestId);

    return permintaanLayananRepository.findById(idPermintaan)
      .filter(request -> request.getTeknisiProfile() != null)
      .filter(request -> request.getTeknisiProfile().getIdTeknisiProfile().equals(
        technicianProfile.getIdTeknisiProfile()
      ))
      .orElseThrow(() -> new ResourceNotFoundException("Service request not found"));
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

  private String parseSort(String sort) {
    if (TextUtil.isBlank(sort)) {
      return SORT_LATEST;
    }

    String normalizedSort = sort.trim().toLowerCase();

    if (SORT_LATEST.equals(normalizedSort) || SORT_OLDEST.equals(normalizedSort)) {
      return normalizedSort;
    }

    throw new BadRequestException("Invalid sort. Allowed values: latest, oldest");
  }

  private void validateStatus(
    PermintaanLayanan serviceRequest,
    RequestStatus requiredStatus,
    String action
  ) {
    if (serviceRequest.getStatus() == requiredStatus) {
      return;
    }

    throw new ConflictException(
      "Service request cannot be " + action + " from status " + serviceRequest.getStatus()
    );
  }

  private void validateTechnicianStillSupportsSelectedCategories(
    TeknisiProfile technicianProfile,
    PermintaanLayanan serviceRequest
  ) {
    List<KategoriLayanan> selectedCategories = getSelectedCategories(serviceRequest);

    for (KategoriLayanan category : selectedCategories) {
      boolean supported = teknisiKategoriLayananRepository
        .existsByTeknisiProfile_IdTeknisiProfileAndKategori_IdKategoriAndAktifTrue(
          technicianProfile.getIdTeknisiProfile(),
          category.getIdKategori()
        );

      if (!supported) {
        throw new ConflictException(
          "Technician does not support selected device category: " + category.getNamaKategori()
        );
      }
    }
  }

  private List<KategoriLayanan> getSelectedCategories(PermintaanLayanan serviceRequest) {
    return permintaanLayananKategoriRepository
      .findByPermintaan_IdPermintaan(serviceRequest.getIdPermintaan())
      .stream()
      .map(PermintaanLayananKategori::getKategori)
      .filter(category -> category != null && category.getDeletedAt() == null && Boolean.TRUE.equals(category.getAktif()))
      .toList();
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

  private TechnicianServiceRequestResponse toResponse(PermintaanLayanan serviceRequest) {
    List<DeviceCategoryResponse> selectedDeviceCategories = getSelectedCategories(serviceRequest)
      .stream()
      .map(this::toDeviceCategoryResponse)
      .toList();

    User customer = serviceRequest.getPengguna();

    return new TechnicianServiceRequestResponse(
      serviceRequest.getIdPermintaan(),
      serviceRequest.getKodePermintaan(),

      customer.getIdUser(),
      customer.getNama(),
      customer.getNoTelepon(),
      customer.getFotoProfil(),

      serviceRequest.getTeknisiProfile().getIdTeknisiProfile(),
      serviceRequest.getStatus(),

      serviceRequest.getDeskripsiMasalah(),
      serviceRequest.getAlamat(),
      serviceRequest.getDetailAlamat(),

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

  private DeviceCategoryResponse toDeviceCategoryResponse(KategoriLayanan category) {
    return new DeviceCategoryResponse(
      category.getIdKategori(),
      category.getNamaKategori(),
      category.getIcon()
    );
  }
}
