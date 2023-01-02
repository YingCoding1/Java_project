package com.skillup.api.dto.in;

import lombok.*;

import javax.validation.constraints.NotNull;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PasswordInDto {
    private String userId;
    @NotNull(message = "oldpassword can not be null")
    private String oldPassword;
    @NotNull(message = "newpassword can not be null")
    private String newPassword;
}
