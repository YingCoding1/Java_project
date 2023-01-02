package com.skillup.api.dto.in;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderInDto {

    private String promotionId;

    private String promotionName;

    private String userId;

    private Integer orderAmount;
}
