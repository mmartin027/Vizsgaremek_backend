package com.vizsgaremek.backend.controler;


import com.vizsgaremek.backend.model.User;
import com.vizsgaremek.backend.service.JwtService;
import com.vizsgaremek.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200") // Engedélyezi az Angular frontendet
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    AuthenticationManager authenticationManager;


    @Autowired
    private JwtService jwtService;





    @PostMapping("register")
    public User  register(@RequestBody User user){

        return service.saveUser(user);
    }

    @PostMapping("login")
    public String login(@RequestBody User user){

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));


        if(authentication.isAuthenticated())
            return jwtService.generateToken (user.getUsername());
        else
            return "Fail";
    }

}
