package com.teknisio.services;

import com.teknisio.common.exception.BadRequestException;
import com.teknisio.common.exception.ConflictException;
import com.teknisio.common.exception.ResourceNotFoundException;
import com.teknisio.dto.requests.AddTechnicianDeviceCategoryRequest;
import com.teknisio.dto.responses.TechnicianDeviceCategoryResponse;
import com.teknisio.model.entities.KategoriLayanan;
import com.teknisio.model.entities.TeknisiKategoriLayanan;
import com.teknisio.model.entities.TeknisiKategoriLayananId;
import com.teknisio.model.entities.TeknisiProfile;
import com.teknisio.repositories.KategoriLayananRepository;
import com.teknisio.repositories.TeknisiKategoriLayananRepository;
import com.teknisio.repositories.TeknisiProfileRepository;
import com.teknisio.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TechnicianDeviceCategoryService {

  private final CurrentUserService currentUserService;
  private final TeknisiProfileRepository teknisiProfileRepository;
  private final KategoriLayananRepository kategoriLayananRepository;
  private final TeknisiKategoriLayananRepository teknisiKategoriLayananRepository;

  @Transactional(readOnly = true)
  public List<TechnicianDeviceCategoryResponse> getMyDeviceCategories() {
    TeknisiProfile technicianProfile = getCurrentTechnicianProfile();

    return teknisiKategoriLayananRepository
      .findByTeknisiProfile_IdTeknisiProfileAndAktifTrue(technicianProfile.getIdTeknisiProfile())
      .stream()
      .filter(skill -> Boolean.TRUE.equals(skill.getKategori().getAktif()))
      .filter(skill -> skill.getKategori().getDeletedAt() == null)
      .sorted(Comparator.comparing(
        skill -> skill.getKategori().getNamaKategori(),
        String.CASE_INSENSITIVE_ORDER
      ))
      .map(this::toResponse)
      .toList();
  }

  @Transactional
  public TechnicianDeviceCategoryResponse addDeviceCategory(
    AddTechnicianDeviceCategoryRequest request
  ) {
    TeknisiProfile technicianProfile = getCurrentTechnicianProfile();
    UUID deviceCategoryId = parseDeviceCategoryId(request.deviceCategoryId());

    KategoriLayanan category = kategoriLayananRepository
      .findByIdKategoriAndAktifTrueAndDeletedAtIsNull(deviceCategoryId)
      .orElseThrow(() -> new ResourceNotFoundException("Device category not found"));

    TeknisiKategoriLayananId skillId = new TeknisiKategoriLayananId(
      technicianProfile.getIdTeknisiProfile(),
      category.getIdKategori()
    );

    TeknisiKategoriLayanan skill = teknisiKategoriLayananRepository
      .findById(skillId)
      .map(existingSkill -> reactivateSkillIfNeeded(existingSkill))
      .orElseGet(() -> createSkill(skillId, technicianProfile, category));

    TeknisiKategoriLayanan savedSkill = teknisiKategoriLayananRepository.save(skill);

    return toResponse(savedSkill);
  }

  @Transactional
  public void removeDeviceCategory(String deviceCategoryId) {
    TeknisiProfile technicianProfile = getCurrentTechnicianProfile();
    UUID categoryId = parseDeviceCategoryId(deviceCategoryId);

    TeknisiKategoriLayananId skillId = new TeknisiKategoriLayananId(
      technicianProfile.getIdTeknisiProfile(),
      categoryId
    );

    TeknisiKategoriLayanan skill = teknisiKategoriLayananRepository
      .findById(skillId)
      .filter(existingSkill -> Boolean.TRUE.equals(existingSkill.getAktif()))
      .orElseThrow(() -> new ResourceNotFoundException("Technician device category not found"));

    skill.setAktif(false);
    teknisiKategoriLayananRepository.save(skill);
  }

  private TeknisiProfile getCurrentTechnicianProfile() {
    UUID currentUserId = currentUserService.getCurrentUserId();

    return teknisiProfileRepository.findByUser_IdUser(currentUserId)
      .orElseThrow(() -> new ResourceNotFoundException("Technician profile not found"));
  }

  private UUID parseDeviceCategoryId(String deviceCategoryId) {
    try {
      return UUID.fromString(deviceCategoryId);
    } catch (IllegalArgumentException exception) {
      throw new BadRequestException("Invalid device category id");
    }
  }

  private TeknisiKategoriLayanan reactivateSkillIfNeeded(TeknisiKategoriLayanan existingSkill) {
    if (Boolean.TRUE.equals(existingSkill.getAktif())) {
      throw new ConflictException("Device category already added");
    }

    existingSkill.setAktif(true);
    return existingSkill;
  }

  private TeknisiKategoriLayanan createSkill(
    TeknisiKategoriLayananId skillId,
    TeknisiProfile technicianProfile,
    KategoriLayanan category
  ) {
    return TeknisiKategoriLayanan.builder()
      .id(skillId)
      .teknisiProfile(technicianProfile)
      .kategori(category)
      .aktif(true)
      .build();
  }

  private TechnicianDeviceCategoryResponse toResponse(TeknisiKategoriLayanan skill) {
    return new TechnicianDeviceCategoryResponse(
      skill.getTeknisiProfile().getIdTeknisiProfile(),
      skill.getKategori().getIdKategori(),
      skill.getKategori().getNamaKategori(),
      skill.getKategori().getIcon(),
      skill.getAktif()
    );
  }
}
