package com.ewig.user.DTO;

import lombok.Data;

@Data
public class UserInfoDTO {
    private String userInfoId;
    private String userId;
    private String otp;
    private Integer isExpire;
    private String otpType;
    private Long otpTime;
    private int isValidate;
}
