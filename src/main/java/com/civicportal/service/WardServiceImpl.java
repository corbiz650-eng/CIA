package com.civicportal.service;

import com.civicportal.dto.ward.WardRequest;
import com.civicportal.dto.ward.WardResponse;
import com.civicportal.entity.Ward;
import com.civicportal.exception.BadRequestException;
import com.civicportal.exception.DuplicateResourceException;
import com.civicportal.exception.ResourceNotFoundException;
import com.civicportal.mapper.WardMapper;
import com.civicportal.repository.WardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class WardServiceImpl implements WardService {

    private final WardRepository wardRepository;
    private final WardMapper wardMapper;

    public WardServiceImpl(WardRepository wardRepository, WardMapper wardMapper) {
        this.wardRepository = wardRepository;
        this.wardMapper = wardMapper;
    }

    @Override
    @Transactional
    public WardResponse createWard(WardRequest request) {
        String code = normalizeCode(request.getCode());
        ensureCodeAvailable(code);

        Ward ward = new Ward(
                code,
                normalizeName(request.getName()),
                normalizeDescription(request.getDescription())
        );
        return wardMapper.toResponse(wardRepository.save(ward));
    }

    @Override
    @Transactional
    public WardResponse updateWard(Long wardId, WardRequest request) {
        Ward ward = findWard(wardId);
        String code = normalizeCode(request.getCode());

        if (!code.equalsIgnoreCase(ward.getCode())) {
            ensureCodeAvailable(code);
        }

        ward.setCode(code);
        ward.setName(normalizeName(request.getName()));
        ward.setDescription(normalizeDescription(request.getDescription()));
        return wardMapper.toResponse(wardRepository.save(ward));
    }

    @Override
    @Transactional(readOnly = true)
    public WardResponse getWardById(Long wardId) {
        return wardMapper.toResponse(findWard(wardId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<WardResponse> getAllWards() {
        return wardRepository.findAll()
                .stream()
                .map(wardMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WardResponse> getActiveWards() {
        return wardRepository.findByActiveTrue()
                .stream()
                .map(wardMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public WardResponse setWardActive(Long wardId, boolean active) {
        Ward ward = findWard(wardId);
        ward.setActive(active);
        return wardMapper.toResponse(wardRepository.save(ward));
    }

    private Ward findWard(Long wardId) {
        return wardRepository.findById(wardId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ward not found with ID: " + wardId));
    }

    private void ensureCodeAvailable(String code) {
        if (wardRepository.existsByCodeIgnoreCase(code)) {
            throw new DuplicateResourceException(
                    "A ward with this code already exists");
        }
    }

    private String normalizeCode(String code) {
        if (code == null || code.isBlank()) {
            throw new BadRequestException("Ward code must not be blank");
        }
        return code.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeName(String name) {
        if (name == null || name.isBlank()) {
            throw new BadRequestException("Ward name must not be blank");
        }
        return name.trim();
    }

    private String normalizeDescription(String description) {
        return description == null || description.isBlank()
                ? null
                : description.trim();
    }
}
