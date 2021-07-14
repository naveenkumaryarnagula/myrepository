package com.ewig.celeb.DTO;

import lombok.Data;
import org.bson.types.Binary;

@Data
public class CelebrityDTO {

    private String id;
    private String  firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String location;
    private String description;
    private String links;
    private Binary profilePic;
    private String password;
}
