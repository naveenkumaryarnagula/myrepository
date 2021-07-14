package com.ewig.celeb.DTO;

import lombok.Data;

import java.util.Date;

@Data
public class TokenDTO {
    private String id;
    private String tokenId;
    private String token;
    private Boolean isActive;
    private Date createDate;
}
