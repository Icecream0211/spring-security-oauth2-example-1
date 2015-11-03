package com.github.toastshaman.example.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;

import static com.fasterxml.jackson.databind.DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

@SpringBootApplication
@Configuration
public class ResourceServer {

    public static void main(String[] args) {
        SpringApplication.run(ResourceServer.class, args);
    }

    @Bean @Primary
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
            .registerModule(new Jdk8Module())
            .configure(USE_BIG_DECIMAL_FOR_FLOATS, true)
            .configure(WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Configuration
    @EnableResourceServer
    @SuppressWarnings("unused")
    public static class OAuthConfiguration extends ResourceServerConfigurerAdapter {

        @Bean
        public RemoteTokenServices remoteTokenServices() {
            final RemoteTokenServices tokenServices = new RemoteTokenServices();
            tokenServices.setCheckTokenEndpointUrl("http://localhost:8081/oauth/check_token");
            tokenServices.setClientId("resource-server");
            tokenServices.setClientSecret("resource-server");
            return tokenServices;
        }

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
            resources
                .tokenServices(remoteTokenServices())
                .resourceId("todo-services");
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            // For some reason we cant just "permitAll" OPTIONS requests which are needed for CORS support. Spring Security
            // will respond with an HTTP 401 nonetheless.
            // So we just put all other requests types under OAuth control and exclude OPTIONS.
            http
                .authorizeRequests()
                    .antMatchers(HttpMethod.GET, "/**").access("#oauth2.hasScope('read')")
                    .antMatchers(HttpMethod.POST, "/**").access("#oauth2.hasScope('write')")
                    .antMatchers(HttpMethod.PATCH, "/**").access("#oauth2.hasScope('write')")
                    .antMatchers(HttpMethod.PUT, "/**").access("#oauth2.hasScope('write')")
                    .antMatchers(HttpMethod.DELETE, "/**").access("#oauth2.hasScope('write')")
                .and()
                    .headers().addHeaderWriter((request, response) -> {
                        response.addHeader("Access-Control-Allow-Origin", "*");
                        if (request.getMethod().equals("OPTIONS")) {
                            response.setHeader("Access-Control-Allow-Methods", request.getHeader("Access-Control-Request-Method"));
                            response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
                    }
                });
        }
    }
}
