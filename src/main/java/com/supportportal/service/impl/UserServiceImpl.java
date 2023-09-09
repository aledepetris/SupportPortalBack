package com.supportportal.service.impl;

import com.supportportal.domain.User;
import com.supportportal.domain.UserPrincipal;
import com.supportportal.exception.domain.EmailExistException;
import com.supportportal.exception.domain.UserNotFoundException;
import com.supportportal.exception.domain.UsernameExistException;
import com.supportportal.repository.UserRepository;
import com.supportportal.service.LoginAttemptService;
import com.supportportal.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Date;
import java.util.List;

import static com.supportportal.constant.UserImplConstant.*;
import static com.supportportal.enumaration.Role.ROLE_USER;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Log4j2
@Service
@Transactional
@Qualifier("userDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private LoginAttemptService loginAttemptService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            log.error(NO_USER_FOUND_BY_USERNAME + username);
            throw new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME + username);
        } else {
            validateLoginAttempt(user);
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepository.save(user);
            UserPrincipal userPrincipal = new UserPrincipal(user);
            log.info(RETURNING_FOUND_USER_BY_USERNAME + username);
            return userPrincipal;
        }

    }

    private void validateLoginAttempt(User user) {
        if (user.isNotLocked()) {
            user.setNotLocked(!loginAttemptService.hasExceededMaxAttempts(user.getUsername()));
        } else {
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }

    @Override
    public User register(String firstName, String lastName, String username, String email)
            throws UserNotFoundException, EmailExistException, UsernameExistException {

        validateNewUsernameAndEmail(EMPTY, username, email);
        User user = new User();
        user.setUserId(generateUserId());
        String password = generatePassword();
        String encodedPassword = encodePassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEmail(email);
        user.setJoinDate(new Date());
        user.setPassword(encodedPassword);
        user.setActive(true);
        user.setNotLocked(true);
        user.setRole(ROLE_USER.name());
        user.setAuthorities(ROLE_USER.getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileImageUrl());

        userRepository.save(user);
        log.info("New user password: {}", password);
        return user;

    }

    private String getTemporaryProfileImageUrl() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMG_PATH).toUriString();
    }

    private String encodePassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(8);
    }

    private String generateUserId() {
        return RandomStringUtils.randomNumeric(10);
    }

    private User validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail)
            throws UserNotFoundException, UsernameExistException, EmailExistException {

        User userByNewUsername = findUserByUsername(newUsername);
        User userByNewEmail = findUserByEmail(newEmail);
        if (StringUtils.isNotBlank(currentUsername)) {
            User currentUser = findUserByUsername(currentUsername);
            if (currentUser == null)
                throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + currentUsername);
            if (userByNewUsername != null && !currentUser.getId().equals(userByNewUsername.getId()))
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            if (userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getId()))
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            return currentUser;
        } else {
            if (userByNewUsername != null) throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            if (userByNewEmail != null) throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            return null;
        }

    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }
}
