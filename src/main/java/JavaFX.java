import javafx.application.Application;

import javafx.scene.Group;
import javafx.scene.Scene;

import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import weather.Period;
import weather.WeatherAPI;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/* 								-= References =-

 - Images: https://www.tutorialspoint.com/javafx/javafx_images.htm
 - Color: https://docs.oracle.com/javase/8/javafx/api/javafx/scene/paint/Color.html
 - Linear Gradient: https://www.tutorialspoint.com/javafx/javafx_linear_gradient_pattern.htm
 - Enums: https://www.w3schools.com/java/java_enums.asp
 - Group: https://docs.oracle.com/javase/8/javafx/api/javafx/scene/Group.html
 - Text: https://www.tutorialspoint.com/javafx/javafx_text.htm
 - Font List: https://motleybytes.com/w/JavaFxFonts
 - NWS Forecast shortForecasts: https://github.com/ktrue/NWS-forecast/blob/ccdfc4b0acf2598a1d9c5d500267be6362b6e0d5/advforecast2.php#L1747

 */

public class JavaFX extends Application {

	private enum SkyStatus {
		CLEAR,
		SUNNY,
		CLOUDY,
		RAIN,
		STORM,
		SNOW
	}

	HBox TopBox;
		TextField Date;
		//Sound Icon
		//Sound Toggle
	Rectangle sky; //Need?
	private static final String SunnySkyTop = "#6faede";
	private static final String SunnySkyBottom = "#205ae3";
	private static final String CloudySkyTop = "#6faede";
	private static final String CloudySkyBottom = "#365399";
	private static final String RainSkyTop = "#3d454a";
	private static final String RainSkyBottom = "#252d42";
	private static final String StormSkyTop = "#55626b";
	private static final String StormSkyBottom = "#191b21";
	private static final String SnowSkyTop = "#939eb8";
	private static final String SnowSkyBottom = "#626b80";

	private static final double SCENEWIDTH = 600;
	private static final double SCENEHEIGHT = 900;


		//Clouds left
		//Sun
		//Clouds right
	HBox Bottom;
	//Weather
	//Weather Description
	//Switch Time
		//
	String date, description, temperature, windSpeed, windDirection;
	Text shortDesc, tempHigh, tempLow, windSpd, windDir;

	ImageView sun, sunHalo;
	ImageView cloud1, cloud2, cloud3, cloud4, cloud5, cloud6, cloud7;

	Rectangle statsContainer, tempContainer, windContainer, rainContainer;

	SkyStatus skyStatus;
	int day = 0;

	Group todayObjects = new Group();

	public static void main(String[] args) { launch(args); }

	@Override
	public void start(Stage primaryStage) throws Exception, FileNotFoundException {
		primaryStage.getIcons().add(new Image(new FileInputStream("src\\main\\resources\\icon.png")));
		primaryStage.setTitle("Jarrette's Weather App!");
		primaryStage.setResizable(false);

		ArrayList<Period> forecast = WeatherAPI.getForecast("LOT",77,70);
		if (forecast == null){
			throw new RuntimeException("Forecast did not load");
		}

		date = forecast.get(day).startTime.toString();
		description = forecast.get(day).shortForecast;
		temperature = String.valueOf(forecast.get(day).temperature);
		windSpeed = forecast.get(day).windSpeed;
		windDirection = forecast.get(day).windDirection;

		Date = new TextField(date);
		sun = makeView(new Image(new FileInputStream("src\\main\\resources\\Sun3.png")),
									225, 100, 150, 150, 1);

		TopBox = new HBox(Date);



//		temperature = new TextField();
//		weather = new TextField();
//		int periodNum = 3;
//		temperature.setText("Today's weather is: " + String.valueOf(forecast.get(periodNum).temperature) + ". start of period: " + forecast.get(periodNum).startTime + " " + forecast.get(periodNum).endTime);
//		weather.setText(forecast.get(0).shortForecast);

		makeSky(parseDescription(description));
		makeDescription();
		makeStats();

		Scene today = new Scene(todayObjects, SCENEWIDTH, SCENEHEIGHT);
		primaryStage.setScene(today);
		primaryStage.show();
	}

	private ImageView makeView(Image newImage, double X, double Y, double height, double width, double opacity) {
		ImageView newView = new ImageView(newImage);

		newView.setX(X);
		newView.setY(Y);
		newView.setFitHeight(height);
		newView.setFitWidth(width);
		newView.setPreserveRatio(true);
		newView.setOpacity(opacity);

		return newView;
	}

	private SkyStatus parseDescription(String desc) {

		String description = desc.toLowerCase();
		if(description.contains("cloudy") || description.contains("fog")) {
			return SkyStatus.CLOUDY;
		}
		else if(description.contains("clear")) {
			return SkyStatus.CLEAR;
		}
		else if(description.contains("rain") || description.contains("showers") || description.contains("cold")) {
			return SkyStatus.RAIN;
		}
		else if(description.contains("snow") || description.contains("blizzard") || description.contains("sleet")) {
			return SkyStatus.SNOW;
		}
		else if(description.contains("storm") || description.contains("thunder")) {
			return SkyStatus.STORM;
		}
		else /* sunny, fair, */{
			return SkyStatus.SUNNY;
		}

	}

