package com.studio.booking.entities;

import com.studio.booking.enums.TokenType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "verify_token")
public class VerifyToken {
    @Id
    @Column(name = "token_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @Column(name = "expiryDate", nullable = false)
    private LocalDateTime expiryDate;

    @Column(name = "is_verified")
    private Boolean isVerified;

    @Column(name = "token_type")
    private TokenType tokenType;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id", referencedColumnName = "account_id")
    private Account account;
}
