package com.example.demo.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import static com.example.demo.security.Roles.*;

@Configuration
@EnableWebSecurity
public class AppSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private final PasswordEncoder passwordEncoder;


    public AppSecurityConfig(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        /*
            Stateless session enables authentication for every request. This would help ease the demonstration
            of this tutorial on Postman.
        */
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.csrf().disable().authorizeRequests()
                .antMatchers(HttpMethod.DELETE,"/api/v1/products/{productId}").hasRole(ADMIN.name())
                .antMatchers(HttpMethod.PUT,"/api/v1/products/{productId}").hasRole(ADMIN.name())
                .antMatchers("/api/v1/products/add").hasAnyRole(ADMIN.name(),SUPERVISOR.name())
                .antMatchers("/api/v1/products").hasAnyRole(ADMIN.name(), SUPERVISOR.name(),INTERN.name())
                .antMatchers("/api/v1/products/{productId}").hasAnyRole(ADMIN.name(), SUPERVISOR.name(), INTERN.name()) // All three users should be able to get a product by id.
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic();
    }

    // Setting up the details of the application users with their respective usernames, roles and passwords.
    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        UserDetails jojan = User.builder()
                .username("jojan")
                .password(passwordEncoder.encode("password"))
                .roles(INTERN.name())
                .build();

        UserDetails bishwo = User.builder()
                .username("bishwo")
                .password(passwordEncoder.encode("password"))
                .roles(SUPERVISOR.name())
                .build();

        UserDetails prabin = User.builder()
                .username("prabin")
                .password(passwordEncoder.encode("password"))
                .roles(ADMIN.name())
                .build();

        InMemoryUserDetailsManager userDetailsManager=new InMemoryUserDetailsManager(jojan,bishwo,prabin);
        return userDetailsManager;
    }
}
