package com.skillup.api.util;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SkillResponse {
    Object result;
    String msg;
}

