package com.shadowbattler.simulator.web.error;

import java.util.Map;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import com.shadowbattler.simulator.exception.PublicResponseStatusException;

@Component
public class PublicMessageErrorAttributes extends DefaultErrorAttributes {
    @Override
    public Map<String, Object> getErrorAttributes(
        WebRequest webRequest,
        ErrorAttributeOptions options
    ) {
        Map<String, Object> attributes = super.getErrorAttributes(webRequest, options);
        Throwable error = getError(webRequest);
        if (error instanceof PublicResponseStatusException exception) {
            attributes.put("message", exception.getReason());
        }
        return attributes;
    }
}
