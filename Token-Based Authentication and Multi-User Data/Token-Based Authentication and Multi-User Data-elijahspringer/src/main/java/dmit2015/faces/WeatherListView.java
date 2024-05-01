package dmit2015.faces;

import dmit2015.restclient.Weather;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Named;
import org.omnifaces.cdi.ViewScoped;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

@Named("currentWeatherListView")
@ViewScoped
public class WeatherListView implements Serializable {

    private static final List<String> summary = Arrays.asList(
            "Freezing", "Bracing", "Chilly", "Cool", "Mild", "Warm", "Balmy", "Hot", "Sweltering", "Scorching"
    );

    private Map<String, Weather> weatherMap;

    @PostConstruct
    public void initialize() {
        weatherMap = generateWeatherMap();
    }

    public Map<String, Weather> getWeatherMap() {
        return weatherMap;
    }

    private Map<String, Weather> generateWeatherMap() {
        Map<String, Weather> weatherMap = new HashMap<>();

        for (int i = 1; i <= 10; i++) {
            Weather weather = generateRandomWeather();
            weatherMap.put(String.valueOf(i), weather);
        }

        return weatherMap;
    }

    private Weather generateRandomWeather() {
        Weather weather = new Weather();
        weather.setDate(LocalDate.now().plusDays(new Random().nextInt(10)));
        weather.setTempc(new Random().nextInt(55) - 20);
        weather.setTempf(32 + (int) (weather.getTempc() / 0.5556));
        int randomIndex = new Random().nextInt(summary.size());
        weather.setSummary(summary.get(randomIndex));

        return weather;
    }
}