package com.ewig.celeb.Document;

import lombok.Data;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "celebrity")
@Data
public class Celebrity {

    @Id
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
