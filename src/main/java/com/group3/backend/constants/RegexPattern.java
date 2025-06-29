package com.group3.backend.constants;

public class RegexPattern {
    public static final String PHONE_NUMBER = "^\\d{10}$";
    public static final String PASSWORD = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{12,}$";
}
