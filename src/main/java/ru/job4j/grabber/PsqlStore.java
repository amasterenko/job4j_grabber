package ru.job4j.grabber;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(PsqlStore.class);
    private final Connection cnn;

    public PsqlStore(Properties cfg) {
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
            cnn = DriverManager.getConnection(
                    cfg.getProperty("jdbc.url"),
                    cfg.getProperty("jdbc.username"),
                    cfg.getProperty("jdbc.password")
            );
        } catch (Exception e) {
            LOG.error("Exception", e);
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement ps = cnn.prepareStatement(
                "INSERT INTO post (name, text, link, created) "
                        + "VALUES (?, ?, ?, ?) ON CONFLICT DO NOTHING")) {
            ps.setString(1, post.getName());
            ps.setString(2, post.getContent());
            ps.setString(3, post.getSource());
            ps.setTimestamp(4, post.getCreated());
            ps.executeUpdate();
        } catch (SQLException e) {
            LOG.error("Exception", e);
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> outputList = new ArrayList<>();
        try (PreparedStatement ps = cnn.prepareStatement(
                "SELECT * FROM post ORDER BY created DESC")) {
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
            LOG.error("Exception", e);
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
            LOG.error("Exception", e);
        }
        return resultPost;
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }
}
