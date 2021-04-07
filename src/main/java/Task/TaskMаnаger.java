package Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TaskMаnаger {


    private static final String DATE_FORMAT_IN_MESSAGES = "dd.MM.yyyy HH:mm";
    private static final String DATE_FORMAT_IN_DATABASE = "yyyy.MM.dd.HH.mm";
    private static final String REGEX_OF_TASK = "^(([0]?[1-9])|([1-2]\\d)|(3[0-1]))\\.(([0]?[1-9])|([1-2][0-2])) " +
            "(([0-1]?\\d)|(2[0-3])):([0-5]?\\d) (.{0,200})$";

    public static String convertDateFromDatabaseToMessage(String dateFromDatabase){
        SimpleDateFormat messageFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        SimpleDateFormat databaseFormat = new SimpleDateFormat("yyyy.MM.dd.HH.mm");
        return "";
    }

    public static String convertDateToMessage(Date date){  // преобразует дату для отправки сообщения
        SimpleDateFormat messageFormat = new SimpleDateFormat(DATE_FORMAT_IN_MESSAGES);
        return messageFormat.format(date);
    }

    public static String convertDateToDatabase(Date date){  // преобразуте дату для записи в базу данных
        SimpleDateFormat databaseFormat = new SimpleDateFormat(DATE_FORMAT_IN_DATABASE);
        return databaseFormat.format(date);
    }

    public static Date convertFromMessageToDate(String date){ //преобразует строку с датой из сообщения в дату
        SimpleDateFormat messageFormat = new SimpleDateFormat(DATE_FORMAT_IN_MESSAGES);
        Date answer = null;
        try {
            answer = messageFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return answer;
    }

    public static Date convertFromDatabaseToDate(String date){    //преобразует строку с датой из базы данных в дату
        SimpleDateFormat databaseFormat = new SimpleDateFormat(DATE_FORMAT_IN_DATABASE);
        Date answer = null;
        try {
            answer = databaseFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return answer;
    }

    public static String[] checkTask(String task){
        Pattern pattern = Pattern.compile(REGEX_OF_TASK);
        if (pattern.matcher(task).find()){
            String[] arr = task.split(" ", 3);
            String year = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
            arr[0] = arr[0] +"." +year;
            if(validate(arr[0])) {
                return new String[]{arr[0] +" "+ arr[1], arr[2]};
            }
        }
        return null;
    }

    public static boolean validate(final String date){
        String reg = "(0?[1-9]|[12][0-9]|3[01])[.](0?[1-9]|1[012])[.]((19|20)\\d\\d)";

        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(date);

        if(matcher.matches()){
            matcher.reset();
            if(matcher.find()){
                String day = matcher.group(1);
                String month = matcher.group(2);
                int year = Integer.parseInt(matcher.group(3));
                if (day.equals("31") &&
                        (month.equals("4") || month .equals("6") || month.equals("9") ||
                                month.equals("11") || month.equals("04") || month .equals("06") ||
                                month.equals("09"))) {
                    return false; // только 1,3,5,7,8,10,12 имеют 31 день
                } else if (month.equals("2") || month.equals("02")) {
                    //leap year
                    if(year % 4==0){
                        if(day.equals("30") || day.equals("31")){
                            return false;
                        } else{
                            return true;
                        }
                    } else {
                        if(day.equals("29")||day.equals("30")||day.equals("31")){
                            return false;
                        } else { return true; }
                    }
                } else{ return true; }
            } else { return false; }
        } else{ return false; }
    }

    public static List<List<String>> converListOfTask(List<List<String>>  tasks){


        List<List<String>> convertedTasks = new ArrayList<>();

        System.out.println(tasks.toString());

        Collections.sort(tasks, (a, b) -> a.get(2).compareTo(b.get(2)));

        for (int i = 0; i < tasks.size(); i++) {
            String date = null;
            List<String> task = tasks.get(i);

            date = TaskMаnаger.convertDateToMessage(TaskMаnаger.convertFromDatabaseToDate(task.get(2)));

            List<String> convertedTask = new ArrayList<>();
            convertedTask.add(Integer.toString(i + 1) + ". " + date + " " + task.get(1));
            convertedTask.add(task.get(3));

            convertedTasks.add(convertedTask);
        }

        return convertedTasks;
    }






}
