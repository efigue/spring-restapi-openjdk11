package api.security.authorization;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class AuthorizationMapping {
    private HashMap<String, String> map;

    public AuthorizationMapping() {
        map = new HashMap<>();
        map.put("/people", "DevServerAdmin");
    }

    public boolean hasAuthorizationTo(String path) {
        return SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getAuthorities()
            .contains(new SimpleGrantedAuthority("ROLE_" + map.get(path)));
    }
}
