package com.supportportal.model.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter @Setter
public class UserRequest {

    @NotBlank
    private String currentUsername;
    @NotNull @NotBlank
    private String firstName;
    @NotNull @NotBlank
    private String lastName;
    @NotNull @NotBlank
    private String username;
    @NotNull @NotBlank @Email
    private String email;
    @NotNull @NotBlank
    private String role;
    @NotNull
    private boolean isActive;
    @NotNull
    private boolean isNonLocked;
    private MultipartFile profileImage;

}
