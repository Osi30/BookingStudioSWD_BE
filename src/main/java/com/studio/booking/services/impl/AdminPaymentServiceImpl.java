package com.studio.booking.services.impl;

import com.studio.booking.dtos.request.AdminPaymentStatusRequest;
import com.studio.booking.dtos.response.AdminPaymentResponse;
import com.studio.booking.entities.Payment;
import com.studio.booking.exceptions.exceptions.AccountException;
import com.studio.booking.mappers.AdminPaymentMapper;
import com.studio.booking.repositories.PaymentRepo;
import com.studio.booking.services.AdminPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminPaymentServiceImpl implements AdminPaymentService {
    private final PaymentRepo paymentRepo;
    private final AdminPaymentMapper mapper;

    @Override
    public List<AdminPaymentResponse> getAll() {
        return paymentRepo.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public AdminPaymentResponse getById(String id) {
        Payment payment = paymentRepo.findById(id)
                .orElseThrow(() -> new AccountException("Payment not found with id: " + id));
        return mapper.toResponse(payment);
    }

    @Override
    public AdminPaymentResponse updateStatus(String id, AdminPaymentStatusRequest req) {
        Payment payment = paymentRepo.findById(id)
                .orElseThrow(() -> new AccountException("Payment not found with id: " + id));

        if (req.getStatus() == null) {
            throw new IllegalArgumentException("Payment status cannot be null");
        }

        // Không cho admin đổi sang cùng một trạng thái
        if (payment.getStatus().equals(req.getStatus())) {
            throw new IllegalArgumentException("Payment already has this status: " + req.getStatus());
        }

        payment.setStatus(req.getStatus());
        paymentRepo.save(payment);
        return mapper.toResponse(payment);
    }
}
