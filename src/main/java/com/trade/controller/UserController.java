package com.trade.controller;

import com.trade.Entity.ForgetPasswordToken;
import com.trade.Entity.User;
import com.trade.Entity.VerificationCode;
import com.trade.domain.VerificationType;
import com.trade.request.ForgetPasswordTokenRequest;
import com.trade.request.ResetPasswordRequest;
import com.trade.response.ApiResponse;
import com.trade.response.AuthResponse;
import com.trade.service.EmailService;
import com.trade.service.ForgetPasswordService;
import com.trade.service.UserService;
import com.trade.service.VerificationCodeService;
import com.trade.utils.OtpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private VerificationCodeService verificationCodeService;

    @Autowired
    private ForgetPasswordService forgetPasswordService;

    @GetMapping("/api/users/profile")
    public ResponseEntity<User> getUserProfile(@RequestHeader("Authorization")String jwt) throws Exception {

        User user = userService.findUserProfileByJwt(jwt);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/api/users/verification/{verificationType}/send-otp")
    public ResponseEntity<String> sendVerificationOtp(@RequestHeader("Authorization")String jwt,
                                                      @PathVariable VerificationType verificationType) throws Exception {

        User user = userService.findUserProfileByJwt(jwt);

        VerificationCode verificationCode = verificationCodeService.getVerificationCodeByUser(user.getId());

        if (verificationCode==null){
            verificationCode = verificationCodeService.sendVerificationCode(user,verificationType);
        }

        if (verificationType.equals(VerificationType.EMAIL)){
            emailService.sendVerificationOtpEmail(user.getEmail(), verificationCode.getOtp());
        }


        return new ResponseEntity<>("verification otp sent successfully", HttpStatus.OK);
    }

    @PatchMapping("/api/users/enable-two-factor/verify-otp/{otp}")
    public ResponseEntity<User> enableTwoFactorAuthentication(@PathVariable String otp,
                                                              @RequestHeader("Authorization")String jwt) throws Exception {

        User user = userService.findUserProfileByJwt(jwt);

        VerificationCode verificationCode = verificationCodeService.getVerificationCodeByUser(user.getId());

        String sendTo = verificationCode.getVerificationType().equals(VerificationType.EMAIL)? verificationCode.getEmail() : verificationCode.getMobile();

        boolean isVerified = verificationCode.getOtp().equals(otp);

        if (isVerified){
            User updatedUser = userService.enableTwoFactorAuthentication(verificationCode.getVerificationType(),sendTo,user);

            verificationCodeService.deleteVerificationCodeById(verificationCode);

            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        }

        throw new Exception("wrong otp");
    }

    @PostMapping("/auth/users/reset-password/send-otp")
    public ResponseEntity<AuthResponse> sendForgetPasswordOtp(@RequestBody ForgetPasswordTokenRequest req) throws Exception {

        User user = userService.findUserByEmail(req.getSendTo());
        String otp = OtpUtils.generateOtp();
        UUID uuid = UUID.randomUUID();
        String id = uuid.toString();

        ForgetPasswordToken token = forgetPasswordService.findByUser(user.getId());

        if (token == null){
            token = forgetPasswordService.createToken(user,id, otp, req.getVerificationType(), req.getSendTo());
        }

        if (req.getVerificationType().equals(VerificationType.EMAIL)){
            emailService.sendVerificationOtpEmail(user.getEmail(), token.getOtp());
        }

        AuthResponse response = new AuthResponse();

        response.setSession(token.getId());
        response.setMessage("forget password otp sent successfully");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/auth/users/reset-password/verify-otp")
    public ResponseEntity<ApiResponse> resetPassword(@RequestParam String id,
                                              @RequestBody ResetPasswordRequest req) throws Exception {

        ForgetPasswordToken forgetPasswordToken = forgetPasswordService.findById(id);

        boolean isVerified = forgetPasswordToken.getOtp().equals(req.getOtp());

        if (isVerified){
            userService.updatePassword(forgetPasswordToken.getUser(),req.getPassword());
            ApiResponse res = new ApiResponse();
            res.setMessage("password update successfully");
            return new ResponseEntity<>(res,HttpStatus.ACCEPTED);
        }
        throw new Exception("wrong otp");
    }
}
