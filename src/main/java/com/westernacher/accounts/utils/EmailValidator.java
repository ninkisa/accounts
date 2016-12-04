package com.westernacher.accounts.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator {
    private static final String EMAIL_REGEX="^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";
    private static Pattern pattern;
    private Matcher matcher;
     
    public EmailValidator(){
        pattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
    }
    /**
     * This method validates the input email address with EMAIL_REGEX pattern
     * @param email
     * @return boolean
     */
    public boolean validateEmail(String email){
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
