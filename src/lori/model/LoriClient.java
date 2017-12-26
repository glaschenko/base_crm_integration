package lori.model;

import java.util.UUID;

/*
 * Author: glaschenko
 * Created: 26.12.2017
 */
public class LoriClient {
    private UUID id;
    private String name;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
