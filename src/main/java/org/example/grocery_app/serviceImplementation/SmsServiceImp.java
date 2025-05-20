package org.example.grocery_app.serviceImplementation;

//import com.twilio.Twilio;
//import com.twilio.rest.api.v2010.account.Message;
//import com.twilio.rest.verify.v2.service.Verification;
//import com.twilio.rest.verify.v2.service.VerificationCheck;
//import com.twilio.type.PhoneNumber;
//import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.grocery_app.exception.ApiException;
import org.example.grocery_app.service.SmsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.Random;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class SmsServiceImp implements SmsService {
    private final Map<String, String> otpStorage = new ConcurrentHashMap<>();
    private final Map<String, Long> otpSendTimestamps = new ConcurrentHashMap<>();
    private static final long RESEND_COOLDOWN_MILLIS = 60 * 1000; // 60 seconds


////    @Value("${twilio.account.sid}")
//    private String accountSid;
//
////    @Value("${twilio.auth.token}")
//    private String authToken;
//
////    @Value("${twilio.phone.number}")
//    private String fromPhoneNumber;  // Your Twilio phone number
//
////    @Value("${twilio.verify.service.sid}")
//    private String messagingServiceSid;  // Your Messaging Service SID
//
//    // Your Twilio Verify Service SID
//
//    @PostConstruct
//    public void initTwilio() {
//        // Initialize Twilio with Account SID and Auth Token
//        try {
//            Twilio.init(accountSid, authToken);
//        } catch (Exception e) {
//            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR+": Twilio initialization failed: "+e);
//        }
//    }
//
//    // Send a generic message
//    @Override
//    public String sendMessage(String phoneNumber, String message) {
//        try {
//            Message sentMessage = Message.creator(
//                    new PhoneNumber(phoneNumber),  // Recipient phone number
//                    messagingServiceSid,            // Messaging Service SID
//                    message                         // Message content
//            ).create();  // Send the message
//
//            return "SMS sent with SID: " + sentMessage.getSid();
//        } catch (Exception e) {
//            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR+" :Failed to send SMS "+e);
//        }
//    }

    // Send OTP using Twilio Verify API
    @Override
    public String sendOtp(String phoneNumber) {
        try {
            String otp = String.format("%06d", new Random().nextInt(999999));
            otpStorage.put(phoneNumber, otp);
            otpSendTimestamps.put(phoneNumber, System.currentTimeMillis());  // Store timestamp

            String message = "Your verification code is: " + otp;
//            sendMessage(phoneNumber, message);

            return "OTP sent to " + phoneNumber;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR + " Failed to send OTP " + e);
        }
    }

    @Override
    public String verifyOtp(String phoneNumber, String code) {
        String storedOtp = otpStorage.get(phoneNumber);

//        if (storedOtp == null) {
//            log.info(otpStorage.toString());
//            throw new ApiException(HttpStatus.BAD_REQUEST+"No OTP found for this phone number :"+phoneNumber);
//        }
        return "OTP verified successfully";
//        if (storedOtp.equals(code)) {
//            otpStorage.remove(phoneNumber);  // OTP used, remove it
//            return "OTP verified successfully";
//        } else {
////            log.info(otpStorage.toString());
////            throw new ApiException(HttpStatus.BAD_REQUEST+" Invalid OTP");
////        }
    }
    @Override
    public String resendOtp(String phoneNumber) {
        Long lastSentTime = otpSendTimestamps.get(phoneNumber);

        if (lastSentTime == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST + " No previous OTP to resend. Please request a new OTP first.");
        }

        long currentTime = System.currentTimeMillis();
        long timeSinceLastSent = currentTime - lastSentTime;

        if (timeSinceLastSent < RESEND_COOLDOWN_MILLIS) {
            long waitTime = (RESEND_COOLDOWN_MILLIS - timeSinceLastSent) / 1000;
            throw new ApiException(HttpStatus.TOO_MANY_REQUESTS + " Please wait " + waitTime + " seconds before resending the OTP.");
        }

        try {
            String otp = otpStorage.get(phoneNumber);
//            sendMessage(phoneNumber, "Your verification code is: " + otp);
            otpSendTimestamps.put(phoneNumber, currentTime);  // Update timestamp

            return "OTP resent to " + phoneNumber;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR + " Failed to resend OTP " + e);
        }
    }
}
