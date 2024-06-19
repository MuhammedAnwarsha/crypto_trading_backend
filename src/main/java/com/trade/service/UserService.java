package com.trade.service;

import com.trade.Entity.User;
import com.trade.domain.VerificationType;

public interface UserService {

    public User findUserProfileByJwt(String jwt) throws Exception;

    public User findUserByEmail(String email) throws Exception;

    public User findUserById(Long userId) throws Exception;

    public User enableTwoFactorAuthentication(VerificationType verificationType,String sendTo,User user);

    User updatePassword(User user, String newPassword);

}
