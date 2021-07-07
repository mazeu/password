package com.mazeu.passwordmanager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import static org.springframework.http.HttpMethod.POST;

@Configuration
@EnableWebSecurity
public class AccessConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authenticationMgr) throws Exception {
        // Define o perfil para separar seus endpoints
        //{noop} remove a necessidade de definir o login
        authenticationMgr.inMemoryAuthentication()
        .withUser("gerente").password("{noop}gerente").roles("GERENTE");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable(); //Isso permite que solicitações de postagem
        http.cors().disable();
        http.authorizeRequests()
        .antMatchers(POST, "/login").permitAll()
        .antMatchers("/gerente").access("hasRole('ROLE_GERENTE')")
        .antMatchers(POST, "/actuator").access("hasRole('ROLE_GERENTE')");
    }
}