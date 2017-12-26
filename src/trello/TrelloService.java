package trello;

import base.BaseCRMService;
import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


/*
 * Author: glaschenko
 * Created: 25.12.2017
 */
public class TrelloService {
    private static final String URL = "https://trello.com/1/";

    private TrelloApiKey key;
    private int lastStatusCode;
    private String lastError;

    public TrelloService(TrelloApiKey key) {
        this.key = key;
    }

    public Card getCard(String cardId) throws IOException {
        HttpClientBuilder builder = HttpClientBuilder.create();
        HttpClient client = builder.build();
        HttpGet request = new HttpGet();

        URI uri = getUriWithAuthParams("cards/" + cardId);
        request.setURI(uri);

        HttpResponse response = client.execute(request);

        StringBuilder sb = BaseCRMService.readHttpEntity(response.getEntity());
        Gson gson = new Gson();
        return gson.fromJson(sb.toString(), Card.class);
    }

    private URI getUriWithAuthParams(String path) {
        URI uri;
        try {
            URIBuilder uriBuilder = new URIBuilder(URL + path);
            uriBuilder.addParameter("key", key.getApiKey());
            uriBuilder.addParameter("token", key.getToken());
            uri = uriBuilder.build();
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
        return uri;
    }
}
