package yg0r2.examples.layout.web.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .cors()
                .and()
            .csrf()
                .disable() // https://github.com/spring-cloud/spring-cloud-netflix/issues/2754#issuecomment-372808529
            .httpBasic()
                .disable()
            .formLogin()
                .loginPage("http://auth-service.localhost/sign-in").permitAll()
                .and()
//            .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.NEVER)
//                .and()
            .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/", "/static/**").permitAll()
                .anyRequest().authenticated();
    }

}
