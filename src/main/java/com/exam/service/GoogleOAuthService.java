package com.exam.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class GoogleOAuthService {
    
    private static final String APPLICATION_NAME = "Smart Exam System";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList("https://www.googleapis.com/auth/userinfo.email");
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    
    private static NetHttpTransport HTTP_TRANSPORT;
    
    static {
        try {
            HTTP_TRANSPORT = new NetHttpTransport();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Authenticate user with Google OAuth
     * @return Userinfo object containing user email and other info, or null if authentication fails
     */
    public static Userinfo authenticate() {
        try {
            // Load client secrets
            GoogleClientSecrets clientSecrets;
            try {
                InputStreamReader reader = new InputStreamReader(
                    GoogleOAuthService.class.getResourceAsStream(CREDENTIALS_FILE_PATH));
                if (reader == null) {
                    System.err.println("Error: credentials.json not found in src/main/resources/");
                    System.err.println("Please follow the setup instructions in GOOGLE_OAUTH_SETUP.md");
                    return null;
                }
                clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, reader);
            } catch (Exception e) {
                System.err.println("Error loading credentials.json: " + e.getMessage());
                System.err.println("Please ensure the file exists in src/main/resources/");
                System.err.println("For development, you can use traditional login instead.");
                return null;
            }
            
            // Build flow and trigger user authorization request
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                    .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)))
                    .setAccessType("offline")
                    .build();
            
            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
            Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
            
            // Get user info
            Oauth2 service = new Oauth2.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();
            
            Userinfo userInfo = service.userinfo().get().execute();
            return userInfo;
            
        } catch (Exception e) {
            System.err.println("Google OAuth authentication failed: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Get user email from Google OAuth
     * @return email address or null
     */
    public static String getUserEmail() {
        Userinfo userInfo = authenticate();
        return userInfo != null ? userInfo.getEmail() : null;
    }
    
    /**
     * Validate if the email belongs to the allowed domain
     * @param email The email to validate
     * @param allowedDomain The allowed domain (e.g., "apsit.edu.in")
     * @return true if email belongs to allowed domain, false otherwise
     */
    public static boolean isValidEmailDomain(String email, String allowedDomain) {
        if (email == null || email.isEmpty() || allowedDomain == null || allowedDomain.isEmpty()) {
            return false;
        }
        return email.toLowerCase().endsWith("@" + allowedDomain.toLowerCase());
    }
    
    /**
     * Get user email from Google OAuth and validate domain
     * @param allowedDomain The allowed email domain (e.g., "apsit.edu.in")
     * @return email address if valid domain, null otherwise
     */
    public static String getUserEmailWithDomainValidation(String allowedDomain) {
        String email = getUserEmail();
        if (email != null && isValidEmailDomain(email, allowedDomain)) {
            return email;
        }
        return null;
    }
}
