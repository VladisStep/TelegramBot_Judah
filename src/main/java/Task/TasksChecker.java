package Task;

import Database.PostgreSQLJDBC;
import Bot.Bot;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class TasksChecker {
    private static volatile boolean isWork = false;
    public Bot bot;

    private TasksChecker(Bot bot) {
        this.bot = bot;
    }

    public static boolean start(Bot bot) {
        if (isWork == false) {
            synchronized(TasksChecker.class) {
                if (isWork == false) {
                    ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
                    ses.scheduleAtFixedRate(new Runnable() {
                        @Override
                        public void run() {

                            Calendar cal = Calendar.getInstance();
                            cal.setTime(new Date());
                            TimeZone.setDefault(TimeZone.getTimeZone("GMT+3"));
                            Date currentDate = cal.getTime();

                            List<List<String>> allTask = PostgreSQLJDBC.findDate(
                                    TaskMаnаger.convertDateToDatabase(currentDate));
                            System.out.println(currentDate);

                            for (List<String> task: allTask){
                                bot.sendNotification(task);
                            }
                        }
                    }, 0, 1, TimeUnit.SECONDS);
                isWork = true;
                }
            }
        }
        return isWork;
    }

}
