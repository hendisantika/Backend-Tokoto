package com.apiecommerce.tokoto.auth;

import com.apiecommerce.tokoto.gender.Gender;
import com.apiecommerce.tokoto.location.district.District;
import com.apiecommerce.tokoto.location.province.Province;
import com.apiecommerce.tokoto.location.regency.Regency;
import com.apiecommerce.tokoto.location.village.Village;
import com.apiecommerce.tokoto.user.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String phone;
    private Role role;
    private Gender gender;
    private Province provinceName;
    private Regency regencyName;
    private District districtName;
    private Village villageName;
}