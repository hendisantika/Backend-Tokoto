package com.apiecommerce.tokoto.categories;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoriesRequest {

    @NotBlank(message = "Nama category tidak boleh kosong")
    private String nameCategory;

}