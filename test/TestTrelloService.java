import com.google.gson.Gson;
import junit.framework.TestCase;
import trello.Card;
import trello.TrelloApiKey;
import trello.TrelloService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/*
 * Author: glaschenko
 * Created: 25.12.2017
 */
public class TestTrelloService extends TestCase {
    public void testGetCard(){
        TrelloApiKey apiKey = getTrelloApiKey();
        TrelloService service = new TrelloService(apiKey);
        Card card = null;
        try {
            card = service.getCard("5a39758a9fd3640b9c9e47d8");
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        System.out.println(card);

    }

    private TrelloApiKey getTrelloApiKey() {
        Gson gson = new Gson();
        return gson.fromJson(readToken(), TrelloApiKey.class);
    }

    private FileReader readToken(){
        File file = new File("trello_key.json");
        try {
            return new FileReader(file);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

}
