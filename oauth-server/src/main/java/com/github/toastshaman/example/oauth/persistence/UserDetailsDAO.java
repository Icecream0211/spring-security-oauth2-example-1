package com.github.toastshaman.example.oauth.persistence;

import alexh.weak.Dynamic;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;
import static com.google.common.collect.Maps.newHashMap;

public class UserDetailsDao implements UserDetailsService {

    @Autowired
    private JdbcTemplate db;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final List<SimpleGrantedAuthority> authorities = Lists.newLinkedList();
        return db.queryForList("select * from credentials c " +
            "  join credentials_authorities ca on c.id = ca.credentials_id " +
            "  join authority a on ca.authorities_id = a.id " +
            "where name = ? and enabled = 1", username).stream()
            .map(UserDetailsDao::transformKeys)
            .map(Dynamic::from)
            .map(user -> {
                authorities.add(new SimpleGrantedAuthority(user.get("authority").asString()));
                return user;
            })
            .map(user -> new User(
                user.get("name").asString(),
                user.get("password").asString(),
                user.get("enabled").as(Boolean.class),
                true, true, true,
                authorities))
            .findFirst().orElseThrow(() -> new UsernameNotFoundException(""));
    }

    protected static Map<String, Object> transformKeys(Map<String, Object> map) {
        final HashMap<String, Object> copy = newHashMap();
        map.forEach((key, val) -> copy.put(UPPER_UNDERSCORE.to(LOWER_CAMEL, key.toLowerCase()), val));
        return copy;
    }
}
