package com.github.toastshaman.example.oauth;

import com.github.toastshaman.example.oauth.persistence.UserDetailsDao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@Configuration
@SuppressWarnings("unused")
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        return new UserDetailsDao();
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
            .formLogin()
                .loginPage("/login")
                .usernameParameter("form-username")
                .passwordParameter("form-password")
                .loginProcessingUrl("/login.do").permitAll()
            .and()
                .authorizeRequests().antMatchers("/me").authenticated()
            .and()
                .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout.do"))
            .and()
                .requestMatchers().antMatchers("/login", "/login.do", "/oauth/authorize", "/oauth/confirm_access")
            .and()
                .authorizeRequests().anyRequest().authenticated()
            .and()
                .userDetailsService(userDetailsService());
    }
}