import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.*;

/*
 * Copyright (c) 2016 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.

 * Author: glaschenko
 * Created: 10.12.2017
 */
public class BaseCRMService {
    private static final String URL = "https://api.getbase.com/v2";
    private static final int OWNER_ID = 1282136;

    private String token;
    private int lastStatusCode;
    private String lastError;

    public BaseCRMService(String token) {
        this.token = token;
    }

    public JSONObject getUserSelf() throws IOException {
        String path = "/users/self";

        HttpClientBuilder builder = HttpClientBuilder.create();
        HttpClient client = builder.build();
        HttpGet request = initGetRequest(path);
        HttpResponse response = client.execute(request);

        StringBuilder sb = readHttpEntity(response.getEntity());
        JSONObject obj = new JSONObject(sb.toString());
        return obj.getJSONObject("data");
    }

    public int createContact(JSONObject data, JSONObject address, ArrayList<String> tags) throws IOException {
        JSONObject obj = new JSONObject();
        obj.put("data", data);
        JSONObject dataObject = obj.getJSONObject("data");
        dataObject.put("address", address);
        dataObject.put("tags", new JSONArray(tags));
        StringEntity entity = wrapToStringEntity(obj);

        HttpClientBuilder builder = HttpClientBuilder.create();
        HttpClient client = builder.build();
        HttpPost httpPost = initPostRequest("/contacts");
        httpPost.setEntity(entity);
        System.out.println(readHttpEntity(httpPost.getEntity()));
        HttpResponse response = client.execute(httpPost);
//        return response.get
        StringBuilder sb = readHttpEntity(response.getEntity());
        System.out.println(sb);

        readStatusResponse(response, sb);
        return lastStatusCode;
    }

//    private boolean printTags(ArrayList<String> tags) {
//        JSONArray jsonArray = ;
//        StringBuilder res = new StringBuilder();
//        res.append("[");
//        tags.forEach(e -> res.append("\"").append(e).append("\""));
//        return "[";
//    }

    private void readStatusResponse(HttpResponse response, StringBuilder sb) {
        lastStatusCode = response.getStatusLine().getStatusCode();
        JSONObject responseObj = new JSONObject(sb.toString());
        if(responseObj.has("errors")){
            JSONArray errorsArray = responseObj.getJSONArray("errors");
            errorsArray.forEach(e -> parseError(((JSONObject)e).getJSONObject("error")));
        }
    }

    private void parseError(JSONObject errorObj) {
        if(errorObj != null) {
            lastError = "-------------------------------------------------\n";
            for (Iterator<String> iterator = errorObj.keys(); iterator.hasNext(); ) {
                String key = iterator.next();
                lastError += key + ": " + errorObj.get(key) + "\n";
            }
        }
    }

    private void addBasicData(JSONObject dataObject) {
        dataObject.put("created_at", new Date());
        dataObject.put("updated_at", new Date());
        dataObject.put("creator_id", OWNER_ID);
        dataObject.put("owner_id", OWNER_ID);
    }

    private StringEntity wrapToStringEntity(JSONObject obj) {
        StringEntity entity;
        try {
            entity = new StringEntity(obj.toString());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
        return entity;
    }

    private void addMeta(JSONObject obj, String typeValue) {
        Map<String, String> meta = new HashMap<>();
        meta.put("type",typeValue);
        obj.put("meta", meta);
    }

    private HttpGet initGetRequest(String path) {
        HttpGet request = new HttpGet(URL + path);
        addAuthHeaders(request);
        return request;
    }

    private HttpPost initPostRequest(String path) {
        HttpPost request = new HttpPost(URL + path);
        addAuthHeaders(request);
        request.addHeader("Content-Type", "application/json");
        return request;
    }

    private void addAuthHeaders(HttpRequestBase request) {
        request.addHeader("Accept", "application/json");
        request.addHeader("Authorization", "Bearer " + token);
    }

    private StringBuilder readHttpEntity(HttpEntity entity) throws IOException {
        InputStreamReader in = new InputStreamReader(entity.getContent());
        BufferedReader rd = new BufferedReader(in);
        StringBuilder sb = new StringBuilder();
        String s = rd.readLine();
        while (s != null){
            sb.append(s);
            s = rd.readLine();
        }
        return sb;
    }

    public int getLastStatusCode() {
        return lastStatusCode;
    }

    public String getLastError() {
        return lastError;
    }
}
