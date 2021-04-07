# Telegram Bot Judah

Бот, который подскажет погоду, напомнит о важных задачах и поделится мудрыми мыслями.
Бот написан на Java, в качестве бд используется PostgreSQL, в качестве сервера используется Heroku.


![alt text](screenshots/start_and_help.png)​

Каждый день бот присылает сообщение состоящие из текущей даты, задач, который пройдут в этот день, информацию о погоде и мудрую мысль.

![alt text](screenshots/start_and_help.png)​

В настройках мы можем изменить наши имя,  город и нужно ли присылать каждое утро сообщения.

![alt text](screenshots/setting.png)​
![alt text](screenshots/changeName.png)​

В ответ на команды /waether и /quote бот присылает погоду и цитату

![alt text](screenshots/quote_and_weather.png)​

Добавление задач происходит после вызова команды /addTask. Для просмотра всех задач необходимо вызвать команду /showTasks. 
В списке задачи сортируются по времени. Так же имеется возможно удалить задачи

![alt text](screenshots/show_and_add_tasks_1.png)​
![alt text](screenshots/show_and_add_tasks_2.png)​
