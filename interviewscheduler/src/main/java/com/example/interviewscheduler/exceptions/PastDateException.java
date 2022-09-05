package com.example.interviewscheduler.exceptions;

public class PastDateException extends Exception {
    
    public PastDateException(String message){
        super(message);
    }

}
