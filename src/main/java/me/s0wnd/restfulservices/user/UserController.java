package me.s0wnd.restfulservices.user;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class UserController {
    private UserDaoService userDaoService;

    public UserController(UserDaoService service) {
        this.userDaoService = service;
    }

    @GetMapping("/users")
    public List<User> retrieveAllUsers() {
        return userDaoService.findAll();
    }

//    Before HATEOAS
//
//    @GetMapping("/users/{userId}")
//    public User retrieveUser(@PathVariable int userId) {
//        User user = userDaoService.findOne(userId);
//
//        if (user == null ) {
//            throw new UserNotFoundException(String.format("ID[%s] not found", userId));
//        }
//        return user;
//    }
    // AFTER HATEOAS ( Spring Boot
    @GetMapping("/users/{userId}")
    public EntityModel<User> retrieveUser(@PathVariable int userId) {
        User user = userDaoService.findOne(userId);

        if (user == null ) {
            throw new UserNotFoundException(String.format("ID[%s] not found", userId));
        }
        // HATEOAS --> User 에서 filter 적용 빼기
        EntityModel<User> model = new EntityModel<>(user);
        WebMvcLinkBuilder linkBuilder = linkTo( methodOn(this.getClass()).retrieveAllUsers());
        model.add(linkBuilder.withRel("all-users"));

        return model;
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User savedUser = userDaoService.save(user);

        URI location =  ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedUser.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("/users/{userId}")
    public  void deleteUser(@PathVariable int userId) {
       User user = userDaoService.deleteById(userId);

       if (user == null) {
           throw new UserNotFoundException(String.format("ID[%s] not found", userId));
       }
    }
}
