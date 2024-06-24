package com.trade.request;

import com.trade.domain.VerificationType;
import lombok.Data;

@Data
public class ForgetPasswordTokenRequest {

    private String sendTo;

    private VerificationType verificationType;
}
