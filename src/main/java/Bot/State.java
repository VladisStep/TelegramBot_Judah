package Bot;

import Database.PostgreSQLJDBC;
import Quote.Quote;
import Quote.QuoteBuilder;
import Task.TaskMаnаger;
import Weather.Weather;

import java.io.IOException;
import java.util.List;

public class State {

    public static String startState(String chatID){
        String text = "Привет! Меня зовут Джудо и я твой помощник!\n";
        if (PostgreSQLJDBC.find(chatID) == null){
            PostgreSQLJDBC.insert(chatID, "--", "Moscow");
        }
        return text;
    }

    public static String helpState(String chatID){
        String text = "Я могу: \n"
                + "/setting – чтобы настроить свой профиль \n"
                + "/quote – чтобы прочитать мудрость \n"
                + "/weather – чтобы узнать погоду \n"
                + "/addTask – чтбы добавить напоминание \n"
                + "/showTasks – чтобы посмотреть напоминания \n"
                + "\nКаждый день в 9:00 я буду присылать задачи на сегодня\n"
                + "Отключить эти сообщения можно в настройках (changeNotifications)";

        List<String> user = PostgreSQLJDBC.find(chatID);
        text = text +"\n\nТвое имя: " + user.get(1)
                + "\nТвой город: " + user.get(2);
        return text;
    }

    public static String settingState(String chatID){
        String text = "Что будем менять?\n" +
                        "Уведомления: "
                        + (PostgreSQLJDBC.getNotifications(chatID)?" ВКЛючены":"ВЫКЛючены");
        List<String> user = PostgreSQLJDBC.find(chatID);
        text = text +"\nТвое имя: " + user.get(1)
                + "\nТвой город: " + user.get(2);
        PostgreSQLJDBC.setState(chatID, 1);
        return text;
    }

    public static String weatherState(String chatID){
        String text = null;
        try {
            String city = PostgreSQLJDBC.find(chatID).get(2);
            text = Weather.getWether(city);
        } catch (IOException e) {
            text = "Город не найден!";
        }
        return text;
    }

    public static String quoteState(){
        Quote quote = new QuoteBuilder().getQuote();
        String text = "<i>" + quote.quoteText +"</i>" ;
        if(quote.quoteAuthor.length() != 0){
            text = text+ "\n\nАвтор: " + quote.quoteAuthor;
        }
        return text;
    }

    public static String addTaskState(String chatID){
        PostgreSQLJDBC.setState(chatID, 2);
        return "Введите напоминание\nНапример: 23.10 12:00 Сделать дело";
    }

    public static String showTasksState(String chatID){
        List<List<String>> tasks = PostgreSQLJDBC.findTasks(chatID);
        String text = "Твои задачи:\n";
        if(tasks.size() != 0){
            List<List<String>> convertedTasks = TaskMаnаger.converListOfTask(tasks);
            for (List<String> task: convertedTasks){
                text  = text + task.get(0) + '\n';
            }
        } else {
            text = "Задач нет";
        }
        return text;
    }

    public static String settingMenuState(String chatID, String command){
        String text = "";
        switch (command){
            case "/city":
                text = "Введите город";
                PostgreSQLJDBC.setState(chatID, 3);
                break;
            case "/name":
                text = "Введите имя";
                PostgreSQLJDBC.setState(chatID, 4);
                break;
            case "/changeNotifications":
                PostgreSQLJDBC.updatenotifications(chatID);
                PostgreSQLJDBC.setState(chatID, 0);
                text = "Сделано!";
                break;
            case "/cancel":
                text = "Хорошо, не будем";
            default:
                PostgreSQLJDBC.setState(chatID, 0);
                text = "Я не понимаю";
        }
        return text;
    }

    public static String changeCityState(String chatID, String city){
        String text = "Город успешно добавлен!";
        PostgreSQLJDBC.setState(chatID, 0);
        try {
            text = text + "\n" + Weather.getWether(city);
            PostgreSQLJDBC.updateCity(chatID, city);
        } catch (IOException e) {
            text = "Город не найден!";
        }
        return text;
    }

    public static String changeNameState(String chatID, String name){
        String text = "Имя изменено";
                PostgreSQLJDBC.setState(chatID, 0);
        if (!(name.length() < 200) || !PostgreSQLJDBC.updateName(chatID, name)){
        text = " Ой, что-то не так";
        }
        return text;
    }

    public static String deletingTaskState(String chatID, String index){
        String text = "Задача удалена";
        PostgreSQLJDBC.setState(chatID, 0);
        try {
            Integer taskIndex = Integer.parseInt(index);
            List<List<String>> tasks = TaskMаnаger.converListOfTask(PostgreSQLJDBC.findTasks(chatID));
            if (taskIndex > 0 && taskIndex <= tasks.size()){
                PostgreSQLJDBC.deleteTask(tasks.get(taskIndex - 1).get(1));
            }else{
                text = "Такого индеска нет. Выбери от 1 до " + (tasks.size());
            }
        }catch (NumberFormatException ex){
            text = "Это не индекс!";
        }
        return text;
    }

    public static String addingTaskState(String chatID, String task){
        String text = "Запомнил! Обязательно тебе напомню";
        PostgreSQLJDBC.setState(chatID, 0);
        String []checked_task = TaskMаnаger.checkTask(task);
        if(checked_task != null){
            String dateInPostgreSQLJDBC =
                    TaskMаnаger.convertDateToDatabase(
                            TaskMаnаger.convertFromMessageToDate(checked_task[0]));
            PostgreSQLJDBC.insertDate(dateInPostgreSQLJDBC, checked_task[1], chatID);
        } else {
            text = "Что-то тут не так... (Проверь правильность задачи)";
        }
        return text;
    }

    public static String deleteState(String chatID){
        String text = "Какую задачу удалить? (Введи номер задчи)";
        PostgreSQLJDBC.setState(chatID, 5);
        return text;
    }
}
