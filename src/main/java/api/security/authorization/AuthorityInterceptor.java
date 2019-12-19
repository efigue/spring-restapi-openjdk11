package api.security.authorization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Component
public class AuthorityInterceptor implements HandlerInterceptor {

    private final LdapTemplate ldapTemplate;

    @Autowired
    public AuthorityInterceptor(LdapTemplate ldapTemplate) {
        this.ldapTemplate = ldapTemplate;
    }

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
        var mapper = (AttributesMapper<ArrayList<?>>) attrs -> {
            var userGroups = attrs.get("memberOf");
            return userGroups != null ? Collections.list(userGroups.getAll()) : new ArrayList<>();
        };
        return ldapTemplate.search(query().where("sAMAccountName").is(userID), mapper).get(0);
    }

    private SimpleGrantedAuthority getGrantedAuthorityFromGroup(String group) {
        return new SimpleGrantedAuthority("ROLE_" + getGroupName(group));
    }

    private String getGroupName(String group) {
        return group.substring(group.indexOf('=') + 1, group.indexOf(','));
    }

}
