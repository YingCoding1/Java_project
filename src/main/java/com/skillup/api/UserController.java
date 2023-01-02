package com.skillup.api;

import com.skillup.api.util.ResponseUtil;
import com.skillup.api.util.SkillResponse;
import com.skillup.api.dto.in.PasswordInDto;
import com.skillup.api.dto.in.UserInDto;
import com.skillup.api.dto.out.UserOutDto;
import com.skillup.domain.user.UserDomain;
import com.skillup.domain.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/account")
public class UserController {
    //UserService userService = new UserService();
    @Autowired
    UserService userService;
    @PostMapping()
    public ResponseEntity<SkillResponse> createNewUser(@RequestBody UserInDto userInDto){
        UserDomain userDomain = createUserDomain(userInDto);
        UserDomain savedUserDomain = null;
        try{
            savedUserDomain = userService.registerUser(userDomain);
        }catch (Exception e){
            return ResponseEntity.status(ResponseUtil.BAD_REQUEST).body(SkillResponse.builder()
                    .msg(ResponseUtil.USER_EXIST)
                    .build()) ;
        }
        SkillResponse skillResponse = SkillResponse.builder()
                .result(createUserOutDto(savedUserDomain))
                .build();
        return ResponseEntity.status(ResponseUtil.SUCCESS).body(skillResponse) ;
    }
    @GetMapping("/id/{id}")
    public ResponseEntity<SkillResponse> getuserById(@PathVariable("id") String id){
        UserDomain userDomain = userService.getUserById(id);
        if(Objects.nonNull(userDomain)){
            return ResponseEntity.status(ResponseUtil.SUCCESS).body(SkillResponse.builder().result(userDomain).build());
        }else{
            return ResponseEntity.status(ResponseUtil.BAD_REQUEST).body(SkillResponse.builder().msg(String.format(ResponseUtil.USER_ID_WRONG,id)).build());
        }
    }
    @GetMapping("/name/{name}")
    public ResponseEntity<SkillResponse> getuserByName(@PathVariable("name") String name){
        UserDomain userDomain = userService.getUserByName(name);
        if(Objects.nonNull(userDomain)){
            return ResponseEntity.status(ResponseUtil.SUCCESS).body(SkillResponse.builder().result(userDomain).build());
        }else{
            return ResponseEntity.status(ResponseUtil.BAD_REQUEST).body(SkillResponse.builder().msg(String.format(ResponseUtil.USER_NAME_WRONG,name)).build());
        }
    }
    @PostMapping("/login")
    public ResponseEntity<SkillResponse> login(@RequestBody UserInDto userInDto){
        //1.get UserDomain By UserName
        UserDomain userDomain = userService.getUserByName(userInDto.getUserName());
        if(Objects.isNull(userDomain)){
            return ResponseEntity.status(ResponseUtil.BAD_REQUEST).body(SkillResponse.builder().msg(String.format(ResponseUtil.USER_NAME_WRONG,userInDto.getUserName())).build());
        }
        //2. check password match
        if(!userDomain.getPassword().equals(userInDto.getPassword())){
            return ResponseEntity.status(ResponseUtil.BAD_REQUEST).body(SkillResponse.builder().msg(ResponseUtil.PASSWORD_NOT_MATCH).build());
        }
        //3. LOGIN successfully
        return ResponseEntity.status(ResponseUtil.SUCCESS).body(SkillResponse.builder().result(createUserOutDto(userDomain)).build());

    }
    @PutMapping("/password")
    public ResponseEntity<SkillResponse> updatePassword(@Validated @RequestBody PasswordInDto passwordInDto){
        //1.find user by id
        UserDomain userDomain = userService.getUserById(passwordInDto.getUserId());
        if(Objects.isNull(userDomain)){
            return ResponseEntity.status(ResponseUtil.BAD_REQUEST).body(SkillResponse.builder().msg(String.format(ResponseUtil.USER_NAME_WRONG,passwordInDto.getUserId())).build());
        }
        //2.check old password
        if(!userDomain.getPassword().equals(passwordInDto.getOldPassword())){
            return ResponseEntity.status(ResponseUtil.BAD_REQUEST).body(SkillResponse.builder().msg(ResponseUtil.PASSWORD_NOT_MATCH).build());
        }
        //3.update new password
        userDomain.setPassword(passwordInDto.getNewPassword());
        UserDomain savedUserDomain = userService.updateUserDomain(userDomain);
        return ResponseEntity.status(HttpStatus.OK).body(SkillResponse.builder().result(createUserOutDto(savedUserDomain)).build());
    }
    private UserDomain createUserDomain(UserInDto userInDto){
        return UserDomain.builder()
                .userId(UUID.randomUUID().toString())
                .userName(userInDto.getUserName())
                .password(userInDto.getPassword())
                .build();
    }
    private UserOutDto createUserOutDto(UserDomain userDomain){
        return UserOutDto.builder()
                .userId(userDomain.getUserId())
                .userName(userDomain.getUserName())
                .build();
    }
}
