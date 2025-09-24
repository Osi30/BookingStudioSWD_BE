package com.studio.booking.dtos.dto;

import com.studio.booking.entities.Account;
import com.studio.booking.enums.AccountIdentifier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountIdentity {
    private String identity;
    private Boolean isInactive;
    private Account processAccount;
    private AccountIdentifier identifier;
}
