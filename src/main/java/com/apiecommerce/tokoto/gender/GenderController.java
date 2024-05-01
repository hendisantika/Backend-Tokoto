package com.apiecommerce.tokoto.gender;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/genders")
@RequiredArgsConstructor
public class GenderController {

    private final GenderService genderService;

    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody GenderRequest request
    ) {
        genderService.create(request);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/list")
    public ResponseEntity<Page<GenderResponse>> findAllGender(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<GenderResponse> response = genderService.findAll(page, size);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}