package com.github.toastshaman.example.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import javax.sql.DataSource;

import static com.fasterxml.jackson.databind.DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

@SpringBootApplication
@EnableWebMvcSecurity
@Configuration
public class OAuthServer {

    public static void main(String[] args) {
        SpringApplication.run(OAuthServer.class, args);
    }

    @Bean @Primary
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
            .registerModule(new Jdk8Module())
            .configure(USE_BIG_DECIMAL_FOR_FLOATS, true)
            .configure(WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Configuration
    @SuppressWarnings("unused")
    public static class SecurityConfiguration extends WebSecurityConfigurerAdapter {
        @Override
        public void configure(HttpSecurity http) throws Exception {
            http
                .formLogin().loginPage("/login").permitAll().and()
                .requestMatchers().antMatchers("/login", "/oauth/authorize", "/oauth/confirm_access").and()
                .authorizeRequests().anyRequest().authenticated();
        }
    }

    @EnableAuthorizationServer
    @Configuration
    @SuppressWarnings("unused")
    public static class OAuthConfiguration extends AuthorizationServerConfigurerAdapter {

        @Bean
        @ConfigurationProperties(prefix = "spring.datasource.oauth")
        public DataSource dataSource() {
            return DataSourceBuilder.create().build();
        }

        @Bean
        public TokenStore tokenStore() {
            return new JdbcTokenStore(dataSource());
        }

        @Bean
        public ApprovalStore approvalStore() {
            return new JdbcApprovalStore(dataSource());
        }

        @Bean
        public AuthorizationCodeServices authorizationCodeServices() {
            return new JdbcAuthorizationCodeServices(dataSource());
        }

        /**
         * We expose the JdbcClientDetailsService because it has extra methods that the Interface does not have. E.g.
         * {@link org.springframework.security.oauth2.provider.client.JdbcClientDetailsService#listClientDetails()} which we need for the
         * admin page.
         */
        @Bean
        public JdbcClientDetailsService clientDetailsService() {
            return new JdbcClientDetailsService(dataSource());
        }

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            clients.withClientDetails(clientDetailsService());
        }

        @Override
        public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
            oauthServer
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()");
        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
            endpoints
                .approvalStore(approvalStore())
                .authorizationCodeServices(authorizationCodeServices())
                .tokenStore(tokenStore());
        }
    }
}
