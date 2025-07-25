// PaymentGatewayServiceImp.java
package org.example.grocery_app.serviceImplementation;

import com.razorpay.*;
import lombok.extern.slf4j.Slf4j;
import org.example.grocery_app.exception.ApiException;
import org.example.grocery_app.service.PaymentGatewayService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PaymentGatewayServiceImp implements PaymentGatewayService {

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.secret.key}")
    private String razorpayKeySecret;

    @Override
    public String initiatePartialRefund(String paymentId, double totalAmount) {
        try {
            RazorpayClient client = new RazorpayClient("rzp_live_UDn0rqtiftjbPd", "78IeWxDMwC8M6C8YUffofuKh");

            double refundAmount = totalAmount * 1;
            int refundAmountInPaise = (int) (refundAmount * 100);

            JSONObject options = new JSONObject();
            options.put("amount", refundAmountInPaise);
            log.info("refund paymentId :{}", paymentId);
            Refund refund = client.payments.refund(paymentId, options);

            return "Refund initiated: " + refund.get("id");
        } catch (RazorpayException e) {
//            e.printStackTrace();
            log.info("error of the initiate refund :{}", e.getMessage());
            throw  new ApiException(e.getMessage());
//            return "Refund failed: " + e.getMessage();
        }
    }
}
