package com.zerobase.fastlms;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TEST_01 {

    @Test
    void TEST_01(){

        String value = "2021-06-08";

        DateTimeFormatter formatter = DateTimeFormatter.BASIC_ISO_DATE;

        try{
            LocalDate.parse(value, formatter);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}
