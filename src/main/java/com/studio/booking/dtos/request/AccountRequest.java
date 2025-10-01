package com.studio.booking.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.studio.booking.enums.AccountRole;
import com.studio.booking.enums.AccountStatus;
import com.studio.booking.enums.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountRequest {
    @Email
    @JsonProperty("email")
    private String email;

    @JsonProperty("username")
    private String username;

    @JsonProperty("fullName")
    private String fullName;

    @JsonProperty("password")
    private String password;

    @Pattern(regexp = "(84|0[3|5|7|8|9])+(\\d{8})", message = "Invalid phone!")
    @JsonProperty("phoneNumber")
    private String phoneNumber;

    @JsonProperty("role")
    private AccountRole role;

    @JsonProperty("status")
    private AccountStatus status;

    @JsonProperty("userType")
    private UserType userType;
}
