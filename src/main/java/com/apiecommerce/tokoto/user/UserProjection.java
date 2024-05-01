package com.apiecommerce.tokoto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProjection {

    private String email;
    private String firstname;
    private String lastname;
    private String phone;
    private String gender;
    private Role role;
    private String province;
    private String regency;
    private String district;
    private String village;
    private String profileName;
}
