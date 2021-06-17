package ru.job4j.grabber;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;

public class WebServer implements Output {
    private static final Logger LOG = LoggerFactory.getLogger(WebServer.class);
    private int port;

    public WebServer(Properties cfg) {
        this.port = Integer.parseInt(cfg.getProperty("webserver.port"));
    }

    @Override
    public void start(Store store) {
        new Thread(() -> {
            try (ServerSocket server = new ServerSocket(port)) {
                while (!server.isClosed()) {
                    Socket socket = server.accept();
                    try (OutputStream out = socket.getOutputStream();
                         PrintWriter print = new PrintWriter(
                                 new OutputStreamWriter(out, StandardCharsets.UTF_8), true);
                         BufferedReader in = new BufferedReader(
                                 new InputStreamReader(socket.getInputStream()))) {
                        if (!in.readLine().contains("GET")) {
                            continue;
                        }
                        print.println("HTTP/1.1 200 OK)");
                        print.println("content-type:text/html; charset=UTF-8");
                        List<Post> posts = store.getAll();
                        for (Post post : posts) {
                            print.println("<p><b>" +  post.getName() + "</b></p>");
                            print.println("<p>Created: " +  post.getCreated() + "</p>");
                            print.println("<p>Source: <a href=" + post.getSource() + "\">"
                                    + post.getSource() + "</a></p>");
                            print.println(post.getContent());
                            print.println("<hr>");
                        }
                    } catch (IOException io) {
                        LOG.error("Exception", io);
                    }
                }
            } catch (Exception e) {
                LOG.error("Exception", e);
            }
        }).start();
    }
}
