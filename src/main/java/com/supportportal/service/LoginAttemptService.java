package com.supportportal.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

import static java.util.concurrent.TimeUnit.MINUTES;

@Service
public class LoginAttemptService {

    public static final int MAX_NUMBER_OF_ATTEMPTS = 5;
    public static final int ATTEMPT_INCREMENT = 1;

    private LoadingCache<String, Integer> loginAttemptCache;

    public LoginAttemptService() {
        super();
        loginAttemptCache = CacheBuilder.newBuilder()
                .expireAfterWrite(15, MINUTES)
                .maximumSize(100)
                .build(new CacheLoader<String, Integer>() {
                    public Integer load(String key) {
                        return 0;
                    }
                });
    }

    public void evictUserFromLoginAttemptCache(String username) {
        loginAttemptCache.invalidate(username);
    }

    public void addUserToLoginAttemptCache(String username)  throws ExecutionException {
        int attempts = ATTEMPT_INCREMENT + loginAttemptCache.get(username);
        loginAttemptCache.put(username, attempts);
    }

    public boolean hasExceededMaxAttempts(String username) throws ExecutionException {
        return loginAttemptCache.get(username) >= MAX_NUMBER_OF_ATTEMPTS;
    }

}
