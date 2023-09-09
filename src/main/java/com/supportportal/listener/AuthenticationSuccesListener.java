package com.supportportal.listener;

import com.supportportal.domain.UserPrincipal;
import com.supportportal.service.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccesListener {

    @Autowired
    private LoginAttemptService loginAttemptService;

    @EventListener
    public void onAuthenticationSucces(AuthenticationSuccessEvent event) {

        Object principal = event.getAuthentication().getPrincipal();
        if (principal instanceof UserPrincipal) {
            UserPrincipal user = (UserPrincipal) principal;
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }

    }

}
