package com.ewig.user.Document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "userinfo")
@Data
public class UserInfo {

    @Id
    private String userInfoId;
    private String userId;
    private String otp;
    private Integer isExpire;
    private String otpType;
    private Long otpTime;
    private int isValidate;

}
