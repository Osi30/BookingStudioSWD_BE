package com.studio.booking.services;

import com.studio.booking.dtos.request.AdminPaymentStatusRequest;
import com.studio.booking.dtos.response.AdminPaymentResponse;

import java.util.List;

public interface AdminPaymentService {
    List<AdminPaymentResponse> getAll();
    AdminPaymentResponse getById(String id);
    AdminPaymentResponse updateStatus(String id, AdminPaymentStatusRequest req);
}
