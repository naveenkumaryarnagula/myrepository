package com.ewig.user.Controller;

import com.ewig.celeb.Document.Celebrity;
import com.ewig.user.DTO.ChangepwdDTO;
import com.ewig.user.DTO.OtpDTO;
import com.ewig.user.DTO.UserDTO;
import com.ewig.user.Document.User;
import com.ewig.user.Document.UserInfo;
import com.ewig.user.Repository.UserInfoRepository;
import com.ewig.user.Repository.UserRepository;
import com.ewig.user.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.bind.ValidationException;
import java.io.IOException;
import java.util.List;


@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private UserRepository userRepository;
    private static final long otp_valid_duration = 5 * 60 * 1000;

    @RequestMapping(value = "/users/registration", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public String user(@RequestPart("multipartFile") MultipartFile multipartFile, @RequestPart UserDTO userDTO) throws ValidationException, IOException {

        User celeb1 = userService.createCeleb(userDTO, multipartFile);
        if (!celeb1.getId().isEmpty()) {
            userService.sms(celeb1);
            userService.mail(celeb1);
        }
        return "user is added";
    }


    @PostMapping("user/verify/phonenumber")
    public ResponseEntity verifyPhoneNumber(@RequestBody OtpDTO otpDTO) throws ValidationException {


        System.out.println(otpDTO.getPhoneNumber());
        User user = userRepository.finduserIdInUserInfo(otpDTO.getPhoneNumber());
        System.out.println(user.getId());
        if (user != null) {
            System.out.println(user.getId());
            UserInfo userInfo = userService.getUserInfoByUserId(user.getId());
            System.out.println(userInfo);
            if (userInfo.getIsValidate() < 1) {
                if (userInfo.getIsExpire() < 1) {
                    if (userInfo.getOtp().equals(otpDTO.getOtp())) {
                        userService.updateValidate(userInfo.getUserInfoId());
                        return ResponseEntity.ok("user is validated");
                    } else
                        return ResponseEntity.ok("otp is in correct");
                } else
                    return ResponseEntity.ok("otp is inactive");
            } else
                return ResponseEntity.ok("user is already validated");
        } else
            return ResponseEntity.ok("phone number is not existed");
    }

    @PostMapping("user/verify/email")
    public ResponseEntity verifyUserEmail(@RequestBody OtpDTO otpDTO) throws ValidationException {


        System.out.println(otpDTO.getEmail());
        User celeb = userRepository.finduserIdInUser(otpDTO.getEmail());
        System.out.println(celeb.getId());
        if (celeb != null) {
            System.out.println(celeb.getId());
            UserInfo userInfo = userService.getUserByUserId(celeb.getId());
            System.out.println(userInfo);
            if (userInfo.getIsValidate() < 1) {
                if (userInfo.getIsExpire() < 1) {
                    if (userInfo.getOtp().equals(otpDTO.getOtp())) {
                        userService.updateValidate(userInfo.getUserInfoId());
                        return ResponseEntity.ok("user is validated");
                    } else
                        return ResponseEntity.ok("otp is in correct");
                } else
                    return ResponseEntity.ok("otp is expired");
            } else
                return ResponseEntity.ok("mail is already validated");
        } else
            return ResponseEntity.ok("mail is not existed");
    }

    @PostMapping("/resendOtp")
    public ResponseEntity resendOtp(@RequestBody OtpDTO otpDTO) throws ValidationException {
        if (otpDTO.getEmail() == null) {
            User user = userRepository.finduserIdInUserInfo(otpDTO.getPhoneNumber());
            if (user.getId() != null) {
                userService.sms(user);
            }
        } else {
            User user = userRepository.finduserIdInUser(otpDTO.getEmail());
            if (user.getId() != null) {
                userService.mail(user);
            }
        }

        return ResponseEntity.ok("resent");
    }

    @PostMapping("/createPassword")
    public ResponseEntity<String> createPassword(@RequestBody UserDTO userDTO) {
        userService.createPassword(userDTO);
        return ResponseEntity.ok("password created");
    }

    @PostMapping("/loginuser")
    public List<Celebrity> login() {
        return userService.login();
    }

    @GetMapping("/user/logout")
    public ResponseEntity<String> logout() {
        //return ResponseEntity.ok("<h1> you are logged out from celeb app</h1>");
        SecurityContextHolder.getContext().setAuthentication(null);
        return ResponseEntity.ok("<h1> you are logged out from celeb app</h1>");
    }

    @RequestMapping(value = "/user/updateProfile", method = RequestMethod.PUT, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public String updateProfile(@RequestPart("multipartFile") MultipartFile multipartFile, @RequestPart UserDTO userDTO) throws IOException {
        if (userDTO.getEmail() != null) {
            return userService.updateProfile(multipartFile, userDTO);
        } else return "email is not exist";
    }

    @PutMapping("/users/changepwd")
    public String chagepwd(@RequestBody ChangepwdDTO changepwdDTO) {
        return userService.changePassword(changepwdDTO);
    }

    @PostMapping("/celebrities/{celeb_id}/follow")
    public String following(@PathVariable("celeb_id") String celeb_id, @RequestBody UserDTO userDTO){
        return userService.followCelebrity(celeb_id, userDTO);
    }

    @PostMapping("/celebrities/{celeb_id}/unfollow")
    public String unfollow(@PathVariable("celeb_id") String celeb_id, @RequestBody UserDTO userDTO){
        return userService.unfollowCelebrity(celeb_id, userDTO);
    }

    @PostMapping("/celebrities/following")
    public List<List<Celebrity>> getList(@RequestBody UserDTO userDTO){
        return userService.getFollowList(userDTO);
    }
}

