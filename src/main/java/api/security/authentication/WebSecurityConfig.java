package api.security.authentication;

import api.security.authentication.jwt.JWTAuthenticationFilter;
import api.security.authentication.jwt.JWTLoginFilter;
import api.security.authentication.jwt.LdapUserDetailsContextMapper;
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
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.userdetails.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    //todo: do i need this tag here
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
//            .userDetailsContextMapper(userDetailsContextMapper()) //put this back if things break
        .userDetailsContextMapper(new LdapUserDetailsContextMapper())
        .contextSource(contextSource())
        .and().userDetailsService(new LdapUserDetailsService(new FilterBasedLdapUserSearch("DC=private,DC=treetop,DC=com", "(sAMAccountName=efigue)", contextSource())));
//            .ldapAuthoritiesPopulator(authoritiesPopulator());
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

    @Bean
    public LdapAuthoritiesPopulator authoritiesPopulator(){
        DefaultLdapAuthoritiesPopulator populator = new DefaultLdapAuthoritiesPopulator(contextSource(), "");
        populator.setSearchSubtree(true);
        populator.setIgnorePartialResultException(true);
        return populator;
    }
}
