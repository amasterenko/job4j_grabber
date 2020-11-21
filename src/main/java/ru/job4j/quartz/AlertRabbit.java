package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.sql.Timestamp;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {
    public static void main(String[] args) {
        try (InputStream in = AlertRabbit.class
                .getClassLoader().getResourceAsStream("app.properties")) {
            Properties config = new Properties();
            if (in == null) {
                throw new Exception();
            }
            config.load(in);
            int interval = Integer.parseInt(config.getProperty("rabbit.interval"));
            Class.forName(config.getProperty("jdbc.driver"));
            try (Connection cn = DriverManager.getConnection(
                    config.getProperty("jdbc.url"),
                    config.getProperty("jdbc.username"),
                    config.getProperty("jdbc.password"))) {
                Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
                scheduler.start();
                JobDataMap data = new JobDataMap();
                data.put("connection", cn);
                JobDetail job = newJob(Rabbit.class)
                        .usingJobData(data)
                        .build();
                SimpleScheduleBuilder times = simpleSchedule()
                        .withIntervalInSeconds(interval)
                        .repeatForever();
                Trigger trigger = newTrigger()
                        .startNow()
                        .withSchedule(times)
                        .build();
                scheduler.scheduleJob(job, trigger);
                Thread.sleep(10000);
                scheduler.shutdown();
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.out.println("Rabbit runs here ...");
            Connection cn = (Connection) context.getJobDetail().getJobDataMap().get("connection");
            try {
                PreparedStatement ps = cn.prepareStatement(
                        "INSERT INTO rabbit (created) VALUES (?) "
                );
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                ps.setTimestamp(1, timestamp);
                ps.executeQuery();
            } catch (SQLException e) {
                throw new JobExecutionException(e);
            }
        }
    }
}