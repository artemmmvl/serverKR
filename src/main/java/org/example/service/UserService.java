package org.example.service;

import jakarta.transaction.Transactional;
import org.example.config.JwtService;
import org.example.controller.dao.AuthenticationResponse;
import org.example.controller.dao.RegReq;
import org.example.entity.Role;
import org.example.entity.User;
import org.example.entity.UserDao;
import org.example.repo.UserRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;

@Service
//@AllArgsConstructor
public class UserService  {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CityService cityService;
    @Value("${upload-directory}")
    private String UPLOAD_DIRECTORY;


    public UserService(UserRepo userRepo, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager, CityService cityService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.cityService = cityService;
    }

    public List<User> findAll(){
        return userRepo.findAll();
    }
    public UserDao getUserByToken(String token){
        User user=userRepo.findByEmail(jwtService.extractUserName(token));





        return  UserDao.builder().
                id(user.getId()).
                firstname(user.getFirstname()).
                birthday(user.getBirthday()).
                gender(user.getGender()).
                email(user.getEmail()).
                aboutMe(user.getAboutMe()).
                city(user.getCity().getName()).
                vk(user.getVk()).
                tg(user.getTg()).
                img(user.getImgPath()).
                build();

    }

    public UserDao findUserById(Long id){
        User user=userRepo.getReferenceById(id);
        return  UserDao.builder().
                id(user.getId()).
                firstname(user.getFirstname()).
                birthday(user.getBirthday()).
                gender(user.getGender()).
                email(user.getEmail()).
                aboutMe(user.getAboutMe()).
                city(user.getCity().getName()).
                vk(user.getVk()).
                tg(user.getTg()).
                img(user.getImgPath()).
                build();
    }
    public void delete(Long id){
        userRepo.deleteById(id);
    }
    public AuthenticationResponse auth(String email, String password){
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            email,
                            password
                    )
            );
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return AuthenticationResponse.builder().token(null).error(1).build();
        }

        var user=userRepo.findByEmail(email);
        var jwtToken=jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(jwtToken).error(0).build();

    }


    public AuthenticationResponse save(RegReq userReg)  {
        if(userRepo.findByEmail(userReg.getEmail())!=null){
            return null;
        }
        User user=new User();
        user.setEmail(userReg.getEmail());
        user.setFirstname(userReg.getName());
        MultipartFile img=userReg.getImg();
        String path="";
        try {
            if(img.getSize()!=0) {
                String fileName = UUID.randomUUID().toString() + "." + img.getOriginalFilename();
                path="img/" + fileName;
                File file1 = new File(UPLOAD_DIRECTORY + "img/" + fileName);
                img.transferTo(file1);
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        user.setImgPath(path);
        user.setPassword(passwordEncoder.encode(userReg.getPassword()));

        user.setBirthday(userReg.getBirthday());
        user.setGender(userReg.getGender());
        user.setAboutMe(userReg.getAboutMe().equals("null")?null: userReg.getAboutMe());
        try {
            user.setCity(cityService.getById(Long.parseLong(userReg.getCity())));
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        user.setTg(userReg.getTg());
        user.setVk(userReg.getVk());

        user.setRole(Role.USER);
        userRepo.save(user);
        var jwtToken= jwtService.generateToken(user);
        return AuthenticationResponse.builder().
                token(jwtToken).
                build();
    }


    public boolean change(RegReq userChange, String token) {

        try {

            User user=userRepo.findByEmail(jwtService.extractUserName(token));
            user.setEmail(userChange.getEmail());
            user.setFirstname(userChange.getName());
            MultipartFile img=userChange.getImg();
            String path="";
            try {
                if(userChange.getImg()!=null){
                    if(img.getSize()!=0) {
                        String fileName = UUID.randomUUID().toString() + "." + img.getOriginalFilename();
                        path="img/" + fileName;
                        File file1 = new File(UPLOAD_DIRECTORY + "img/" + fileName);
                        img.transferTo(file1);
                        user.setImgPath(path);
                    }
                }

            }
            catch (Exception e){
                System.out.println(e.getMessage());
            }
            if(!userChange.getPassword().equals("null")){
                user.setPassword(passwordEncoder.encode(userChange.getPassword()));
            }


            user.setBirthday(userChange.getBirthday());
            user.setGender(userChange.getGender());
            user.setAboutMe(userChange.getAboutMe());
            try {
                user.setCity(cityService.getById(Long.parseLong(userChange.getCity())));
            }
            catch (Exception e){
                System.out.println(e.getMessage());
            }
            user.setTg(userChange.getTg());
            user.setVk(userChange.getVk());

            userRepo.save(user);
        }
        catch (Exception e){
            return false;
        }
        return true;



    }

    public List<UserDao> getRandomUsersByToken(String token, int limit) {
        User user=userRepo.findByEmail(jwtService.extractUserName(token));
        List<User> users=userRepo.findAll();
        Random random = new Random();

        int size=users.size();
        List<User> skip=user.getSkip();
        List<User> favorites=user.getFavorites();
        List<UserDao> randomUsers=new ArrayList<>();
        List<User> viewedUsers=new ArrayList<>();


        for (int i =0; i<limit;i++){

            while (true){
                int randomInt=random.nextInt(size);
                User randomUser=users.get(randomInt);
                if(viewedUsers.size()==size){
                    break;
                }
                if(!viewedUsers.contains(randomUser)){
                    viewedUsers.add(randomUser);
                }
                else {
                    continue;
                }
                if(!skip.contains(randomUser) &&
                        !(favorites.contains(randomUser))
                        &&(!Objects.equals(randomUser.getId(), user.getId()))
                ){
                    randomUsers.add(UserDao.builder().
                            firstname(randomUser.getFirstname()).
                            aboutMe(randomUser.getAboutMe()).
                            birthday(randomUser.getBirthday()).
                            city(randomUser.getCity().getName()).
                            img(randomUser.getImgPath()).
                            id(randomUser.getId()).build());

                    break;
                }


            }

        }
        return randomUsers;

    }
    public int skipUser(String token, Long id) {
        User userMe;
        try {
            userMe=userRepo.findByEmail(jwtService.extractUserName(token));
        }
        catch (Exception e){
            return 1;
        }

        List<User> skip=userMe.getSkip();
        Optional<User> skipUserOptional=userRepo.findById(id);
        User skipUser;


        if(skipUserOptional.isEmpty()){
            return 2;
        }
        else {
            skipUser=skipUserOptional.get();
        }
        if(skip.contains(skipUser)){
            return 0;
        }
        else {
            skip.add(skipUser);
        }
         userMe.setSkip(skip);
        userRepo.save(userMe);
        return 0;



    }

    public int likeUser(String token, Long id) {
        User userMe;
        try {
            userMe=userRepo.findByEmail(jwtService.extractUserName(token));
        }
        catch (Exception e){
            return 1;
        }


        List<User> favorites=userMe.getFavorites();

        Optional<User> likeUserOptional=userRepo.findById(id);
        User likeUser;


        if(likeUserOptional.isEmpty()){
            return 2;
        }
        else {
            likeUser=likeUserOptional.get();
        }



        if(favorites.contains(likeUser)){
            return 0;
        }
        favorites.add(likeUser);


       userMe.setFavorites(favorites);

        userRepo.save(userMe);
        return 0;

    }

    public List<UserDao> getFavorites(String token) {
        User userMe;
        try {
            userMe=userRepo.findByEmail(jwtService.extractUserName(token));
        }
        catch (Exception e){
            return null;
        }
        List<UserDao> result=new ArrayList<>();
        List<User> favorites=userMe.getFavorites();
        for(int i=0; i<favorites.size();i++){
            result.add(UserDao.builder().
                    firstname(favorites.get(i).getFirstname()).
                    img(favorites.get(i).getImgPath()).
                    id(favorites.get(i).getId()).
                    birthday(favorites.get(i).getBirthday()).
                    city(favorites.get(i).getCity().getName()).build());
        }
        return result;
    }

    public List<UserDao> deleteFavorites(String token, Long id) {

        User userMe;
        try {
            userMe=userRepo.findByEmail(jwtService.extractUserName(token));
        }
        catch (Exception e){
            return null;
        }
        List<User> favorites=userMe.getFavorites();
        Optional<User> deleteUser=userRepo.findById(id);
        if (deleteUser.isEmpty()){
            return null;
        }
        favorites.remove(deleteUser.get());
        userMe.setFavorites(favorites);
        List<UserDao> result=new ArrayList<>();
        for(int i=0; i<favorites.size();i++){
            result.add(UserDao.builder().
                    firstname(favorites.get(i).getFirstname()).
                    img(favorites.get(i).getImgPath()).
                    id(favorites.get(i).getId()).
                    birthday(favorites.get(i).getBirthday()).
                    city(favorites.get(i).getCity().getName()).build());
        }
        userRepo.save(userMe);
        return result;

    }
}
