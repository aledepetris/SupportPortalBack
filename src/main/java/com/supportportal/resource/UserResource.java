package com.supportportal.resource;

import com.supportportal.domain.HttpResponse;
import com.supportportal.domain.User;
import com.supportportal.domain.UserPrincipal;
import com.supportportal.exception.domain.*;
import com.supportportal.exception.ExceptionHandling;
import com.supportportal.model.request.UserLoginRequest;
import com.supportportal.model.request.UserRegisterRequest;
import com.supportportal.model.request.UserRequest;
import com.supportportal.service.UserService;
import com.supportportal.utility.JWTTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;

import java.io.IOException;
import java.util.List;

import static com.supportportal.constant.FileConstant.*;
import static com.supportportal.constant.SecurityConstant.JWT_TOKEN_HEADER;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@RestController
@RequestMapping(path = {"/", "/user"})
public class UserResource extends ExceptionHandling {

    public static final String EMAIL_SENT = "An email with a new password was sent to: ";
    public static final String USER_DELETED_SUCCESSFULLY = "User deleted successfully";

    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JWTTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody @Valid UserLoginRequest user) {
        authenticate(user.getUsername(), user.getPassword());
        User loginUser = userService.findUserByUsername(user.getUsername());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
        return ResponseEntity.status(OK).headers(jwtHeader).body(loginUser);
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody @Valid UserRegisterRequest user)
            throws UserNotFoundException, EmailExistException, UsernameExistException, MessagingException {
        User newUser = userService.register(user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail());
        return ResponseEntity.status(OK).body(newUser);
    }

    @PostMapping("/add")
    public ResponseEntity<User> addNewUser(@RequestBody @Valid UserRequest user)
            throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException {
        User newUser = userService.addNewUser(
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.isNonLocked(),
                user.isActive(),
                user.getProfileImage());
        return new ResponseEntity<>(newUser, OK);
    }

    @PostMapping("/update")
    public ResponseEntity<User> update(@RequestBody @Valid UserRequest user)
            throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException {
        User updatedUser = userService.updateUser(
                user.getCurrentUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.isNonLocked(),
                user.isActive(),
                user.getProfileImage());
        return new ResponseEntity<>(updatedUser, OK);
    }

    @GetMapping("/find/{username}")
    public ResponseEntity<User> getUser(@PathVariable("username") String username) {
        User user = userService.findUserByUsername(username);
        return new ResponseEntity<>(user, OK);
    }
    @GetMapping("/list")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getUsers();
        return new ResponseEntity<>(users, OK);
    }

    @GetMapping("/resetpassword/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email)
            throws MessagingException, EmailNotFoundException {
        userService.resetPassword(email);
        return response(OK, EMAIL_SENT + email);
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        HttpResponse response = HttpResponse.builder()
                .httpStatusCode(httpStatus.value())
                .httpStatus(httpStatus)
                .message(message)
                .reason(httpStatus.getReasonPhrase().toUpperCase())
                .build();
        return new ResponseEntity<>(response, httpStatus);
    }

    private HttpHeaders getJwtHeader(UserPrincipal user) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(user));
        return headers;
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

}
