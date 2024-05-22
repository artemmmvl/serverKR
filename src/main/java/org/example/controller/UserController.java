package org.example.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.example.controller.dao.*;
import org.example.entity.UserDao;
import org.example.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;
    private final Gson gson;


    public UserController(UserService userService, Gson gson) {
        this.userService = userService;
        this.gson = gson;
    }


    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id){
        return new ResponseEntity<>(userService.findUserById(id), HttpStatus.OK);
    }
    @GetMapping("/favorites")
    public ResponseEntity<?> getFavorites(@RequestHeader("Authorization") String token){
        return new ResponseEntity<>(userService.getFavorites(token.substring(7)), HttpStatus.OK);
    }
    @DeleteMapping("/favorites/{id}")
    public ResponseEntity<?> deleteFavorites(@RequestHeader("Authorization") String token, @PathVariable Long id){
        List<UserDao> users=userService.deleteFavorites(token.substring(7), id);
        if (users!=null){
            return new ResponseEntity<>(users, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(ErrorResponse.builder().message("Error").build(), HttpStatus.BAD_REQUEST);
        }

    }
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/register")
    public ResponseEntity<?> save( @ModelAttribute RegReq regReq ){
        System.out.println(regReq.getImg().getOriginalFilename());
        AuthenticationResponse authenticationResponse=userService.save(regReq);
        if(authenticationResponse==null){
            System.out.println("{ message: \"username already exists\" }");
            return new ResponseEntity<>(gson.fromJson("{ message: \"Пользователь с такой почтой уже существует\" }", JsonObject.class), HttpStatus.BAD_REQUEST);

        }
        else {
            return new ResponseEntity<>(authenticationResponse, HttpStatus.CREATED);
        }

    }
//    @CrossOrigin(origins = "http://localhost:3000", methods = {RequestMethod.GET, RequestMethod.OPTIONS, RequestMethod.HEAD})
    @GetMapping("/users/me")
    public ResponseEntity<?> getMe(@RequestHeader("Authorization") String token){

        return ResponseEntity.ok()
                .body(userService.getUserByToken(token.substring(7)));

//        return new ResponseEntity<>(userService.getUserByToken(token.substring(7)), HttpStatus.OK);
    }

    @CrossOrigin(origins = "http://localhost:3000", methods = {RequestMethod.GET, RequestMethod.OPTIONS, RequestMethod.HEAD})
    @GetMapping("/random-users")
    public ResponseEntity<?> getRandomUser(@RequestHeader("Authorization") String token, Integer limit){

        return ResponseEntity.ok()
                .body(userService.getRandomUsersByToken(token.substring(7), limit));
    }
    @CrossOrigin(origins = "http://localhost:3000", methods = {RequestMethod.GET, RequestMethod.OPTIONS, RequestMethod.HEAD})
    @PostMapping("/users/like")
    public ResponseEntity<?> likeUser(@RequestHeader("Authorization") String token, @RequestBody UserId userId){
        int error=userService.likeUser(token.substring(7), userId.getId());
        if(error==0){
            return ResponseEntity.ok()
                    .body(ErrorResponse.builder().message("successfully").build());
        }
        else if(error==2) {
            return ResponseEntity.badRequest().body(ErrorResponse.builder().message("Пользователь не найден").build());
        }
        else {
            return ResponseEntity.badRequest().body(ErrorResponse.builder().message("Ошибка").build());

        }


    }
    @PostMapping("/users/skip")
    public ResponseEntity<?> skip(@RequestHeader("Authorization") String token, @RequestBody UserId userId){
        int error=userService.skipUser(token.substring(7), userId.getId());
        if(error==0){
            return ResponseEntity.ok()
                    .body(ErrorResponse.builder().message("successfully").build());
        }
        else if(error==2) {
            return ResponseEntity.badRequest().body(ErrorResponse.builder().message("Пользователь не найден").build());
        }
        else {
            return ResponseEntity.badRequest().body(ErrorResponse.builder().message("Ошибка").build());

        }

    }
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/authenticate")
    public ResponseEntity<?> auth(@RequestBody AuthRequest authRequest ){

        AuthenticationResponse authenticationResponse=userService.auth(authRequest.getEmail(), authRequest.getPassword());
        if (authenticationResponse.getError()==1){
            return new ResponseEntity<>(ErrorResponse.builder().message("Неверный логин или пароль").build(), HttpStatus.UNAUTHORIZED);

        }
        else {
            return new ResponseEntity<>(authenticationResponse, HttpStatus.OK);
        }


    }
//    @DeleteMapping("/users")
//    public ResponseEntity<?> delete(Long id){
//        userService.delete(id);
//        return new ResponseEntity<>( HttpStatus.OK);
//    }
    @PatchMapping("/users")
    public ResponseEntity<?> change(@ModelAttribute RegReq regReq, @RequestHeader("Authorization") String token){
        boolean check=userService.change(regReq, token.substring(7));
        if(check){
            return new ResponseEntity<>(ErrorResponse.builder().message("success").build(), HttpStatus.OK);

        }
        else {
            return new ResponseEntity<>(ErrorResponse.builder().message("ошибка").build(), HttpStatus.BAD_REQUEST);
        }
    }
}
