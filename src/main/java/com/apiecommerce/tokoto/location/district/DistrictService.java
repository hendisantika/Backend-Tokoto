package com.apiecommerce.tokoto.location.district;

import com.apiecommerce.tokoto.location.province.Province;
import com.apiecommerce.tokoto.location.province.ProvinceRepository;
import com.apiecommerce.tokoto.location.regency.Regency;
import com.apiecommerce.tokoto.location.regency.RegencyRepository;
import com.apiecommerce.tokoto.location.regency.RegencyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DistrictService {

    @Autowired
    private DistrictRepository districtRepository;

    @Autowired
    private RegencyRepository regencyRepository;

    @Autowired
    private ProvinceRepository provinceRepository;

    @Transactional(readOnly = true)
    public List<DistrictResponse> getAllDistrictsByRegency(String provinceName, String regencyName) {
        Province province = provinceRepository.findProvinceByName(provinceName);
        if (province == null) {
            throw new IllegalArgumentException("Province with Name : " + provinceName + " Not Found");
        }

        Regency regency = regencyRepository.findRegencyByName(regencyName);
        if (regency == null) {
            throw new IllegalArgumentException("Regency with Name : " + regencyName + " Not Found");
        }

        List<District> districts = districtRepository.findAllByRegency(regency);

        return districts.stream()
                .map(this::toDistrictResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DistrictResponse findDistrictByName(String provinceName, String regencyName, String districtName) {
        Province province = provinceRepository.findProvinceByName(provinceName);
        Regency regency = regencyRepository.findRegencyByName(regencyName);

        if (province == null || regency == null) {
            throw new IllegalArgumentException("Province with Name : " + provinceName + " or Regency with Name: " + regencyName + " Not Found!");
        }

        District district = districtRepository.findDistrictByRegencyAndName(regency, districtName);

        if (district == null) {
            throw new IllegalArgumentException("District with Name : " + districtName + " Not Found in Regency: " + regencyName);
        }

        return toDistrictResponse(district);
    }

    @Transactional(readOnly = true)
    public District findDistrictByNames(String provinceName, String regencyName, String districtName) {
        Province province = provinceRepository.findProvinceByName(provinceName);
        Regency regency = regencyRepository.findRegencyByName(regencyName);

        if (province == null || regency == null) {
            throw new IllegalArgumentException("Province with Name : " + provinceName + " or Regency with Name: " + regencyName + " Not Found!");
        }

        District district = districtRepository.findDistrictByRegencyAndName(regency, districtName);

        if (district == null) {
            throw new IllegalArgumentException("District with Name : " + districtName + " Not Found in Regency: " + regencyName);
        }

        return district;
    }

    private DistrictResponse toDistrictResponse(District district) {
        return DistrictResponse.builder()
                .id(district.getId())
                .regency(district.getRegency().getName())
                .name(district.getName())
                .build();
    }
}
