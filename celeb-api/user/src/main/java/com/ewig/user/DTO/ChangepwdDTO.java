package com.ewig.user.DTO;

import lombok.Data;

@Data
public class ChangepwdDTO {

    private String email;
    private String old_password;
    private String new_password;

}
