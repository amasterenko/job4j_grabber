package ru.job4j.grabber;

import java.sql.Timestamp;
import java.util.List;

public class Post {
    private String name;
    private String content;
    private String source;
    private Timestamp created;

    public Post(String name, String content, String source, Timestamp created) {
        this.name = name;
        this.content = content;
        this.source = source;
        this.created = created;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public String getSource() {
        return source;
    }

    public Timestamp getCreated() {
        return created;
    }
}
