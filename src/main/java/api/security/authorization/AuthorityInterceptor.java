package api.security.authorization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Component
public class AuthorityInterceptor implements HandlerInterceptor {

    @Autowired
    private LdapTemplate ldapTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Set<GrantedAuthority> authorities = new HashSet<>();

        if(auth.isAuthenticated()) {
            getUserGroups(auth.getName()).forEach(group -> authorities.add(getGrantedAuthorityFromGroup((String)group)));
        }

        Authentication newAuth = new UsernamePasswordAuthenticationToken(auth.getPrincipal(), auth.getCredentials(), authorities);
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        return true;
    }

    private ArrayList<?> getUserGroups(String userID) {
        return ldapTemplate.search (
            query().where("sAMAccountName").is(userID),
            (AttributesMapper<ArrayList<?>>) attrs -> {
                var list = attrs.get("memberOf");
                return list != null ? Collections.list(list.getAll()) : new ArrayList<>();
            }
        ).get(0);
    }

    private SimpleGrantedAuthority getGrantedAuthorityFromGroup(String group) {
        return new SimpleGrantedAuthority("ROLE_" + getGroupName(group));
    }

    private String getGroupName(String group) {
        return group.substring(group.indexOf('=') + 1, group.indexOf(','));
    }

}
