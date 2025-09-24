package com.studio.booking.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.studio.booking.enums.AccountRole;
import com.studio.booking.enums.AccountStatus;
import com.studio.booking.enums.AuthType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthRequest {
    @JsonProperty("id")
    private String id;

    @JsonProperty("username")
    private String username;

    @JsonProperty("password")
    private String password;

    @JsonProperty("fullName")
    private String fullName;

    @Email
    @JsonProperty("email")
    private String email;

    @Pattern(regexp = "(84|0[3|5|7|8|9])+(\\d{8})", message = "Invalid phone!")
    @JsonProperty("phoneNumber")
    private String phoneNumber;

    @JsonProperty("identifier")
    private String identifier;

    @JsonProperty("accountRole")
    private AccountRole accountRole;

    @JsonProperty("accountStatus")
    private AccountStatus accountStatus;

    @JsonProperty("identifierType")
    private AuthType authType;
}
