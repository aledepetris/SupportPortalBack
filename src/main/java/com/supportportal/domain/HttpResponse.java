package com.supportportal.domain;

import lombok.*;
import org.springframework.http.HttpStatus;


@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class HttpResponse {

    private int httpStatusCode;
    private HttpStatus httpStatus;
    private String reason;
    private String message;

}
