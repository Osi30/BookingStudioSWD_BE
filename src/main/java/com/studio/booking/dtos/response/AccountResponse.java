package com.studio.booking.dtos.response;

import com.studio.booking.enums.AccountRole;
import com.studio.booking.enums.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountResponse {
    private String id;
    private String email;
    private String username;
    private String fullName;
    private String phoneNumber;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private AccountRole accountRole;
    private AccountStatus status;
}
