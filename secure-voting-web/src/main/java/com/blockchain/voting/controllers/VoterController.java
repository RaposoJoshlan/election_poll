package com.blockchain.voting.controllers;

import com.blockchain.voting.model.ERole;
import com.blockchain.voting.model.Role;
import com.blockchain.voting.model.Voter;
import com.blockchain.voting.payload.request.LoginRequest;
import com.blockchain.voting.payload.request.SignupRequest;
import com.blockchain.voting.payload.response.JwtResponse;
import com.blockchain.voting.payload.response.MessageResponse;
import com.blockchain.voting.services.CandidateService;
import com.blockchain.voting.services.VoterService;
import com.blockchain.voting.services.security.UserDetailsImpl;
import com.blockchain.voting.services.security.jwt.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.blockchain.voting.model.consts.AWSQldb.generateUniqueStringForGivenCandidateAsID;

/**
 * Created on 12 Sep 2022
 */

@RequestMapping({"/auth"})
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class VoterController {

    public final Logger log = LoggerFactory.getLogger(this.getClass());
    private final VoterService voterService;
    private final CandidateService candidateService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    public VoterController(VoterService voterService, CandidateService candidateService) {
        this.voterService = voterService;
        this.candidateService = candidateService;
    }

    @PostMapping({"/signin"})
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toList();

        return ResponseEntity.ok(new JwtResponse
                (
                        jwt,
                        userDetails.getId(),
                        userDetails.getUsername(),
                        userDetails.getUserFirstName(),
                        userDetails.getUserLastName(),
                        userDetails.getUserGender(),
                        userDetails.getUserLocation()
                ));
    }

    @PostMapping({"/signup"})
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) throws IOException {

        System.out.println(signUpRequest);

        if (voterService.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        // Create new user's account
        Voter user = new Voter(
                signUpRequest.getUsername(),
                encoder.encode(signUpRequest.getPassword()),
                signUpRequest.getVoterFirstName(),
                signUpRequest.getVoterLastName(),
                signUpRequest.getVoterGender(),
                signUpRequest.getVoterLocation());
        user.setId(generateUniqueStringForGivenCandidateAsID(user.getFirstName(), user.getLastName(), user.getGender().toString(), user.getLocation()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {

            Role userRole = voterService.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                Role userRole = voterService.findByName(ERole.ROLE_USER)
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                roles.add(userRole);
            });
        }

        user.setRoles(roles);

        System.out.println(user);
        voterService.addVoter(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

//    @PostMapping("/signout")
//    public ResponseEntity<?> logoutUser() {
//        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
//        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
//                .body(new MessageResponse("You've been signed out!"));
//    }
}
