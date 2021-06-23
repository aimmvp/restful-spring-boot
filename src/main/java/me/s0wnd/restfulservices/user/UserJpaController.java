package me.s0wnd.restfulservices.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/jpa")
public class UserJpaController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    // http://localhost:8088/jpa/users
    @GetMapping("/users")
    public List<User> retrieveAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/users/{userId}")
    public EntityModel<User> retrieveUser(@PathVariable int userId) {
        Optional<User> user = userRepository.findById(userId);
        if( !user.isPresent() ) {
            throw new UserNotFoundException(String.format("ID[%s] not found", userId));
        }

        EntityModel<User> entityModel = new EntityModel<>(user.get());
        WebMvcLinkBuilder linkBuilder = linkTo(methodOn(this.getClass()).retrieveAllUsers());
        entityModel.add(linkBuilder.withRel("all-users"));

        return entityModel;
    }

    @DeleteMapping("/users/{userId}")
    public void deleteUser(@PathVariable int userId) {
        userRepository.deleteById(userId);
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User savedUser = userRepository.save(user);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{userId}")
                .buildAndExpand(savedUser.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/users/{userId}/posts")
    public List<Post> retrieveAllPostByUser(@PathVariable int userId) {
        Optional<User> user = userRepository.findById(userId);
        if( !user.isPresent() ) {
            throw new UserNotFoundException(String.format("ID[%s] not found", userId));
        }

        return user.get().getPosts();
    }


    @PostMapping("/users/{userId}/posts")
    public ResponseEntity<Post> createPost(@PathVariable int userId, @RequestBody Post post) {
        Optional<User> user = userRepository.findById(userId);

        if (!user.isPresent()) {
            throw new UserNotFoundException(String.format("ID[%s] is not found", userId));
        }

        post.setUser(user.get());
        Post savedPost = postRepository.save(post);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{userId}")
                .buildAndExpand(savedPost.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }
}
