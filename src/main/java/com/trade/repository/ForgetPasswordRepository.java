package com.trade.repository;

import com.trade.Entity.ForgetPasswordToken;
import com.trade.service.ForgetPasswordService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForgetPasswordRepository extends JpaRepository<ForgetPasswordToken,String> {

    ForgetPasswordToken findByUserId(Long userId);
}
