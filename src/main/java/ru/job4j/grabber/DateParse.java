package ru.job4j.grabber;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

public class DateParse {
    private static final DateFormatSymbols SQL_RU_MONTHS = new DateFormatSymbols() {
        @Override
        public String[] getMonths() {
            return new String[] {"янв", "фев", "мар", "апр", "май", "июн",
                    "июл", "авг", "сен", "окт", "ноя", "дек"};
        }
    };

    private static final Logger LOG = LoggerFactory.getLogger(DateParse.class);

    public static Timestamp strToTimestamp(String inputDate)
            throws IllegalArgumentException {
        try {
            String[] dateTime = inputDate.split(",");
            if (dateTime[0].equals("сегодня")) {
                dateTime[0] = new SimpleDateFormat("dd MMMM yy", SQL_RU_MONTHS)
                        .format(Date.valueOf(LocalDate.now()));
            }
            if (dateTime[0].equals("вчера")) {
                dateTime[0] = new SimpleDateFormat("dd MMMM yy", SQL_RU_MONTHS)
                        .format(Date.valueOf(LocalDate.now().minusDays(1)));
            }
            SimpleDateFormat input = new SimpleDateFormat("dd MMMM yy H:mm", SQL_RU_MONTHS);
            return new Timestamp(input.parse(dateTime[0] + dateTime[1]).getTime());
        } catch (Exception e) {
            LOG.error("Exception", e);
            throw new IllegalArgumentException(e);
        }
    }
}
