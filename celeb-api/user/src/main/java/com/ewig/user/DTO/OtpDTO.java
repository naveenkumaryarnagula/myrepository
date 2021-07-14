package com.ewig.user.DTO;

import lombok.Data;

@Data
public class OtpDTO {
    private String email;
    private String phoneNumber;
    private String otp;
}
