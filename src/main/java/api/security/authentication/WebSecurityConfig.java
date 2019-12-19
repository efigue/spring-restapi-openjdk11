package api.security.authentication;

import api.security.authentication.jwt.JWTAuthenticationFilter;
import api.security.authentication.jwt.JWTLoginFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.headers().cacheControl(); //disable caching

        http.csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/login").permitAll()
                .anyRequest().authenticated()
                .and()
                //filter api/login requests1
                .addFilterBefore(new JWTLoginFilter("/login", authenticationManager()), UsernamePasswordAuthenticationFilter.class)
                //filter other requests to check the presence of JWT in the header
                .addFilterBefore(new JWTAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.ldapAuthentication()
        .userSearchFilter("(sAMAccountName={0})")
        .contextSource(contextSource());
    }

    @Bean
    public BaseLdapPathContextSource contextSource() {
        LdapContextSource bean = new LdapContextSource();
        bean.setUrl("ldap://ldap.treetop.com:389");
        bean.setBase("DC=private,DC=treetop,DC=com");
        bean.setUserDn("ADReader");
        bean.setPassword("h6bmem");
        bean.setReferral("follow");
        bean.afterPropertiesSet();
        return bean;
    }

}
