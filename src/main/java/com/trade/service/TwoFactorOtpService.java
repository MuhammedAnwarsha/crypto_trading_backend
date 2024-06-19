package com.trade.service;

import com.trade.Entity.TwoFactorOtp;
import com.trade.Entity.User;

public interface TwoFactorOtpService {

    TwoFactorOtp createTwoFactorOtp(User user,String otp, String jwt);

    TwoFactorOtp findByUser(Long userId);

    TwoFactorOtp findById(String id);

    boolean verifyTwoFactorOtp(TwoFactorOtp twoFactorOtp,String otp);

    void deleteTwoFactorOtp(TwoFactorOtp twoFactorOtp);
}
