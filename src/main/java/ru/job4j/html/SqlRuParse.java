package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlRuParse {

    public static void main(String[] args) throws Exception {
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
                + "AppleWebKit/537.36 (KHTML, like Gecko) "
                + "Chrome/86.0.4240.75 Safari/537.36";
        SqlRuParse parser = new SqlRuParse();
        for (int i = 1; i <= 5; i++) {
            Document doc = Jsoup.connect("https://www.sql.ru/forum/job-offers/" + i)
                    .userAgent(userAgent)
                    .get();
            Elements rowPost = doc.select(".postslisttopic");
            for (Element td : rowPost) {
                Element href = td.child(0);
                String postLink = href.attr("href");
                System.out.println(postLink);
                System.out.println(href.text());
                Document post = Jsoup.connect(postLink)
                        .userAgent(userAgent)
                        .get();
                System.out.println(parser.getMsg(post));
                System.out.println(parser.getMsgDate(post));
            }
        }
    }

    String getMsg(Document doc) throws IllegalArgumentException {
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
}