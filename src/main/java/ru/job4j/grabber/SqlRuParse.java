package ru.job4j.grabber;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlRuParse implements Parse {
    private static String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
                                    + "AppleWebKit/537.36 (KHTML, like Gecko) "
                                    + "Chrome/86.0.4240.75 Safari/537.36";

    public static void main(String[] args) throws Exception {
        SqlRuParse parser = new SqlRuParse();
        List<Post> listOfPosts;
        listOfPosts = parser.list("https://www.sql.ru/forum/job-offers/");
    }

    String getMsgText(Document doc) throws IllegalArgumentException {
        StringBuilder rsl = new StringBuilder();
        try {
            Element msg = doc.select(".msgBody").get(1);
            for (int i = 0; i < msg.childNodeSize(); i++) {
                rsl.append(msg.childNode(i).toString()).append("\n");
            }
            return rsl.toString();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    Timestamp getMsgDate(Document doc) throws IllegalArgumentException {
        /*getting a date from the string like " 13 май 20, 21:58&nbsp..."
         or "сегодня, 21:58&nbsp..." */
        try {
            Element date = doc.select(".msgFooter").get(0);
            Pattern pattern = Pattern.compile("[\\w|А-Яа-я][^&]*");
            Matcher matcher = pattern.matcher(date.childNode(0).toString());
            matcher.find();
            return DateParse.strToTimestamp(matcher.group(0));
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    String getMsgHeader(Document doc) throws IllegalArgumentException {
        try {
            Element header = doc.select(".messageHeader").get(0);
            return header.childNode(1).toString().replace("&nbsp;", "");
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public List<Post> list(String link) {
        List<Post> outputList = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(link)
                    .userAgent(userAgent)
                    .get();
            Elements rowPost = doc.select(".postslisttopic");
            for (Element td : rowPost) {
                Element href = td.child(0);
                String postLink = href.attr("href");
                outputList.add(detail(postLink));
            }
        } catch (IOException e) {
            return null;
        }
        return outputList;
    }

    @Override
    public Post detail(String link) {
        try {
            Document doc = Jsoup.connect(link)
                    .userAgent(userAgent)
                    .get();
            return new Post(getMsgHeader(doc), getMsgText(doc), link, getMsgDate(doc));
        } catch (IOException e) {
            return null;
        }
    }
}