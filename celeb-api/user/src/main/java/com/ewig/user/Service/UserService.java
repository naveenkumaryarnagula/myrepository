package com.ewig.user.Service;

import com.ewig.celeb.Document.Celebrity;
import com.ewig.celeb.Repository.CelebrityRepository;
import com.ewig.user.DTO.ChangepwdDTO;
import com.ewig.user.DTO.UserDTO;
import com.ewig.user.Document.Follow;
import com.ewig.user.Document.User;
import com.ewig.user.Document.UserInfo;
import com.ewig.user.Repository.FollowRepository;
import com.ewig.user.Repository.UserInfoRepository;
import com.ewig.user.Repository.UserRepository;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.bind.ValidationException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class UserService {
    private JavaMailSender javaMailSender;

    @Autowired
    public UserService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CelebrityRepository celebrityRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MongoOperations mongoOperations;

    @Autowired
    private FollowRepository followRepository;


    private final String ACCOUNT_SID = "AC7a8e6921eb983cedceb1e0d937415571";

    private final String AUTH_TOKEN = "7a5552ba0555a7eebdf93d48dcccca8a";

    private final String FROM_NUMBER = "+15043275801";

    private static final long otp_valid_duration = 4 * 60 * 1000;

    public User createCeleb(UserDTO userDTO, MultipartFile multipartFile) throws ValidationException, IOException {
        List<String> phoneNumbers = userRepository.findAllPhoneNumber(userDTO.getPhoneNumber());
        if (!phoneNumbers.isEmpty()) {
            throw new ValidationException("phone number already exists");
        }
        List<String> emails = userRepository.findAllEmails(userDTO.getEmail());
        if (!emails.isEmpty()) {
            throw new ValidationException("email id is already exists");
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        User user = new User();
        user.setId(userDTO.getId());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setEmail(userDTO.getEmail());
        user.setLocation(userDTO.getLocation());
        user.setCreateDate(date);
        user.setUpdateDate(date);
        user.setProfile_photo(multipartFile.getOriginalFilename());
        user.setData(new Binary(multipartFile.getBytes()));
        //System.out.println(Base64.getEncoder().encodeToString(multipartFile.getBytes()));
        return userRepository.save(user);
    }

    public UserInfo getUserInfoByUserId(String userId) throws ValidationException {
        System.out.println(userId);
        String ph = "phoneNumber";
        UserInfo userInfoDTO = userInfoRepository.findbyUserId(userId, ph);
        System.out.println(userInfoDTO);
        return userInfoDTO;
    }

    public void updateValidate(String userInfoId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(userInfoId));
        Update update = new Update();
        update.set("isValidate",1);
        mongoTemplate.upsert(query,update,UserInfo.class);

    }

    public void sms(User user) throws ValidationException {
        try {
            int randomPin = (int) (Math.random() * 9000) + 1000;
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
            String msg = "Your OTP - " + randomPin + " please verify this OTP in your Application by Celeb app Team";
            Message message = Message.creator(new PhoneNumber(user.getPhoneNumber()), new PhoneNumber(FROM_NUMBER), msg)
                    .create();


            UserInfo userInfo1 = userInfoRepository.findbyUserId(user.getId(), "phoneNumber");
            if (userInfo1 != null) {
                userInfoRepository.deleteById(userInfo1.getUserInfoId());
            }
            UserInfo userInfo = new UserInfo();
            userInfo.setUserId(user.getId());
            userInfo.setOtp(String.valueOf(randomPin));
            userInfo.setOtpTime(System.currentTimeMillis());
            userInfo.setOtpType("phoneNumber");
            userInfo.setIsExpire(0);
            userInfo.setIsValidate(0);
            userInfoRepository.save(userInfo);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ValidationException("please check the phone number");
        }
    }


    public UserInfo getUserByUserId(String id) {
        System.out.println(id);
        String ph = "Mail";
        UserInfo userInfoDTO = userInfoRepository.findUserId(id, ph);
        return userInfoDTO;
    }


    public void mail(User user) {
        int otpRandomPIN = (int) (Math.random() * 9000) + 1000;
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(user.getEmail());
        mail.setSubject("otp for email verification");
        mail.setText("otp for email verification " + otpRandomPIN + " sent by celeb team");
        javaMailSender.send(mail);


        UserInfo userInfo1 = userInfoRepository.findbyUserId(user.getId(), "Mail");
        if (userInfo1 != null) {
            userInfoRepository.deleteById(userInfo1.getUserInfoId());
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setOtp(String.valueOf(otpRandomPIN));
        userInfo.setOtpTime(System.currentTimeMillis());
        userInfo.setOtpType("Mail");
        userInfo.setIsExpire(0);
        userInfo.setIsValidate(0);
        userInfoRepository.save(userInfo);
    }

    @Scheduled(cron = "0/1 * * * * *")
    public void updateExpiry(){

        Query query = new Query();
        query.addCriteria(Criteria.where("otpTime").lt(System.currentTimeMillis()-otp_valid_duration).exists(true));
        Update update = new Update();
        update.set("isExpire",1);
        mongoTemplate.updateMulti(query, update, UserInfo.class);
    }

    public void createPassword(UserDTO userDTO) {
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(userDTO.getEmail()));
        Update update = new Update();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        update.set("password",encoder.encode(userDTO.getPassword()));
        mongoTemplate.upsert(query, update, User.class);
    }

    public String updateProfile(MultipartFile multipartFile, UserDTO userDTO) throws IOException {
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(userDTO.getEmail()));
        Update update = new Update();
        update.set("firstName",userDTO.getFirstName());
        update.set("lastName",userDTO.getLastName());
        update.set("location",userDTO.getLocation());
        if(!multipartFile.isEmpty()) {
            update.set("profile_photo", multipartFile.getOriginalFilename());
            update.set("data", multipartFile.getBytes());
        }
        mongoTemplate.upsert(query, update, User.class);
        return "user is updated";
    }

    public String changePassword(ChangepwdDTO changepwdDTO) {
        User user = userRepository.findByEmail(changepwdDTO.getEmail());
        if(user== null) {
            return "email not exist";
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodePassword = encoder.encode(changepwdDTO.getNew_password());
        Boolean b = encoder.matches(changepwdDTO.getOld_password(),user.getPassword());
        if(b){
            Query query = new Query();
            query.addCriteria(Criteria.where("password").is(user.getPassword()));
            Update update = new Update();
            update.set("password",encodePassword);
            mongoTemplate.upsert(query, update, User.class);
            return "password changed successfully";
        }
        return "password mismatch";
    }


    public List<Celebrity> login() {
        Query query = new Query();
        query.fields().include("firstName");
        query.fields().include("lastName");
        query.fields().include("profilePic");
        query.fields().include("description");
        query.fields().include("links");
        List<Celebrity> celebrities = mongoOperations.find(query, Celebrity.class);
        return celebrities;
    }

    public String followCelebrity(String celeb_id, UserDTO userDTO) {
        Follow follw = followRepository.findBy(celeb_id, userDTO.getId());
        if(follw== null){
            Follow follow = new Follow();
            follow.setCelebId(celeb_id);
            follow.setUserId(userDTO.getId());
            follow.setIsFollow(true);
            followRepository.save(follow);
            return "user is followed";
        }
        else
        {
            Query query = new Query();
            query.addCriteria(Criteria.where("celebId").is(celeb_id).andOperator(Criteria.where("userId").is(userDTO.getId())));
            Update update = new Update();
            update.set("isFollow", true);
            mongoTemplate.upsert(query, update, Follow.class);
            return "user is followed";
        }
    }

    public String unfollowCelebrity(String celeb_id, UserDTO userDTO) {
        Query query = new Query();
        query.addCriteria(Criteria.where("celebId").is(celeb_id).andOperator(Criteria.where("userId").is(userDTO.getId())));
        Update update = new Update();
        update.set("isFollow", false);
        mongoTemplate.upsert(query, update, Follow.class);
        return "user is unfollowed the celebrity";
    }

    public List<List<Celebrity>> getFollowList(UserDTO userDTO) {

        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userDTO.getId()).andOperator(Criteria.where("isFollow").is(true)));
        query.fields().include("celebId");
        List<Follow> follows = mongoTemplate.find(query, Follow.class);
        List<List<Celebrity>>celebrities = new ArrayList<>();
        for(int i = 0; i<follows.size(); i++) {
            Query query1 = new Query();
            query1.addCriteria(Criteria.where("id").is(follows.get(i).getCelebId()));
            query1.fields().include("firstName");
            query1.fields().include("lastName");
            query1.fields().include("profilePic");
            query1.fields().include("description");
            query1.fields().include("links");
           List<Celebrity> celebrityList = mongoTemplate.find(query1, Celebrity.class);
           celebrities.add(celebrityList);
        }
        return celebrities;
    }
}


