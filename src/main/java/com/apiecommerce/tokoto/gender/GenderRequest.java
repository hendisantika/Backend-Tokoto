package com.apiecommerce.tokoto.gender;

import lombok.*;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenderRequest {
    private Integer id;
    private String name;
}
