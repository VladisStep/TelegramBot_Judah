package Bot;

import Database.PostgreSQLJDBC;
import Task.QuartzScheduler;
import Task.TasksChecker;

import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.*;


public class Bot extends TelegramLongPollingBot {
    
    
    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        TasksChecker.start(new Bot());
        QuartzScheduler.start();
        try {
            telegramBotsApi.registerBot(new Bot());
        }catch (TelegramApiException e){
            e.printStackTrace();
        }
    }

    public void sendMsg(Message message, String text, int state){
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableHtml(true); // включить возможность разметки
        sendMessage.setChatId(message.getChatId().toString()); // выбираем id чата, куда отвечать

        sendMessage.setText(text);

        try {
            switch (state){
                case 0:
                    setButtons(sendMessage);
                    break;
                case 1:
                    setButtonsSetting(sendMessage);
                    break;
            }

            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendMsgWithSettingMenu(Message message, String text){
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableHtml(true); // включить возможность разметки
        sendMessage.setChatId(message.getChatId().toString()); // выбираем id чата, куда отвечать

        sendMessage.setText(text);

        try {
            setButtonsSetting(sendMessage);
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    public void sendTaskList(Message message, String text, int state){
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableHtml(true); // включить возможность разметки
        sendMessage.setChatId(message.getChatId().toString()); // выбираем id чата, куда отвечать
        sendMessage.setText(text);
        InlineKeyboardMarkup inlineKeyboardMarkup =new InlineKeyboardMarkup(); // объект клавиатуры

        if (!text.equals("Задач нет")){
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText("Удалить задачи"); // надпись на кнопке
            inlineKeyboardButton.setCallbackData("Button \"Удалить задачи\" has been pressed"); // что будет отсылать
            List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
            keyboardButtonsRow1.add(inlineKeyboardButton);
            List<List<InlineKeyboardButton>> rowList= new ArrayList<>();
            rowList.add(keyboardButtonsRow1);
            inlineKeyboardMarkup.setKeyboard(rowList);
        }

        try {
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendNotification(List<String> task){
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableHtml(true); // включить возможность разметки
        sendMessage.setChatId(task.get(0)); // выбираем id чата, куда отвечать
        sendMessage.setText(task.get(1));
        PostgreSQLJDBC.deleteTask(task.get(3));
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            System.err.println(" ex");
        }
    }

    public void setButtons(SendMessage sendMessage){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(); // инициализируем клавиатуру
        sendMessage.setReplyMarkup(replyKeyboardMarkup); // связка сообщения с клавиатурой
        replyKeyboardMarkup.setSelective(true); // выводим клавиатуру всем пользователям
        replyKeyboardMarkup.setResizeKeyboard(true); // размер кнопок
//        replyKeyboardMarkup.setOneTimeKeyboard(false); // не скрывать клавиатуру

        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();

        keyboardFirstRow.add(new KeyboardButton("/help"));
        keyboardFirstRow.add(new KeyboardButton("/setting"));
        keyboardFirstRow.add(new KeyboardButton("/quote"));

        KeyboardRow keyboardSecondRow = new KeyboardRow();
        keyboardSecondRow.add(new KeyboardButton("/weather"));
        keyboardSecondRow.add(new KeyboardButton("/addTask"));
        keyboardSecondRow.add(new KeyboardButton("/showTasks"));

        keyboardRowList.add(keyboardFirstRow);
        keyboardRowList.add(keyboardSecondRow);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
    }

    public void setButtonsSetting(SendMessage sendMessage){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(); // инициализируем клавиатуру
        sendMessage.setReplyMarkup(replyKeyboardMarkup); // связка сообщения с клавиатурой
        replyKeyboardMarkup.setSelective(true); // выводим клавиатуру всем пользователям
        replyKeyboardMarkup.setResizeKeyboard(true); // размер кнопок

        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();

        keyboardFirstRow.add(new KeyboardButton("/city"));
        keyboardFirstRow.add(new KeyboardButton("/name"));


        KeyboardRow keyboardSecondRow = new KeyboardRow();
        keyboardSecondRow.add(new KeyboardButton("/changeNotifications"));

        KeyboardRow keyboardThirdRow = new KeyboardRow();
        keyboardThirdRow.add(new KeyboardButton("/cancel"));

        keyboardRowList.add(keyboardFirstRow);
        keyboardRowList.add(keyboardSecondRow);
        keyboardRowList.add(keyboardThirdRow);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
    }


    @Override
    public void onUpdateReceived(Update update) {  // метод для обновления
        Message message = update.getMessage();
        if(message != null && message.hasText()){
            if (PostgreSQLJDBC.getState(message.getChatId().toString()) == 1) {
                String command = message.getText();
                sendMsg(message,
                        State.settingMenuState(message.getChatId().toString(), message.getText()),
                        0);
            }else if (PostgreSQLJDBC.getState(message.getChatId().toString()) == 2) {
                sendMsg(message,
                        State.addingTaskState(message.getChatId().toString(), message.getText()),
                        0);
            }else if (PostgreSQLJDBC.getState(message.getChatId().toString()) == 3) {
                sendMsg(message,
                        State.changeCityState(message.getChatId().toString(), message.getText()),
                        0);
            }else if (PostgreSQLJDBC.getState(message.getChatId().toString()) == 4) {
                sendMsg(message,
                        State.changeNameState(message.getChatId().toString(), message.getText()),
                        0);
            } else if (PostgreSQLJDBC.getState(message.getChatId().toString()) == 5) {
                sendMsg(message,
                        State.deletingTaskState(message.getChatId().toString(), message.getText()),
                        0);
            } else {
                switch (message.getText()){
                    case "/start":
                        sendMsg(message,
                                State.startState(message.getChatId().toString()),
                                0);
                    case "/help":
                        sendMsg(message,
                                State.helpState(message.getChatId().toString()),
                                0);
                        break;
                    case "/setting":
                        sendMsgWithSettingMenu(message,
                                State.settingState(message.getChatId().toString()));
                        break;
                    case "/weather":
                        sendMsg(message,
                                State.weatherState(message.getChatId().toString()),
                                0);
                        break;
                    case "/quote":
                        sendMsg(message,
                                State.quoteState(),
                                0);
                        break;
                    case "/addTask":
                        sendMsg(message,
                                State.addTaskState(message.getChatId().toString()),
                                0);
                        break;
                    case "/showTasks":
                        sendTaskList(message,
                                State.showTasksState(message.getChatId().toString()),
                                0);
                        break;
                    default:
                        sendMsg(message, "Я не понимаю",0);
                }
            }
        } else if(update.hasCallbackQuery()){
            message = update.getCallbackQuery().getMessage();
            sendMsg(message,
                    State.deleteState(message.getChatId().toString()),
                    0);
        }
    }

    @Override
    public String getBotUsername() {  // вернуть имя бота при регистрации
        return "Bot_test";
    }

    @Override
    public String getBotToken() { // получить токен бота
        return "";
    }
}
