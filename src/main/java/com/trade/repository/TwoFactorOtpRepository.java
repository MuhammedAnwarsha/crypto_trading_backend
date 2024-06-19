package com.trade.repository;

import com.trade.Entity.TwoFactorOtp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TwoFactorOtpRepository extends JpaRepository<TwoFactorOtp,String> {

    TwoFactorOtp findByUserId(Long userid);
}
