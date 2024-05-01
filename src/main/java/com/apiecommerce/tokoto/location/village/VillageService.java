package com.apiecommerce.tokoto.location.village;

import com.apiecommerce.tokoto.location.district.District;
import com.apiecommerce.tokoto.location.district.DistrictRepository;
import com.apiecommerce.tokoto.location.province.Province;
import com.apiecommerce.tokoto.location.province.ProvinceRepository;
import com.apiecommerce.tokoto.location.regency.Regency;
import com.apiecommerce.tokoto.location.regency.RegencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VillageService {

    @Autowired
    private VillageRepository villageRepository;

    @Autowired
    private DistrictRepository districtRepository;

    @Autowired
    private RegencyRepository regencyRepository;

    @Autowired
    private ProvinceRepository provinceRepository;

    @Transactional(readOnly = true)
    public List<VillageResponse> getAllVillagesByDistrict(String provinceName, String regencyName, String districtName) {
        Province province = provinceRepository.findProvinceByName(provinceName);
        if (province == null) {
            throw new IllegalArgumentException("Province with Name : " + provinceName + " Not Found");
        }

        Regency regency = regencyRepository.findRegencyByName(regencyName);
        if (regency == null) {
            throw new IllegalArgumentException("Regency with Name : " + regencyName + " Not Found");
        }

        District district = districtRepository.findDistrictByRegencyAndName(regency, districtName);
        if (district == null) {
            throw new IllegalArgumentException("District with Name : " + districtName + " Not Found in Regency: " + regencyName);
        }

        List<Village> villages = villageRepository.findAllByDistrict(district);

        return villages.stream()
                .map(this::toVillageResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VillageResponse getVillageByDistrictAndName(String provinceName, String regencyName, String districtName, String name) {
        Province province = provinceRepository.findProvinceByName(provinceName);
        if (province == null) {
            throw new IllegalArgumentException("Province with Name : " + provinceName + " Not Found");
        }

        Regency regency = regencyRepository.findRegencyByName(regencyName);
        if (regency == null) {
            throw new IllegalArgumentException("Regency with Name : " + regencyName + " Not Found");
        }

        District district = districtRepository.findDistrictByRegencyAndName(regency, districtName);
        if (district == null) {
            throw new IllegalArgumentException("District with Name : " + districtName + " Not Found in Regency: " + regencyName);
        }

        Village village = villageRepository.findVillageByDistrictAndName(district, name);
        if (village == null) {
            throw new IllegalArgumentException("Village with Name : " + name + " Not Found in District: " + districtName);
        }

        return toVillageResponse(village);
    }

    @Transactional(readOnly = true)
    public Village getVillageByDistrictAndNames(String provinceName, String regencyName, String districtName, String name) {
        Province province = provinceRepository.findProvinceByName(provinceName);
        if (province == null) {
            throw new IllegalArgumentException("Province with Name : " + provinceName + " Not Found");
        }

        Regency regency = regencyRepository.findRegencyByName(regencyName);
        if (regency == null) {
            throw new IllegalArgumentException("Regency with Name : " + regencyName + " Not Found");
        }

        District district = districtRepository.findDistrictByRegencyAndName(regency, districtName);
        if (district == null) {
            throw new IllegalArgumentException("District with Name : " + districtName + " Not Found in Regency: " + regencyName);
        }

        Village village = villageRepository.findVillageByDistrictAndName(district, name);
        if (village == null) {
            throw new IllegalArgumentException("Village with Name : " + name + " Not Found in District: " + districtName);
        }

        return village;
    }

    private VillageResponse toVillageResponse(Village village) {
        return VillageResponse.builder()
                .id(village.getId())
                .district(village.getDistrict().getName())
                .name(village.getName())
                .build();
    }
}
