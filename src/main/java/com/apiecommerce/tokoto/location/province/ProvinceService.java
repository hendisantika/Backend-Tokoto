package com.apiecommerce.tokoto.location.province;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class ProvinceService {

    @Autowired
    private ProvinceRepository provinceRepository;

    @Transactional(readOnly = true)
    public List<Province> findAllProvince() {
        return provinceRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Province findByName(String name) {
        Province province = provinceRepository.findProvinceByName(name);

        if (province == null) {
            throw new IllegalArgumentException("Province with Name : " + name + " Not Found");
        }

        return toProvinceResponse(province);
    }

    private Province toProvinceResponse(Province province) {
        return Province.builder()
                .id(province.getId())
                .name(province.getName())
                .build();
    }
}
