package com.carebridge.carebridge.Exception;

public class SlotAlreadyBookedException  extends RuntimeException{
    public SlotAlreadyBookedException(String message){
        super(message);
    }
}
