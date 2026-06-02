package com.teknisio.services;

import com.teknisio.common.exception.BadRequestException;
import com.teknisio.common.exception.ResourceNotFoundException;
import com.teknisio.dto.responses.DeviceCategoryResponse;
import com.teknisio.model.entities.KategoriLayanan;
import com.teknisio.repositories.KategoriLayananRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeviceCategoryService {

  private final KategoriLayananRepository kategoriLayananRepository;

  @Transactional(readOnly = true)
  public List<DeviceCategoryResponse> getActiveDeviceCategories() {
    return kategoriLayananRepository
      .findByAktifTrueAndDeletedAtIsNullOrderByNamaKategoriAsc()
      .stream()
      .map(this::toResponse)
      .toList();
  }

  @Transactional(readOnly = true)
  public DeviceCategoryResponse getActiveDeviceCategoryById(String deviceCategoryId) {
    UUID idKategori = parseDeviceCategoryId(deviceCategoryId);

    KategoriLayanan category = kategoriLayananRepository
      .findByIdKategoriAndAktifTrueAndDeletedAtIsNull(idKategori)
      .orElseThrow(() -> new ResourceNotFoundException("Device category not found"));

    return toResponse(category);
  }

  private UUID parseDeviceCategoryId(String deviceCategoryId) {
    try {
      return UUID.fromString(deviceCategoryId);
    } catch (IllegalArgumentException exception) {
      throw new BadRequestException("Invalid device category id");
    }
  }

  private DeviceCategoryResponse toResponse(KategoriLayanan category) {
    return new DeviceCategoryResponse(
      category.getIdKategori(),
      category.getNamaKategori(),
      category.getIcon()
    );
  }
}
