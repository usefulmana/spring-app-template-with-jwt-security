package security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import security.demo.exceptions.BadRequestException;
import security.demo.exceptions.ResourceAlreadyExistsException;
import security.demo.models.User;
import security.demo.models.VerificationToken;
import security.demo.repositories.UserRepository;
import security.demo.repositories.VerificationTokenRepository;
import security.demo.responses.GenericResponse;
import security.demo.responses.LoginResponse;
import security.demo.responses.LoginRequest;
import security.demo.responses.SignUpRequest;
import security.demo.services.EmailVerificationService;
import security.demo.services.MyUserDetailsService;
import security.demo.utils.JwtUtil;

import javax.mail.MessagingException;
import java.io.IOException;
import java.nio.file.attribute.UserPrincipal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private EmailVerificationService emailVerificationService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) throws Exception{
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        }
        catch (BadCredentialsException e){
            throw new Exception("Incorrect Username or Password", e);
        }

        final UserDetails userDetails = myUserDetailsService
                .loadUserByUsername(request.getUsername());

        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new LoginResponse(jwt,true, "Successfully Logged In!"));
    }

    @PostMapping(value = "/register")
    public ResponseEntity<?> register(@RequestBody SignUpRequest request) throws Exception {
        Optional<User> optionalUser = userRepository.findByUsername(request.getUsername());
        if (optionalUser.isPresent()){
            throw new ResourceAlreadyExistsException("This username is already taken!");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(request.getRoles());
        userRepository.save(user);

        final UserDetails userDetails = myUserDetailsService
                .loadUserByUsername(request.getUsername());

        final String jwt = jwtUtil.generateToken(userDetails);

        // Uncomment to include email verification
//        emailVerificationService.sendVerificationEmail(user, null);

        return ResponseEntity.ok(new LoginResponse(jwt,true, "Successfully Registered!"));
    }
 // TODO Implement Current User Looking Into UserPrincipal stuff
    @RequestMapping(value = "/resend", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<?> resendToken(Principal principal) throws IOException, MessagingException {
        Optional<User> optionalUser = userRepository.findByUsername(principal.getName());

        if (optionalUser.isEmpty()){
            throw new BadRequestException("User Do Not Exist!");
        }

        User user = optionalUser.get();
        emailVerificationService.resendToken(user);
        return ResponseEntity.ok(new GenericResponse(true, "Successfully Resent!"));
    }

    @RequestMapping(value = "/confirm", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<?> verifyToken(@RequestParam(value = "token") String token){
        try {
            emailVerificationService.verifyToken(token);
            return ResponseEntity.ok(new GenericResponse(true, "Verified!"));
        }
        catch (BadRequestException e){
            return new ResponseEntity<>(new GenericResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

}
