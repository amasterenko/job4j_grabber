package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {
    public static void main(String[] args) {
        try (InputStream in = AlertRabbit.class
                .getClassLoader().getResourceAsStream("app.properties")) {
            Properties config = new Properties();
            if (in == null) {
                throw new FileNotFoundException();
            }
            config.load(in);
            int interval = Integer.parseInt(config.getProperty("rabbit.interval"));
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDetail job = newJob(Rabbit.class).build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(interval)
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
        } catch (FileNotFoundException e) {
            System.out.println("The 'app.properties' file was not found!");
        } catch (IOException | NumberFormatException e) {
            System.out.println("Wrong format of the 'app.properties' file!");
        } catch (SchedulerException e) {
            System.out.println("Scheduler exception was occurred!");
        }
    }

    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.out.println("Rabbit runs here ...");
        }
    }
}