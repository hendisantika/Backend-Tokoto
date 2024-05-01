package com.apiecommerce.tokoto.gender;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GenderService {

    @Autowired
    private GenderRepository genderRepository;

    @Transactional
    public GenderResponse create(GenderRequest request) {
        Gender gender = new Gender();
        gender.setName(request.getName());
        genderRepository.save(gender);

        return toGenderResponse(gender);
    }

    @Transactional
    public GenderResponse update(Integer id, GenderRequest request) {
        Gender gender = genderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Gender With ID : " + id + " Not Found!"));
        gender.setName(request.getName());
        genderRepository.save(gender);

        return toGenderResponse(gender);
    }

    @Transactional(readOnly = true)
    public GenderResponse get(Integer id) {
        Gender gender = genderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,  "Gender With ID : " + id + " Not Found!"));
        return toGenderResponse(gender);
    }

    @Transactional(readOnly = true)
    public Page<GenderResponse> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Gender> genderPage = genderRepository.findAll(pageable);
        return genderPage.map(this::toGenderResponse);
    }

    private GenderResponse toGenderResponse(Gender gender) {
        return GenderResponse.builder()
                .id(gender.getId())
                .name(gender.getName())
                .build();
    }

    public Gender getGenderByName(String name) {
        Optional<Gender> optionalGender = genderRepository.findByName(name);
        return optionalGender.orElse(null);
    }

    public Gender getDefaultGender() {
        Optional<Gender> defaultGender = genderRepository.findByName("Lainnya");

        return defaultGender.orElseThrow(() -> new RuntimeException("Default gender not found. Handle this case accordingly."));
    }

    public List<Gender> getAllGenders() {
        return genderRepository.findAll();
    }
}
