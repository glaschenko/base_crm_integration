package trello;

/*
 * Author: glaschenko
 * Created: 25.12.2017
 */
public class TrelloApiKey {
    private String apiKey;
    private String token;

    public TrelloApiKey(String apiKey, String token) {
        this.apiKey = apiKey;
        this.token = token;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getToken() {
        return token;
    }
}
