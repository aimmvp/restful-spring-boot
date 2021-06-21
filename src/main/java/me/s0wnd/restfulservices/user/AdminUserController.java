package me.s0wnd.restfulservices.user;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.beans.BeanUtils;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminUserController {
    private UserDaoService userDaoService;

    public AdminUserController(UserDaoService service) {
        this.userDaoService = service;
    }

    @GetMapping("/users")
//    public List<User> retrieveAllUsers() {
    public MappingJacksonValue retrieveAllUsers() {
        List<User> users = userDaoService.findAll();

        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter
                .filterOutAllExcept("id", "name", "joinDate", "ssn");

        FilterProvider filters = new SimpleFilterProvider().addFilter("UserInfoFilter", filter);
        MappingJacksonValue mapping = new MappingJacksonValue(users);
        mapping.setFilters(filters);

        return mapping;
    }

//    @GetMapping("/v1/users/{userId}")
//    @GetMapping(value="/users/{userId}/", params = "version=1")
//    @GetMapping(value="/users/{userId}", headers = "X-API-VERSION=1")
    @GetMapping(value="/users/{userId}", produces = "application/vnd.company.appv1+json")
    public MappingJacksonValue retrieveUserv1(@PathVariable int userId) {
        User user = userDaoService.findOne(userId);

        if (user == null ) {
            throw new UserNotFoundException(String.format("ID[%s] not found", userId));
        }

        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter
                .filterOutAllExcept("id", "name", "password", "ssn");

        FilterProvider filters = new SimpleFilterProvider().addFilter("UserInfoFilter", filter);
        MappingJacksonValue mapping = new MappingJacksonValue(user);
        mapping.setFilters(filters);

        return mapping;
    }


//    @GetMapping("/v2/users/{userId}")
//    @GetMapping(value="/users/{userId}/", params = "version=2")
//    @GetMapping(value="/users/{userId}", headers = "X-API-VERSION=2")
    @GetMapping(value="/users/{userId}", produces = "application/vnd.company.appv2+json")
    public MappingJacksonValue retrieveUserv2(@PathVariable int userId) {
        User user = userDaoService.findOne(userId);

        if (user == null ) {
            throw new UserNotFoundException(String.format("ID[%s] not found", userId));
        }

        // User --> User2
        UserV2 userV2 = new UserV2();
        BeanUtils.copyProperties(user, userV2);
        userV2.setGrade("VIP");

        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter
                .filterOutAllExcept("id", "name", "joinDate", "grade");

        FilterProvider filters = new SimpleFilterProvider().addFilter("UserInfoFilterV2", filter);
        MappingJacksonValue mapping = new MappingJacksonValue(userV2);
        mapping.setFilters(filters);

        return mapping;
    }
}
