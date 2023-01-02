package com.skillup.api.dto.in;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInDto {

    private String userName;
    private String password;
}
