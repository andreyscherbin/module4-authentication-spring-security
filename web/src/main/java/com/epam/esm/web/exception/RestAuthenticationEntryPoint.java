package com.epam.esm.web.exception;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.epam.esm.entity.ErrorCode.ACCESS_DENIED;

@Component("restAuthenticationEntryPoint")
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final MessageSource messageSource;

    @Autowired
    public RestAuthenticationEntryPoint(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authenticationException) throws IOException {
        String status = String.valueOf(HttpStatus.UNAUTHORIZED.value());
        String message = messageSource.getMessage("error.auth.header.missing", null, LocaleContextHolder.getLocale());
        ApiError apiError = new ApiError(List.of(message), status + ACCESS_DENIED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        String json = new Gson().toJson(apiError);
        response.getWriter().write(json);
    }
}
