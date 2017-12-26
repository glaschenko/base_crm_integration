package trello;

import com.google.gson.annotations.SerializedName;

/*
 * Author: glaschenko
 * Created: 25.12.2017
 */
public class Card {
    private String id;
    @SerializedName("idBoard")
    private String boardId;
    private String name;
    private String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBoardId() {
        return boardId;
    }

    public void setBoardId(String boardId) {
        this.boardId = boardId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
