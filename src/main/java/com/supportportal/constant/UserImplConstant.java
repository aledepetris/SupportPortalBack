package com.supportportal.constant;

public class UserImplConstant {

    private UserImplConstant() {
        throw new IllegalStateException("Utility class");
    }

    public static final String EMAIL_ALREADY_EXISTS = "Email already exists";
    public static final String USERNAME_ALREADY_EXISTS = "Username already exists";
    public static final String NO_USER_FOUND_BY_USERNAME = "No user found by username ";
    public static final String RETURNING_FOUND_USER_BY_USERNAME = "Returning found user by username: ";
    public static final String DEFAULT_USER_IMG_PATH = "/user/image/profile/temp";

}
