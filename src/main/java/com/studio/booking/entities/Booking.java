package com.studio.booking.entities;

import com.studio.booking.enums.*;
import com.studio.booking.utils.GenerateUtil;
import com.studio.booking.utils.Validation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "booking")
public class Booking {
    @Id
    @Column(name = "booking_id", length = 10)
    private String id;

    @CreationTimestamp
    @Column(name = "booking_date")
    private LocalDateTime bookingDate;

    @UpdateTimestamp
    @Column(name = "update_date")
    private LocalDateTime updateDate;

    @Column(name = "note")
    private String note;

    @Column(name = "total")
    private Double total;

    @Column(name = "status")
    private BookingStatus status;

    @Column(name = "booking_type")
    private BookingType bookingType;

    @Column(name = "phone_number")
    private String phoneNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id", referencedColumnName = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "studio_type_id", referencedColumnName = "studio_type_id")
    private StudioType studioType;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private List<StudioAssign> studioAssigns;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private List<Payment> payments;

    @PrePersist
    public void generateId() {
        this.id = GenerateUtil.generateRandomWords(10);
    }

    public Double getRefundPrice() {
        return this.studioAssigns.stream()
                .filter(s -> s.getStatus().equals(AssignStatus.CANCELLED))
                .mapToDouble(s -> s.getStudioAmount() + s.getServiceAmount())
                .sum();
    }

    public Boolean isComplete() {
        return isCompleteAssign() && isCompletePayment();
    }

    private Boolean isCompletePayment() {
        return isPaymentTypeCompleted(PaymentType.REFUND_PAYMENT)
                && isPaymentTypeCompleted(PaymentType.ADDITION_PAYMENT)
                && isCustomerCompletePayment();
    }

    private Boolean isCustomerCompletePayment() {
        // Case for pay full
        if (this.bookingType.equals(BookingType.PAY_FULL)) {
            // Have one payment full success
            Payment payment = this.payments.stream()
                    .filter(p -> p.getPaymentType().equals(PaymentType.FULL_PAYMENT)
                            && p.getStatus().equals(PaymentStatus.SUCCESS))
                    .findFirst().orElse(null);
            return payment != null;
        // Case for pay deposit
        } else {
            // Have one deposit and one final payment
            List<Payment> payments = this.payments.stream()
                    .filter(p -> p.getStatus().equals(PaymentStatus.SUCCESS)
                            && (p.getPaymentType().equals(PaymentType.DEPOSIT)
                            || p.getPaymentType().equals(PaymentType.FINAL_PAYMENT)))
                    .toList();
            return payments.size() == 2;
        }
    }

    private Boolean isPaymentTypeCompleted(PaymentType paymentType) {
        List<Payment> unSuccessPayments = this.payments
                .stream().filter(p -> p.getPaymentType().equals(paymentType)
                        && !p.getStatus().equals(PaymentStatus.SUCCESS))
                .toList();
        return !Validation.isValidCollection(unSuccessPayments);
    }

    private Boolean isCompleteAssign() {
        List<StudioAssign> assigns = this.studioAssigns
                .stream().filter(s
                        -> s.getStatus().equals(AssignStatus.COMING_SOON)
                        || s.getStatus().equals(AssignStatus.IS_HAPPENING)
                )
                .toList();

        return !Validation.isValidCollection(assigns);
    }
}
