package Weather;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class Weather {

    public static String getWether(String message) throws IOException {
        URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=" +message+"&units=metric&appid=bcd2a82d42c9daeebb4943d7d593892f");

        String city = null;
        Double temp = null;
        String smain = null;

        Scanner in = new Scanner((InputStream) url.getContent());
        String result = "";
        while (in.hasNext()){
            result += in.nextLine();
        }

        JSONObject object = new JSONObject(result);
        city = object.getString("name");

        JSONObject main = object.getJSONObject("main");

        temp = main.getDouble("temp");
        JSONArray getArray = object.getJSONArray("weather");
        for (int i = 0; i < getArray.length(); i++){
            JSONObject obj =  getArray.getJSONObject(i);
            smain = (String)obj.get("main");
        }

        return  "Город: " + Translator.translate("en", "ru",city) + "\n" +
                "Температура: " + temp +"C \n" +
                "Погода: " + Translator.translate("en", "ru",smain);
    }
}
