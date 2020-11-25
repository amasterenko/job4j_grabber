package ru.job4j.grabber;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {

    private Connection cnn;

    public PsqlStore(Properties cfg) {
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
            cnn = DriverManager.getConnection(
                    cfg.getProperty("jdbc.url"),
                    cfg.getProperty("jdbc.username"),
                    cfg.getProperty("jdbc.password")
            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement ps = cnn.prepareStatement(
                "INSERT INTO post (name, text, link, created) VALUES (?, ?, ?, ?)")) {
            ps.setString(1, post.getName());
            ps.setString(2, post.getContent());
            ps.setString(3, post.getSource());
            ps.setTimestamp(4, post.getCreated());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> outputList = new ArrayList<>();
        try (PreparedStatement ps = cnn.prepareStatement("SELECT * FROM post")) {
            ResultSet set = ps.executeQuery();
            while (set.next()) {
                Post post = new Post(
                        set.getString("name"),
                        set.getString("text"),
                        set.getString("link"),
                        set.getTimestamp("created"));
                outputList.add(post);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return outputList;
    }

    @Override
    public Post findById(int id) {
        Post resultPost = null;
        try (PreparedStatement ps = cnn.prepareStatement("SELECT * FROM post WHERE id = ?")) {
            ps.setInt(1, id);
            ResultSet set = ps.executeQuery();
            if (set.next()) {
                resultPost = new Post(
                                set.getString("name"),
                                set.getString("text"),
                                set.getString("link"),
                                set.getTimestamp("created"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultPost;
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }

    public static void main(String[] args) throws IOException {
        try (InputStream in = PsqlStore.class.getClassLoader()
                            .getResourceAsStream("app.properties")) {
            Properties config = new Properties();
            config.load(in);
            PsqlStore store = new PsqlStore(config);
            Post post1 = new Post(
                            "post1",
                            "text of the post1",
                            "http://www.ya.ru",
                                    new Timestamp(System.currentTimeMillis())
            );
            Post post2 = new Post(
                    "post2",
                    "text of the post2",
                    "http://www.rbc.ru",
                    new Timestamp(System.currentTimeMillis())
            );
            store.save(post1);
            store.save(post2);
            System.out.println(store.findById(1));
            store.getAll().forEach(System.out::println);

        }
    }
}
