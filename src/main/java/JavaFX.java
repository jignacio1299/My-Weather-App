import javafx.application.Application;

import javafx.scene.Group;
import javafx.scene.Scene;

import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import weather.Period;
import weather.WeatherAPI;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class JavaFX extends Application {
	HBox TopBox;
		TextField Date;
		//Sound Icon
		//Sound Toggle
	Rectangle Sky; //Need?
		//Clouds left
		//Sun
		//Clouds right
	HBox Bottom;
	//Weather
	//Weather Description
	//Switch Time
		//
	String date, description, temperature, windSpd, windDir;

	int day = 0;

	public static void main(String[] args) { launch(args); }

	//feel free to remove the starter code from this method
	@Override
	public void start(Stage primaryStage) throws Exception, FileNotFoundException {
		primaryStage.setTitle("I'm a professional Weather App!");

		ArrayList<Period> forecast = WeatherAPI.getForecast("LOT",77,70);
		if (forecast == null){
			throw new RuntimeException("Forecast did not load");
		}

		date = forecast.get(day).startTime.toString();
		description = forecast.get(day).shortForecast;
		temperature = String.valueOf(forecast.get(day).temperature);
		windSpd = forecast.get(day).windSpeed;
		windDir = forecast.get(day).windDirection;






		Date = new TextField(date);

		Image Sun = new Image(new FileInputStream("src\\main\\resources\\Sun.png"));
		ImageView sunView = new ImageView(Sun);
		sunView.setX(0);
		sunView.setY(0);
		sunView.setFitHeight(150);
		sunView.setFitWidth(150);
		sunView.setPreserveRatio(true);
		TopBox = new HBox(Date);

		Sky = new Rectangle(700, 900);
		Sky.setFill(Color.AQUA);






//		temperature = new TextField();
//		weather = new TextField();
//		int periodNum = 3;
//		temperature.setText("Today's weather is: " + String.valueOf(forecast.get(periodNum).temperature) + ". start of period: " + forecast.get(periodNum).startTime + " " + forecast.get(periodNum).endTime);
//		weather.setText(forecast.get(0).shortForecast);
		
		
		

		Scene today = new Scene(new Group(sunView), 700, 900);
		primaryStage.setScene(today);
		primaryStage.show();
	}

}
