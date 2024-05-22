package org.example.controller;

import lombok.AllArgsConstructor;
import org.example.service.CityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class Cont {
    private final CityService cityService;
    @PostMapping("/set-city")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<?> setCity(){
        cityService.setCities();
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }
    @GetMapping("/cities")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<?> getCities(){

        return new ResponseEntity<>(cityService.getAll(), HttpStatus.OK);

    }

}
