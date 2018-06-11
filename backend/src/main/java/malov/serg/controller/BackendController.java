package malov.serg.controller;

import malov.serg.domain.User;
import malov.serg.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;



@RestController()
@RequestMapping("/api")
public class BackendController {

    private static final Logger LOG = LoggerFactory.getLogger(BackendController.class);

    @Autowired
    private UserRepository userRepository;


    @RequestMapping(path = "/users")
    public @ResponseBody Iterable<User> users() {
        LOG.info("GET called on /hello resource");
        return userRepository.findAll();
    }


    @GetMapping(path="/user/{id}")
    public @ResponseBody User getUserById(@PathVariable("id") long id) {
        LOG.info("Reading user with id " + id + " from database.");
        return userRepository.findById(id).get();
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public User updateUser(@PathVariable Long id, @RequestBody User data) {

        User user = userRepository.findById(id).get();

        user.setFirstName(data.getFirstName());

        user.setLastName(data.getLastName());

        return userRepository.save(user);
    }

}
