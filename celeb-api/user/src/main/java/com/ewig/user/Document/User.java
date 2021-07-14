package com.ewig.user.Document;

import lombok.Data;
import org.bson.types.Binary;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection ="userdetails")
@Data
public class User {


    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String password;
    private String location;
    private Date createDate;
    private Date updateDate;
    private String profile_photo;
    private Binary data;


}
