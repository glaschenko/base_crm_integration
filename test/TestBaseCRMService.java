import base.BaseCRMService;
import junit.framework.TestCase;
import lombok.Cleanup;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/*
 * Copyright (c) 2016 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.

 * Author: glaschenko
 * Created: 10.12.2017
 */
public class TestBaseCRMService extends TestCase {
    public void testUserSelf(){
        String token = readToken();
        BaseCRMService service = new BaseCRMService(token);
        try {
            JSONObject self = service.getUserSelf();
            self.keySet().forEach(e -> System.out.println(e + ": " + self.get(e).toString()));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public void testCreateContact(){
        String token = readToken();
        BaseCRMService service = new BaseCRMService(token);
        JSONObject contactData = new JSONObject();
        JSONObject addressData = new JSONObject();
        ArrayList<String> tags = new ArrayList<>();
        tags.add("auto_import");
        contactData.put("name", "Haulmont");
        contactData.put("is_organization", true);
        addressData.put("postal_code","443090");
        addressData.put("line1","Gastello 43A");
        addressData.put("city","Samara");
        addressData.put("country","Russia");
        try {
            service.createContact(contactData,addressData,tags);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        System.out.println("Status: " + service.getLastStatusCode());
        System.out.println("Errors:" + service.getLastError());
    }

    private String readToken(){
        File file = new File("token.txt");
        try {
            @Cleanup Scanner s = new Scanner(file);
            return s.next();
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }
}
