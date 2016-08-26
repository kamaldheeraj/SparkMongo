package com.constants;

/**
 * Enum class to define response codes being used in the application.
 * Each enum has a string values attached to it.
 * @author Kamal Dandamudi on 8/26/16.
 */
public enum ResponseCodes{
    Success("Success"),
    Failure("Failure");

    private final String codeString;

    /**
     * Private constructor called internally to assign a string values to each Enum.
     * @param codeString String value representing the Response Code
     */
    ResponseCodes(String codeString){
        this.codeString = codeString;
    }

    /**
     * Method to get the String value of an Enumeration.
     * @return the string value of the enumeration.
     */
    public String getCodeString(){
        return codeString;
    }
}
