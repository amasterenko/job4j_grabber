package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SqlRuParse {
    public static void main(String[] args) throws Exception {
        Document doc = Jsoup.connect("https://www.sql.ru/forum/job-offers")
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
                        + "AppleWebKit/537.36 (KHTML, like Gecko) "
                        + "Chrome/86.0.4240.75 Safari/537.36")
                .get();
        Elements rowPost = doc.select(".postslisttopic");
        Elements rowDate = doc.select(".altCol");
        int indx = 0;
        for (Element td : rowPost) {
            Element href = td.child(0);
            System.out.println(href.attr("href"));
            System.out.println(href.text());
            href = rowDate.get(1 + 2 * indx++);
            System.out.println(href.text());
        }
    }
}