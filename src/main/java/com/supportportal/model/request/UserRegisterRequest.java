package com.supportportal.model.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter @Setter
public class UserRegisterRequest {

    @NotNull @NotBlank
    private String firstName;
    @NotNull @NotBlank
    private String lastName;
    @NotNull @NotBlank
    private String username;
    @NotNull @NotBlank
    private String email;

}
