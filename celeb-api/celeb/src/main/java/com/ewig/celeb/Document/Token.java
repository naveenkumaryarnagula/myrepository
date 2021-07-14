package com.ewig.celeb.Document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "token")
public class Token {
    @Id
    private String id;
    private String tokenId;
    private String token;
    private Boolean isActive;
    private Date createDate;
}
