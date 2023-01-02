package com.skillup.api.dto.out;

import lombok.*;

import java.time.LocalDateTime;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PromotionOutDto {
    String promotionId;

    String promotionName;

    String commodityId;

    Integer originalPrice;

    Integer promotionalPrice;

    LocalDateTime startTime;

    LocalDateTime endTime;

    Integer status;

    Long totalStock;

    Long availableStock;

    Long lockStock;

    String imageUrl;
}
