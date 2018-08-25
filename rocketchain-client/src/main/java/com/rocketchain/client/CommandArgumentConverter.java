package com.rocketchain.client;

public class CommandArgumentConverter {

    public static Integer toInt(String optionName, String value){
        Integer minValue = Integer.MIN_VALUE;
        try {
            if (value != null) {
                Integer intValue = Integer.parseInt(value);
                if( intValue < minValue) {
                    System.out.println("The option should be greater than or equal to minValue.");
                    System.exit(-1);
                }
                return intValue;
            }
            return null;
        } catch ( NumberFormatException e  ) {
            System.out.println("The option should be an integer.");
            System.exit(-1);
            return null;
        }
    }
}