	private Rectangle colorRectangle(String topColor, String bottomColor) {
		Rectangle newSky = new Rectangle(SCENEWIDTH, SCENEHEIGHT);
		Stop[] stops = new Stop[] { new Stop(0, Color.web(topColor)), new Stop(1, Color.web(bottomColor))};
		LinearGradient lg1 = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
		newSky.setFill(lg1);

		return newSky;
	}

	private void makeSky(SkyStatus status) throws FileNotFoundException {

		SkyStatus testStatus = SkyStatus.CLOUDY;
		// TODO: COMMENT OUT TEST STATUS
//		switch(status) {
		switch(testStatus) {
			case CLEAR:
				sky = colorRectangle(SunnySkyTop, SunnySkyBottom);
				sunHalo = makeView(new Image(new FileInputStream("src\\main\\resources\\SunHalo.png")),
						SCENEWIDTH / 2 - 100, 80, 200, 200, 1);
				todayObjects.getChildren().add(sky);
				todayObjects.getChildren().add(sunHalo);
				todayObjects.getChildren().add(sun);
				break;
			case SUNNY:

				sky = colorRectangle(SunnySkyTop, SunnySkyBottom);
				cloud1 = makeView(new Image(new FileInputStream("src\\main\\resources\\cloud_white_1.png")),
						-50, 150, 300, 300, 0.35);
				cloud4 = makeView(new Image(new FileInputStream("src\\main\\resources\\cloud_white_4.png")),
						370, 150, 350, 350, 0.35);

				sunHalo = makeView(new Image(new FileInputStream("src\\main\\resources\\SunHalo.png")),
						SCENEWIDTH / 2 - 100, 80, 200, 200, 1);
				todayObjects.getChildren().add(sky);
				todayObjects.getChildren().add(sunHalo);
				todayObjects.getChildren().add(sun);
				todayObjects.getChildren().add(cloud1);
				todayObjects.getChildren().add(cloud4);

				break;
			case CLOUDY:

				sky = colorRectangle(CloudySkyTop, CloudySkyBottom);
				sunHalo = makeView(new Image(new FileInputStream("src\\main\\resources\\SunHalo.png")),
						SCENEWIDTH / 2 - 100, 80, 200, 200, 1);
				cloud1 = makeView(new Image(new FileInputStream("src\\main\\resources\\cloud_white_1.png")),
						-50, 150, 300, 300, 0.95);
				cloud2 = makeView(new Image(new FileInputStream("src\\main\\resources\\cloud_white_2.png")),
						180, 140, 250, 200, 0.85);
				cloud3 = makeView(new Image(new FileInputStream("src\\main\\resources\\cloud_white_3.png")),
						170, 200, 150, 150, 0.85);
				cloud4 = makeView(new Image(new FileInputStream("src\\main\\resources\\cloud_white_4.png")),
						370, 150, 350, 350, 0.95);
				cloud5 = makeView(new Image(new FileInputStream("src\\main\\resources\\cloud_white_5.png")),
						200, 140, 275, 275, 0.85);

				todayObjects.getChildren().add(sky);
				todayObjects.getChildren().add(sunHalo);
				todayObjects.getChildren().add(sun);
				todayObjects.getChildren().add(cloud2);
				todayObjects.getChildren().add(cloud3);
				todayObjects.getChildren().add(cloud4);
				todayObjects.getChildren().add(cloud5);
				todayObjects.getChildren().add(cloud1);

				break;
			case SNOW:
				sky = colorRectangle(SnowSkyTop, SnowSkyBottom);
				cloud1 = makeView(new Image(new FileInputStream("src\\main\\resources\\cloud_white_1.png")),
						-50, 150, 300, 300, 0.85);
				cloud2 = makeView(new Image(new FileInputStream("src\\main\\resources\\cloud_white_2.png")),
						180, 140, 250, 200, 0.85);
				cloud3 = makeView(new Image(new FileInputStream("src\\main\\resources\\cloud_white_3.png")),
						170, 200, 150, 150, 0.65);
				cloud4 = makeView(new Image(new FileInputStream("src\\main\\resources\\cloud_white_4.png")),
						370, 150, 350, 350, 0.85);
				cloud5 = makeView(new Image(new FileInputStream("src\\main\\resources\\cloud_white_5.png")),
						200, 140, 275, 275, 0.85);
				cloud6 = makeView(new Image(new FileInputStream("src\\main\\resources\\cloud_white_4.png")),
						30, 80, 350, 350, 0.75);
				cloud7 = makeView(new Image(new FileInputStream("src\\main\\resources\\cloud_white_1.png")),
						320, 120, 250, 250, 0.65);

				todayObjects.getChildren().add(sky);
				todayObjects.getChildren().add(cloud6);
				todayObjects.getChildren().add(cloud7);
				todayObjects.getChildren().add(cloud1);
				todayObjects.getChildren().add(cloud2);
				todayObjects.getChildren().add(cloud3);
				todayObjects.getChildren().add(cloud4);
				todayObjects.getChildren().add(cloud5);
				break;
			case RAIN:

				sky = colorRectangle(RainSkyTop, RainSkyBottom);
				cloud1 = makeView(new Image(new FileInputStream("src\\main\\resources\\cloud_gray_1.png")),
						-50, 150, 300, 300, 0.95);
				cloud2 = makeView(new Image(new FileInputStream("src\\main\\resources\\cloud_gray_2.png")),
						180, 140, 250, 200, 0.95);
				cloud3 = makeView(new Image(new FileInputStream("src\\main\\resources\\cloud_gray_3.png")),
						170, 200, 150, 150, 0.75);
				cloud4 = makeView(new Image(new FileInputStream("src\\main\\resources\\cloud_gray_4.png")),
						370, 150, 350, 350, 0.95);
				cloud5 = makeView(new Image(new FileInputStream("src\\main\\resources\\cloud_gray_5.png")),
						200, 140, 275, 275, 0.95);
				cloud6 = makeView(new Image(new FileInputStream("src\\main\\resources\\cloud_gray_4.png")),
						30, 80, 350, 350, 0.85);
				cloud7 = makeView(new Image(new FileInputStream("src\\main\\resources\\cloud_gray_1.png")),
						320, 120, 250, 250, 0.75);

				todayObjects.getChildren().add(sky);
				todayObjects.getChildren().add(cloud6);
				todayObjects.getChildren().add(cloud7);
				todayObjects.getChildren().add(cloud1);
				todayObjects.getChildren().add(cloud2);
				todayObjects.getChildren().add(cloud3);
				todayObjects.getChildren().add(cloud4);
				todayObjects.getChildren().add(cloud5);
				break;
			case STORM:

				sky = colorRectangle(StormSkyTop, StormSkyBottom);
				cloud1 = makeView(new Image(new FileInputStream("src\\main\\resources\\cloud_black_1.png")),
						-50, 150, 300, 300, 0.95);
				cloud2 = makeView(new Image(new FileInputStream("src\\main\\resources\\cloud_black_2.png")),
						180, 140, 250, 200, 0.95);
				cloud3 = makeView(new Image(new FileInputStream("src\\main\\resources\\cloud_black_3.png")),
						170, 200, 150, 150, 0.75);
				cloud4 = makeView(new Image(new FileInputStream("src\\main\\resources\\cloud_black_4.png")),
						370, 150, 350, 350, 0.95);
				cloud5 = makeView(new Image(new FileInputStream("src\\main\\resources\\cloud_black_5.png")),
						200, 140, 275, 275, 0.95);
				cloud6 = makeView(new Image(new FileInputStream("src\\main\\resources\\cloud_black_4.png")),
						30, 80, 350, 350, 0.85);
				cloud7 = makeView(new Image(new FileInputStream("src\\main\\resources\\cloud_black_1.png")),
						320, 120, 250, 250, 0.75);

				todayObjects.getChildren().add(sky);
				todayObjects.getChildren().add(cloud6);
				todayObjects.getChildren().add(cloud7);
				todayObjects.getChildren().add(cloud1);
				todayObjects.getChildren().add(cloud2);
				todayObjects.getChildren().add(cloud3);
				todayObjects.getChildren().add(cloud4);
				todayObjects.getChildren().add(cloud5);
				break;
			default:
		}
	}

