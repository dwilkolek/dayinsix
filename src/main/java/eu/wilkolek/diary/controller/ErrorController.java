package eu.wilkolek.diary.controller;

import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.ErrorPage;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;

@Controller
public class ErrorController {
    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {
     
       return (container -> {
            ErrorPage error401Page = new ErrorPage(HttpStatus.UNAUTHORIZED, "/exception.html");
            ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/exception.html");
            ErrorPage error403Page = new ErrorPage(HttpStatus.FORBIDDEN, "/exception.html");
            ErrorPage error500Page = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/exception.html");
            ErrorPage error400Page = new ErrorPage(HttpStatus.BAD_REQUEST, "/exception.html");
            ErrorPage error4011Page = new ErrorPage(HttpStatus.GATEWAY_TIMEOUT, "/exception.html");
            ErrorPage error4012Page = new ErrorPage(HttpStatus.REQUEST_TIMEOUT, "/exception.html");
            ErrorPage error4013Page = new ErrorPage(HttpStatus.NO_CONTENT, "/exception.html");
            ErrorPage error4014Page = new ErrorPage(HttpStatus.METHOD_NOT_ALLOWED, "/exception.html");
            container.addErrorPages(error400Page,error401Page, error403Page, error404Page, error500Page,error4011Page,error4012Page,error4013Page,error4014Page);
       });
    }
}
