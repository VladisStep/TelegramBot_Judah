package Task;

import Bot.Bot;
import Bot.State;
import Database.PostgreSQLJDBC;
import Weather.Weather;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class EveryDayMessage implements Job {
    public void execute(JobExecutionContext context) throws JobExecutionException {

        List<String> users = PostgreSQLJDBC.getNotificationsChatID();
        Bot bot = new Bot();

        for(String id: users){
            SendMessage sendMessage = new SendMessage();
            sendMessage.enableHtml(true);
            sendMessage.setChatId(id); // выбираем id чата, куда отвечать

            String currentDayAndMonth = TaskMаnаger.convertDateToMessage(new Date()).substring(0,5);

            String text = "<b>Доброе утро!</b>\n\n<b>Сегодня: </b>"
                    + currentDayAndMonth + "\n\n";

            List<List<String>> tasks = PostgreSQLJDBC.findTasks(id);
            Collections.sort(tasks, (a, b) -> a.get(2).compareTo(b.get(2)));

            for(List<String> task: tasks){
                if (task.get(2).lastIndexOf(TaskMаnаger.convertDateToDatabase(new Date()).substring(0,10)) != -1){
                    text = text + task.get(2).substring(11,16).replace('.', ':') +
                            " " + task.get(1) + "\n";
                }
            }

            try {
                String city = PostgreSQLJDBC.find(id).get(2);
                text = text + "\n<b>Погода</b>\n"+
                        Weather.getWether(city) + "\n";
            } catch (IOException e) {
                e.printStackTrace();
            }

            text = text + "\n<b>Умная мысль:</b>\n\n" +
                    State.quoteState();

            sendMessage.setText(text);
            sendMessage.getReplyMarkup();
            try {
                bot.setButtons(sendMessage);
                bot.sendMessage(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

}
