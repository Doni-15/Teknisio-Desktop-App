package com.teknisio.services;

import com.teknisio.common.exception.BadRequestException;
import com.teknisio.common.exception.ResourceNotFoundException;
import com.teknisio.common.util.EnumParser;
import com.teknisio.common.util.TextUtil;
import com.teknisio.dto.responses.CustomerTechnicianResponse;
import com.teknisio.dto.responses.DeviceCategoryResponse;
import com.teknisio.model.entities.KategoriLayanan;
import com.teknisio.model.entities.TeknisiKategoriLayanan;
import com.teknisio.model.entities.TeknisiProfile;
import com.teknisio.model.enums.TeknisiStatus;
import com.teknisio.model.enums.UserRole;
import com.teknisio.model.enums.UserStatus;
import com.teknisio.repositories.KategoriLayananRepository;
import com.teknisio.repositories.TeknisiKategoriLayananRepository;
import com.teknisio.repositories.TeknisiProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerTechnicianService {

  private static final String SORT_RATING = "rating";
  private static final String SORT_TOTAL_JOBS = "totalJobs";
  private static final String SORT_NAME = "name";

  private final KategoriLayananRepository kategoriLayananRepository;
  private final TeknisiKategoriLayananRepository teknisiKategoriLayananRepository;
  private final TeknisiProfileRepository teknisiProfileRepository;

  @Transactional(readOnly = true)
  public List<CustomerTechnicianResponse> searchTechnicians(
    String deviceCategoryId,
    String availabilityStatus,
    String sort
  ) {
    UUID categoryId = parseRequiredDeviceCategoryId(deviceCategoryId);

    kategoriLayananRepository
      .findByIdKategoriAndAktifTrueAndDeletedAtIsNull(categoryId)
      .orElseThrow(() -> new ResourceNotFoundException("Device category not found"));

    TeknisiStatus parsedAvailabilityStatus = EnumParser.parseOptional(
      TeknisiStatus.class,
      availabilityStatus,
      "availabilityStatus"
    );

    List<CustomerTechnicianResponse> technicians = teknisiKategoriLayananRepository
      .findByKategori_IdKategoriAndAktifTrue(categoryId)
      .stream()
      .map(TeknisiKategoriLayanan::getTeknisiProfile)
      .filter(this::isActiveTechnician)
      .filter(technician -> matchesAvailabilityStatus(technician, parsedAvailabilityStatus))
      .map(this::toResponse)
      .toList();

    return technicians.stream()
      .sorted(buildComparator(sort))
      .toList();
  }

  @Transactional(readOnly = true)
  public CustomerTechnicianResponse getTechnicianDetail(String technicianProfileId) {
    UUID idTeknisiProfile = parseTechnicianProfileId(technicianProfileId);

    TeknisiProfile technicianProfile = teknisiProfileRepository
      .findById(idTeknisiProfile)
      .filter(this::isActiveTechnician)
      .orElseThrow(() -> new ResourceNotFoundException("Technician not found"));

    return toResponse(technicianProfile);
  }

  private UUID parseRequiredDeviceCategoryId(String deviceCategoryId) {
    if (TextUtil.isBlank(deviceCategoryId)) {
      throw new BadRequestException("deviceCategoryId is required");
    }

    try {
      return UUID.fromString(deviceCategoryId);
    } catch (IllegalArgumentException exception) {
      throw new BadRequestException("Invalid device category id");
    }
  }

  private UUID parseTechnicianProfileId(String technicianProfileId) {
    if (TextUtil.isBlank(technicianProfileId)) {
      throw new BadRequestException("technicianProfileId is required");
    }

    try {
      return UUID.fromString(technicianProfileId);
    } catch (IllegalArgumentException exception) {
      throw new BadRequestException("Invalid technician profile id");
    }
  }

  private boolean isActiveTechnician(TeknisiProfile technicianProfile) {
    return technicianProfile != null
      && technicianProfile.getUser() != null
      && technicianProfile.getUser().getDeletedAt() == null
      && technicianProfile.getUser().getStatusAkun() == UserStatus.ACTIVE
      && technicianProfile.getUser().getRole() == UserRole.TECHNICIAN;
  }

  private boolean matchesAvailabilityStatus(
    TeknisiProfile technicianProfile,
    TeknisiStatus availabilityStatus
  ) {
    return availabilityStatus == null
      || technicianProfile.getStatusKetersediaan() == availabilityStatus;
  }

  private CustomerTechnicianResponse toResponse(TeknisiProfile technicianProfile) {
    return new CustomerTechnicianResponse(
      technicianProfile.getIdTeknisiProfile(),
      technicianProfile.getUser().getNama(),
      technicianProfile.getUser().getFotoProfil(),
      technicianProfile.getStatusKetersediaan(),
      technicianProfile.getRatingAvg(),
      technicianProfile.getRatingCount(),
      technicianProfile.getTotalPekerjaan(),
      technicianProfile.getDeskripsi(),
      getSupportedDeviceCategories(technicianProfile)
    );
  }

  private List<DeviceCategoryResponse> getSupportedDeviceCategories(TeknisiProfile technicianProfile) {
    return teknisiKategoriLayananRepository
      .findByTeknisiProfile_IdTeknisiProfileAndAktifTrue(technicianProfile.getIdTeknisiProfile())
      .stream()
      .map(TeknisiKategoriLayanan::getKategori)
      .filter(category -> Boolean.TRUE.equals(category.getAktif()))
      .filter(category -> category.getDeletedAt() == null)
      .sorted(Comparator.comparing(
        KategoriLayanan::getNamaKategori,
        String.CASE_INSENSITIVE_ORDER
      ))
      .map(category -> new DeviceCategoryResponse(
        category.getIdKategori(),
        category.getNamaKategori(),
        category.getIcon()
      ))
      .toList();
  }

  private Comparator<CustomerTechnicianResponse> buildComparator(String sort) {
    if (TextUtil.isBlank(sort)) {
      return Comparator
        .comparing(
          CustomerTechnicianResponse::averageRating,
          Comparator.nullsLast(Comparator.reverseOrder())
        )
        .thenComparing(
          CustomerTechnicianResponse::totalJobs,
          Comparator.nullsLast(Comparator.reverseOrder())
        )
        .thenComparing(CustomerTechnicianResponse::name, String.CASE_INSENSITIVE_ORDER);
    }

    return switch (sort.trim()) {
      case SORT_RATING -> Comparator
        .comparing(
          CustomerTechnicianResponse::averageRating,
          Comparator.nullsLast(Comparator.reverseOrder())
        )
        .thenComparing(CustomerTechnicianResponse::name, String.CASE_INSENSITIVE_ORDER);

      case SORT_TOTAL_JOBS -> Comparator
        .comparing(
          CustomerTechnicianResponse::totalJobs,
          Comparator.nullsLast(Comparator.reverseOrder())
        )
        .thenComparing(CustomerTechnicianResponse::name, String.CASE_INSENSITIVE_ORDER);

      case SORT_NAME -> Comparator
        .comparing(CustomerTechnicianResponse::name, String.CASE_INSENSITIVE_ORDER);

      default -> throw new BadRequestException("Invalid sort. Allowed values: rating, totalJobs, name");
    };
  }
}
