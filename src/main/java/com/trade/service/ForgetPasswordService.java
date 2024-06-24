package com.trade.service;

import com.trade.Entity.ForgetPasswordToken;
import com.trade.Entity.User;
import com.trade.domain.VerificationType;

public interface ForgetPasswordService {

    ForgetPasswordToken createToken(User user, String id, String otp, VerificationType verificationType,String sendTo);

    ForgetPasswordToken findById(String id);

    ForgetPasswordToken findByUser(Long userId);

    void deleteToken(ForgetPasswordToken token);
}
