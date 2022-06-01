package com.example.demo.src.sms;

import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.sms.model.Sms;
import com.example.demo.src.user.UserService;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject; // JSON객체 생성
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Random;

@Service
@Slf4j
public class SmsService {
    private String randomNum = "";
    private String phoneNum = "";

    private Sms sms;
    private final UserService userService;
//    private final UserController userController;


    public SmsService(UserService userService){
        this.userService = userService;
    }

    public void sendMessage(String phoneNum) {
        this.phoneNum = phoneNum;
//        String randomNumber = "";
        String api_key = Secret.SMS_API_KEY;
        String api_secret = Secret.SMS_API_SECRET_KEY;

        Message coolsms = new Message(api_key, api_secret);

        Random rand  = new Random();
        for(int i = 0; i < 6; i++) {
            String ran = Integer.toString(rand.nextInt(10));
            this.randomNum += ran;
        }

        HashMap<String, String> params = new HashMap<String, String>();

        params.put("to", phoneNum);
        params.put("from", Secret.FROM_NUMBER);
        params.put("type", "SMS");
        params.put("text", "[grabMe] 인증번호 " + this.randomNum + " 를 입력하세요.");
        params.put("app_version", "test app 1.2"); // application name and version

        try {
            JSONObject obj = (JSONObject) coolsms.send(params);
            System.out.println(obj.toString());
        } catch (CoolsmsException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCode());
        }
    }

    public void certificationNum(String randomNum) throws BaseException {
        if (this.randomNum.equals(randomNum)){
            sms = new Sms(phoneNum);
            userService.createUser(sms.getPhoneNum());
        }
    }
}
