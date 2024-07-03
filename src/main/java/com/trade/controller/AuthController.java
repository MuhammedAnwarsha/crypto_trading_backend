package com.trade.controller;

import com.trade.Entity.TwoFactorOtp;
import com.trade.Entity.User;
import com.trade.config.JwtProvider;
import com.trade.repository.UserRepository;
import com.trade.response.AuthResponse;
import com.trade.service.CustomUserDetailsService;
import com.trade.service.EmailService;
import com.trade.service.TwoFactorOtpService;
import com.trade.service.WatchlistService;
import com.trade.utils.OtpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private TwoFactorOtpService twoFactorOtpService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private WatchlistService watchlistService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> registerUser(@RequestBody User user) throws Exception {

        User isEmailExist = userRepository.findByEmail(user.getEmail());

        if (isEmailExist != null){
            throw new Exception("email is already used with another account");
        }

        User newUser = new User();

        newUser.setFullName(user.getFullName());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(newUser);

        watchlistService.createWatchlist(savedUser);

        Authentication auth = new UsernamePasswordAuthenticationToken(user.getEmail(),user.getPassword());

        SecurityContextHolder.getContext().setAuthentication(auth);

        String jwt = JwtProvider.generateToken(auth);

        AuthResponse res = new AuthResponse();
        res.setJwt(jwt);
        res.setStatus(true);
        res.setMessage("register success");

        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PostMapping("/signing")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody User user) throws Exception {

        String username = user.getEmail();
        String password = user.getPassword();

        Authentication auth = authenticate(username,password);

        SecurityContextHolder.getContext().setAuthentication(auth);

        String jwt = JwtProvider.generateToken(auth);

        User authUser = userRepository.findByEmail(username);

        if (user.getTwoFactorAuth().isEnabled()){
            AuthResponse res = new AuthResponse();
            res.setMessage("Two factor auth is enabled");
            res.setTwoFactorAuthEnabled(true);
            String otp = OtpUtils.generateOtp();

            TwoFactorOtp oldTwoFactorOtp = twoFactorOtpService.findByUser(authUser.getId());
            if (oldTwoFactorOtp!=null){
                twoFactorOtpService.deleteTwoFactorOtp(oldTwoFactorOtp);
            }

            TwoFactorOtp newTwoFactorOtp = twoFactorOtpService.createTwoFactorOtp(authUser,otp,jwt);
            emailService.sendVerificationOtpEmail(username,otp);
            res.setSession(newTwoFactorOtp.getId());
            return new ResponseEntity<>(res,HttpStatus.ACCEPTED);
        }

        AuthResponse res = new AuthResponse();
        res.setJwt(jwt);
        res.setStatus(true);
        res.setMessage("login success");

        return new ResponseEntity<>(res, HttpStatus.ACCEPTED);
    }

    private Authentication authenticate(String username, String password) {

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        if (userDetails == null){
            throw new BadCredentialsException("invalid username");
        }
        if (!passwordEncoder.matches(password,userDetails.getPassword())){
            throw new BadCredentialsException("invalid password");
        }

        return new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
    }

    @PostMapping("/two-factor/otp/{otp}")
    public ResponseEntity<AuthResponse> verifySigningOtp(@PathVariable String otp,
                                                         @RequestParam String id) throws Exception {
        TwoFactorOtp twoFactorOtp = twoFactorOtpService.findById(id);

        if (twoFactorOtpService.verifyTwoFactorOtp(twoFactorOtp,otp)){
            AuthResponse res = new AuthResponse();
            res.setMessage("Two factor authentication verified");
            res.setTwoFactorAuthEnabled(true);
            res.setJwt(twoFactorOtp.getJwt());
            return new ResponseEntity<>(res,HttpStatus.OK);
        }
        throw new Exception("invalid otp");
    }
}
