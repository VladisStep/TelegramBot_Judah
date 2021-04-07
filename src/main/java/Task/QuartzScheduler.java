package Task;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import java.util.Date;
import java.util.TimeZone;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.DateBuilder.evenMinuteDate;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;


public class QuartzScheduler {

    private static String CORN_EXPRESSION = "0 0 9 ? * * *"; // -> everyday at 9:00
    private static volatile boolean isWork = false;

    public static boolean start() {
        if (isWork == false) {
            synchronized(TasksChecker.class) {
                if (isWork == false) {
                    try {
                        SchedulerFactory sf = new StdSchedulerFactory();
                        Scheduler sched = sf.getScheduler();

                        sched.start();

                        JobDetail job = newJob(EveryDayMessage.class)
                                .withIdentity("job1", "group1")
                                .build();

                        Date runTime = evenMinuteDate(new Date());

                        Trigger trigger = newTrigger()
                                .withIdentity("trigger1", "group1")
                                .withSchedule(cronSchedule(CORN_EXPRESSION)
                                        .inTimeZone(TimeZone.getTimeZone("GMT+3")))
                                .build();

                        sched.scheduleJob(job, trigger);

                    } catch (SchedulerException e) {
                        e.printStackTrace();
                    }
                    isWork = true;
                }
            }
        }
        return isWork;
    }

}
