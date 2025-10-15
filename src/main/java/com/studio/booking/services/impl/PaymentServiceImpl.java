package com.studio.booking.services.impl;

import com.studio.booking.dtos.request.PaymentStatusRequest;
import com.studio.booking.dtos.response.PaymentResponse;
import com.studio.booking.entities.Payment;
import com.studio.booking.exceptions.exceptions.AccountException;
import com.studio.booking.mappers.PaymentMapper;
import com.studio.booking.repositories.PaymentRepo;
import com.studio.booking.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepo paymentRepo;
    private final PaymentMapper mapper;

    @Override
    public List<PaymentResponse> getAll() {
        return paymentRepo.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public PaymentResponse getById(String id) {
        Payment payment = paymentRepo.findById(id)
                .orElseThrow(() -> new AccountException("Payment not found with id: " + id));
        return mapper.toResponse(payment);
    }

    @Override
    public PaymentResponse updateStatus(String id, PaymentStatusRequest req) {
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
