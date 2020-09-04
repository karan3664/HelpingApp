package com.aryupay.helpingapp.api;


import java.io.IOException;

/**
 * Created by Karan Brahmaxatriya on 20-Sept-18.
 */
public class NoConnectivityException extends IOException {
    @Override
    public String getMessage() {
        return "Internet Connectivity not found.";
    }
}