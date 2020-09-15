package com.example.demo;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class SheetsAndJava {
    private static Sheets sheetsService;
    private static String APPLICATION_NAME = "GOOGLE SHEETS";
    // ID of GoogleSheets
    private static String SPREADSHEET_ID = "1JQG1YISIQIIWckvRv-XdMLBEHWXGX2T7M0fzbSllcbk";
    private static String CREDENTIALS_FILE_PATH = "/credentials.json";
    
    private static Credential authorize() throws IOException, GeneralSecurityException {
        InputStream in = SheetsAndJava.class.getResourceAsStream("/credentials.json");
        if (in == null) {
            System.out.println("Errors in");
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                JacksonFactory.getDefaultInstance(), new InputStreamReader(in)
        );

        List<String> scopes = Arrays.asList(SheetsScopes.SPREADSHEETS);

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(),
                clientSecrets, scopes)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File("tokens")))
                .setAccessType("offline")
                .build();

        Credential credential = new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver())
                .authorize("user");
        return credential;
    }

    public static Sheets getSheetsService() throws IOException, GeneralSecurityException {
        Credential credential = authorize();

        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(), credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static int readData() throws IOException, GeneralSecurityException {
        int length = 0;
        String range = "data";
        ValueRange response = sheetsService.spreadsheets().values()
                .get(SPREADSHEET_ID, range).execute();

        List<List<Object>> value = response.getValues();

        if(value == null || value.isEmpty()){
            System.out.println("NO DATA");
        }else {
            for (List row : value) {
                System.out.printf("%s - %s --- %s --- %s\n", row.get(0), row.get(1), row.get(2), row.get(3));
                //System.out.println(row);
            }
            //System.out.println(value.size());
            length = value.size();
        }
        return length;
    }

    public static void writeData(Entity entity) throws IOException, GeneralSecurityException{
        ValueRange appendBody = new ValueRange()
                .setValues(Arrays.asList(
                        Arrays.asList(entity.getId(), entity.getName(), entity.getLoan(), entity.getLoanPackage())
                ));
        AppendValuesResponse appendResult = sheetsService.spreadsheets().values()
                .append(SPREADSHEET_ID, "data", appendBody)
                .setValueInputOption("USER_ENTERED")
                .setInsertDataOption("INSERT_ROWS")
                .setIncludeValuesInResponse(true)
                .execute();

    }

    public void updateData() throws IOException {
        ValueRange body = new ValueRange()
                .setValues(Arrays.asList(
                        Arrays.asList("adb")
                ));
        UpdateValuesResponse result = sheetsService.spreadsheets().values()
                .update(SPREADSHEET_ID, "B5", body)
                .setValueInputOption("USER_ENTERED")
                .setIncludeValuesInResponse(true)
                .execute();

        System.out.printf("result:", result);
    }

    public static void intro() {
        System.out.println("HELLO MY FRIEND");
        System.out.println("1. Xem danh sách");
        System.out.println("2. Thêm vào danh sách");
        System.out.println("3. Thoát");
        System.out.println("LỰa chọn của bạn : ");

    }

    public static void main(String[] args) throws IOException, GeneralSecurityException{
        sheetsService = getSheetsService();
        int len = SheetsAndJava.readData();
        int stt = len;
        Entity entity = new Entity(stt, "hoang minh", 123456, 1);
        SheetsAndJava.writeData(entity);

    }

}
