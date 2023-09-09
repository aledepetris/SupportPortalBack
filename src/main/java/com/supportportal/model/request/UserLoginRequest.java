package com.supportportal.model.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter @Setter
public class UserLoginRequest {

    @NotNull @NotBlank
    private String username;
    @NotNull @NotBlank
    private String password;

}
