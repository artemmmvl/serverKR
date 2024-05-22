package org.example.controller.dao;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
public class RegReq {
    String name;
    String email;
    String password;
    String city;
    String aboutMe;
    String gender;
    String tg;
    String vk;
    MultipartFile img;


    Long birthday;
}
