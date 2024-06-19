package com.trade.service;

import com.trade.Entity.User;
import com.trade.Entity.VerificationCode;
import com.trade.domain.VerificationType;

public interface VerificationCodeService {

    VerificationCode sendVerificationCode(User user, VerificationType verificationType);

    VerificationCode getVerificationCodeById(Long id) throws Exception;

    VerificationCode getVerificationCodeByUser(Long userId);

    void deleteVerificationCodeById(VerificationCode verificationCode);
}
