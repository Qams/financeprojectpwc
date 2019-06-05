package pl.edu.agh;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import pl.edu.agh.jobs.ExampleJob;
import pl.edu.agh.jobs.ExchangeRateJob;
import pl.edu.agh.jobs.TransactionsJob;
import pl.edu.agh.jobs.TransactionsStatsJob;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

public class Main {
    public static void main(String[] args) throws SchedulerException {

        SchedulerFactory sf = new StdSchedulerFactory();
        Scheduler sche = sf.getScheduler();

        JobDetail job = newJob(ExampleJob.class).withIdentity("Id1", "Example").build();
        JobDetail job2 = newJob(ExchangeRateJob.class).withIdentity("Id2", "ExchangeRate").build();
        JobDetail job3 = newJob(TransactionsJob.class).withIdentity("Id3", "Transactions").build();
        JobDetail job4 = newJob(TransactionsStatsJob.class).withIdentity("Id4", "TransactionsStats").build();

        Trigger trigger = newTrigger()
                .withIdentity("mytrigger", "group1")
                .startNow()
                .withSchedule(cronSchedule("0 */1 * * * ?")
                        .withMisfireHandlingInstructionFireAndProceed())
                        .build();

        Trigger exchangeRateTrigger = newTrigger()
                .withIdentity("exchangeRateTrigger", "group2")
                .startNow()
                .withSchedule(cronSchedule("0 */1 * * * ?")
                        .withMisfireHandlingInstructionFireAndProceed())
                .build();

        Trigger transactionsTrigger = newTrigger()
                .withIdentity("transactionsTrigger", "group3")
                .startNow()
                .withSchedule(cronSchedule("0 */1 * * * ?")
                        .withMisfireHandlingInstructionFireAndProceed())
                .build();

        Trigger transactionsStatsTrigger = newTrigger()
                .withIdentity("transactionsStatsTrigger", "group4")
                .startNow()
                .withSchedule(cronSchedule("0 */1 * * * ?")
                        .withMisfireHandlingInstructionFireAndProceed())
                .build();

//        sche.scheduleJob(job, trigger);
//        sche.scheduleJob(job2, exchangeRateTrigger);
//        sche.scheduleJob(job3, transactionsTrigger);
        sche.scheduleJob(job4, transactionsStatsTrigger);
        sche.start();

    }
}