	private void makeDescription() {
		shortDesc = new Text(description);
		shortDesc.setX(20);
		shortDesc.setY(400);
		shortDesc.setFont(Font.font("Tahoma", FontWeight.BOLD, FontPosture.REGULAR, 40));
		shortDesc.setFill(Color.WHITE);
		shortDesc.setStrokeWidth(1);
		shortDesc.setStroke(Color.DARKGRAY);

		todayObjects.getChildren().add(shortDesc);
	}

	private void makeStats() {
		statsContainer = new Rectangle(SCENEWIDTH - 100, SCENEHEIGHT / 2 + 27.5, Color.WHITE);
		statsContainer.setX(50);
		statsContainer.setY(SCENEHEIGHT / 2 - 45);
		statsContainer.setArcHeight(25);
		statsContainer.setArcWidth(25);
		statsContainer.setOpacity(0.3);
		todayObjects.getChildren().add(statsContainer);

		tempHigh = new Text(temperature);
		tempHigh.setX(70);
		tempHigh.setY(515);

		windSpd = new Text(windSpeed);
		windSpd.setX(300);
		windSpd.setY(515);

		windDir = new Text(windDirection);
		windDir.setX(300);
		windDir.setY(530);

		todayObjects.getChildren().add(tempHigh);
		todayObjects.getChildren().add(windSpd);
		todayObjects.getChildren().add(windDir);
	}
}
