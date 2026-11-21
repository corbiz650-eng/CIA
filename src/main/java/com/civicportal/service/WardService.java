package com.civicportal.service;

import com.civicportal.dto.ward.WardRequest;
import com.civicportal.dto.ward.WardResponse;

import java.util.List;

public interface WardService {

    WardResponse createWard(WardRequest request);

    WardResponse updateWard(Long wardId, WardRequest request);

    WardResponse getWardById(Long wardId);

    List<WardResponse> getAllWards();

    List<WardResponse> getActiveWards();

    WardResponse setWardActive(Long wardId, boolean active);
}
