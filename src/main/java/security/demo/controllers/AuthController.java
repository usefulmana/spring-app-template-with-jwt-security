package security.demo.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import security.demo.models.User;
import security.demo.responses.ApiResponse;
import security.demo.responses.LoginRequest;
import security.demo.responses.SignUpRequest;
import security.demo.services.MyUserDetailsService;
import security.demo.utils.JwtUtil;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) throws Exception{
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUserName(), request.getPassword())
            );
        }
        catch (BadCredentialsException e){
            throw new Exception("Incorrect Username or Password", e);
        }

        final UserDetails userDetails = myUserDetailsService
                .loadUserByUsername(request.getUserName());

        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new ApiResponse(jwt,true));
    }

//    @PostMapping(value = "/register")
//    public ResponseEntity<?> login(@RequestBody SignUpRequest request) throws Exception {
//
//    }
}
