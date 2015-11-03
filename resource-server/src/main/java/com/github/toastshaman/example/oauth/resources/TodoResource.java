package com.github.toastshaman.example.oauth.resources;

import com.google.common.collect.ImmutableMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/todos")
public class TodoResource {

    @RequestMapping(method = GET)
    public List<Map<String, Object>> getAll() {
        return asList(ImmutableMap.of("Hello", "World"));
    }
}
