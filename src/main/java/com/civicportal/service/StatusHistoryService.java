package com.civicportal.service;

import com.civicportal.entity.ComplaintStatusHistory;

import java.util.List;

public interface StatusHistoryService {
    List<ComplaintStatusHistory> getHistoryForComplaint(Long complaintId);
}