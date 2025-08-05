package com.backend.ToDoList.utils;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class OtpUtils {
    public String generateOtpCode() {
        Random r = new Random();
        int number = r.nextInt(0, 999999);
        String code = String.valueOf(number);
        while (code.length() < 6) {
            code = "0" + code;
        }
        return code;
    }
}
