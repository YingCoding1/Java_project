package com.skillup.api.dto.out;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserOutDto {
    private String userId;
    private String userName;
}
