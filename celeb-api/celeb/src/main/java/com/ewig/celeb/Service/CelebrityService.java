package com.ewig.celeb.Service;

import com.ewig.celeb.DTO.CelebrityDTO;
import com.ewig.celeb.DTO.ChangepwdDTO;
import com.ewig.celeb.Document.Celebrity;
import com.ewig.celeb.Document.Token;
import com.ewig.celeb.Repository.CelebrityRepository;
import com.ewig.celeb.Repository.TokenRepository;
import io.micrometer.core.instrument.util.StringUtils;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class CelebrityService {
    private JavaMailSender javaMailSender;

    @Autowired
    public CelebrityService(JavaMailSender javaMailSender){
        this.javaMailSender = javaMailSender;
    }


    @Autowired
    private CelebrityRepository celebrityRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MongoOperations mongoOperations;


    public String registerCelebrity(CelebrityDTO celebrityDTO, MultipartFile multipartFile) throws IOException {
        Celebrity celebrity1 = celebrityRepository.findByPhoneNumber(celebrityDTO.getPhoneNumber());
        Celebrity celebrity2 = celebrityRepository.findByEmail(celebrityDTO.getEmail());
        if(celebrity1!= null){
            return "phone number already exists";
        }
        if(celebrity2 != null){
            return "email is already exists";
        }
        Celebrity celebrity3 = new Celebrity();
        celebrity3.setFirstName(celebrityDTO.getFirstName());
        celebrity3.setLastName(celebrityDTO.getLastName());
        celebrity3.setEmail(celebrityDTO.getEmail());
        celebrity3.setPhoneNumber(celebrityDTO.getPhoneNumber());
        celebrity3.setLocation(celebrityDTO.getLocation());
        celebrity3.setDescription(celebrityDTO.getDescription());
        celebrity3.setLinks(celebrityDTO.getLinks());
        celebrity3.setProfilePic(new Binary(multipartFile.getBytes()));
        celebrityRepository.save(celebrity3);

        return "celebrity is added";
    }

    public String sentEmail(CelebrityDTO celebrityDTO) {
        Celebrity celebrity1 = celebrityRepository.findByEmail(celebrityDTO.getEmail());
        if(celebrity1==null){
            return "email not found";
        }
        else
        {
            Token token = new Token();
            token.setTokenId(celebrity1.getId());
            token.setToken(UUID.randomUUID().toString());
            tokenRepository.save(token);
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(celebrityDTO.getEmail());
            mail.setSubject("complete Registration");
            mail.setText("to activate your account, please click on the link here: " + "http://localhost:8080/confirm-account?token="+token.getToken());
            javaMailSender.send(mail);
            return "mail sent success";
        }
    }

    public String verifyCelebrity(String confirmationToken) {
        Token token = tokenRepository.findByToken(confirmationToken);

        String password = new Random().ints(8, 33, 122).collect(StringBuilder::new,
                StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        if(token == null){
            return "token is corrupted";
        }
        else{
            Celebrity celebrity = celebrityRepository.findByCelebrityId(token.getTokenId());
            if(celebrity!= null){
                BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
                String encryptPassword = bCryptPasswordEncoder.encode(password);
                celebrity.setPassword(encryptPassword);
                celebrityRepository.save(celebrity);
                token.setIsActive(false);
                tokenRepository.save(token);
                return "celebrity is verified "+"username: "+celebrity.getEmail() + " password: "+ password;
            }
            else
                return "celebrity is not found";
        }

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
//
//        List celebrities = celebrityRepository.findAllCelebrities();
//        return celebrities;
    }

    public String changePassword(ChangepwdDTO changepwdDTO) {

        Celebrity celebrity = celebrityRepository.findByEmail(changepwdDTO.getEmail());
        if(celebrity== null){
            return "email not found";
        }
        else
        {
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            String encodePassword = bCryptPasswordEncoder.encode(changepwdDTO.getNew_password());
            Boolean b = bCryptPasswordEncoder.matches(changepwdDTO.getOld_password(),celebrity.getPassword());
            if(b){
                Query query= new Query();
                query.addCriteria(Criteria.where("password").is(celebrity.getPassword()));
                Update update = new Update();
                update.set("password", encodePassword);
                mongoTemplate.upsert(query, update, Celebrity.class);
                return "password changed successfully";
            }
            else{
                return "password mismatch";
            }
        }
    }
}
