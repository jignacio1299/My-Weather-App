import javafx.application.Application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.*;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import javafx.stage.Stage;
import weather.Period;
import weather.WeatherAPI;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

/* 								-= References =-

 - Images: https://www.tutorialspoint.com/javafx/javafx_images.htm
 - Color: https://docs.oracle.com/javase/8/javafx/api/javafx/scene/paint/Color.html
 - Linear Gradient: https://www.tutorialspoint.com/javafx/javafx_linear_gradient_pattern.htm
 - Enums: https://www.w3schools.com/java/java_enums.asp
 - Group: https://docs.oracle.com/javase/8/javafx/api/javafx/scene/Group.html
 - Text: https://www.tutorialspoint.com/javafx/javafx_text.htm
 - Font List: https://motleybytes.com/w/JavaFxFonts
 - NWS Short Forecast Lookup Table: https://github.com/ktrue/NWS-forecast/blob/ccdfc4b0acf2598a1d9c5d500267be6362b6e0d5/advforecast2.php#L1747
 - Rectangle: https://www.tutorialspoint.com/javafx/javafx_drawing_rectangle.htm

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

	Rectangle sky;
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

	String date, description, windSpeed, windDirection;
	int temperature;
	Text shortDesc, tempHigh, windSpd, windDir;

	ImageView sun, sunHalo;
	ImageView cloud1, cloud2, cloud3, cloud4, cloud5, cloud6, cloud7;

	Rectangle statsContainer, tempContainer, windContainer, rainContainer;

	SkyStatus skyStatus;
	int day = 0;

	HBox descriptionBox = new HBox();

	VBox temperatureBox = new VBox();
	ImageView thermometer;

	VBox windBox = new VBox();
	VBox windDirBox = new VBox();
	ImageView compass;
	private static final double compassRadius = 75;

	Line needle;
	private static final double needleLength = compassRadius * 0.7;

	VBox rainBox = new VBox();

	Group todayObjects = new Group();

	public static void main(String[] args) { launch(args); }

	@Override
	public void start(Stage primaryStage) throws Exception, FileNotFoundException {
		primaryStage.getIcons().add(new Image(new FileInputStream("src\\main\\resources\\icon.png")));
		primaryStage.setTitle("Chicago Weather");
		primaryStage.setResizable(false);

		ArrayList<Period> forecast = WeatherAPI.getForecast("LOT",77,70);
		if (forecast == null){
			throw new RuntimeException("Forecast did not load");
		}

		date = forecast.get(day).startTime.toString();
		description = forecast.get(day).shortForecast;
		temperature = forecast.get(day).temperature;
		windSpeed = forecast.get(day).windSpeed;
		windDirection = forecast.get(day).windDirection;

		sun = makeView(new Image(new FileInputStream("src\\main\\resources\\Sun3.png")),
									225, 100, 150, 150, 1);



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

//		SkyStatus testStatus = SkyStatus.SNOW;
		// TODO: COMMENT OUT TEST STATUS
		switch(status) {
//		switch(testStatus) {
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
		shortDesc.setFont(Font.font("Tahoma", FontWeight.BOLD, FontPosture.REGULAR, 40));
		shortDesc.setFill(Color.WHITE);
		shortDesc.setStrokeWidth(1.5);
		shortDesc.setStroke(Color.DARKGRAY);
		shortDesc.setTextAlignment(TextAlignment.CENTER);

		descriptionBox.setLayoutX(0);
		descriptionBox.setLayoutY(325);
		descriptionBox.setPrefWidth(600);

		descriptionBox.setSpacing(0);
		descriptionBox.setAlignment(Pos.CENTER);

		descriptionBox.getChildren().add(shortDesc);
		todayObjects.getChildren().add(descriptionBox);

//		todayObjects.getChildren().add(shortDesc);

	}

	private void makeStats() throws FileNotFoundException {
		statsContainer = new Rectangle(500, 477.5, Color.WHITE);
		statsContainer.setX(50);
		statsContainer.setY(405);
		statsContainer.setArcHeight(25);
		statsContainer.setArcWidth(25);
		statsContainer.setOpacity(0.3);

		makeTemp();

		makeWind();

		rainContainer = new Rectangle(220, (437.5 - 30) / 2 + 5, Color.WHITE);
		rainContainer.setX(310);
		rainContainer.setY(425 + (437.5 - 30) / 2 + 5 + 20);
		rainContainer.setArcWidth(25);
		rainContainer.setArcHeight(25);
		rainContainer.setOpacity(0.3);


		todayObjects.getChildren().add(statsContainer);

		todayObjects.getChildren().add(tempContainer);
		todayObjects.getChildren().add(temperatureBox);


		todayObjects.getChildren().add(rainContainer);
//		todayObjects.getChildren().add(rainBox);
	}

	private void makeTemp() throws FileNotFoundException {

		tempContainer = new Rectangle(220, 437.5, Color.WHITE);
		tempContainer.setX(70);
		tempContainer.setY(425);
		tempContainer.setArcHeight(25);
		tempContainer.setArcWidth(25);
		tempContainer.setOpacity(0.3);

		tempHigh = new Text(String.valueOf(temperature) + "°F");
		tempHigh.setFont(Font.font("Tahoma", FontWeight.NORMAL, FontPosture.REGULAR, 40));

		temperatureBox.setLayoutX(70);
		temperatureBox.setLayoutY(425);
		temperatureBox.setPrefSize(220, 437.5);
		temperatureBox.setAlignment(Pos.TOP_CENTER);
		temperatureBox.setPadding(new Insets(20));

		temperatureBox.getChildren().add(tempHigh);

		//TODO: GET RID OF OVERRIDE

		if(temperature > 80) {
			//Hot
			thermometer = makeView(new Image(new FileInputStream("src\\main\\resources\\thermometer_hot.png")), 0, 0, 375, 180, 1);
			tempHigh.setStroke(Color.web("#872F18"));
			tempHigh.setStrokeWidth(2);
			tempHigh.setFill(Color.web("#B83F1F"));
		}
		else if(temperature > 60) {
			//Warm
			thermometer = makeView(new Image(new FileInputStream("src\\main\\resources\\thermometer_warm.png")), 0, 0, 375, 180, 1);
			tempHigh.setStroke(Color.web("#A18213"));
			tempHigh.setStrokeWidth(2);
			tempHigh.setFill(Color.web("#EBBD1A"));
		}
		else if(temperature > 32) {
			//Cool
			thermometer = makeView(new Image(new FileInputStream("src\\main\\resources\\thermometer_cool.png")), 0, 0, 375, 180, 1);
			tempHigh.setStroke(Color.web("#1389A1"));
			tempHigh.setStrokeWidth(2);
			tempHigh.setFill(Color.web("#1CC6E8"));
		}
		else {
			//Freezing
			thermometer = makeView(new Image(new FileInputStream("src\\main\\resources\\thermometer_freezing.png")), 0, 0, 375, 180, 1);
			tempHigh.setStroke(Color.web("#1C4CE8"));
			tempHigh.setStrokeWidth(2);
			tempHigh.setFill(Color.web("#0F2C87"));
		}
		temperatureBox.getChildren().add(thermometer);
	}

	private void makeWind() throws FileNotFoundException {
		windContainer = new Rectangle(220, (437.5 - 30) / 2 + 5, Color.WHITE);
		windContainer.setX(310);
		windContainer.setY(425);
		windContainer.setArcWidth(25);
		windContainer.setArcHeight(25);
		windContainer.setOpacity(0.3);

		windSpd = new Text("Wind: " + windSpeed);
		windSpd.setStrokeWidth(1.5);
		windSpd.setFont(Font.font("Tahoma", FontWeight.NORMAL, FontPosture.REGULAR, 20));
		windSpd.setFill(Color.WHITE);

		windBox.setLayoutX(310);
		windBox.setLayoutY(425);
		windBox.setPrefSize(220, (437.5 - 30) / 2 + 5);
		windBox.setAlignment(Pos.TOP_CENTER);
		windBox.setPadding(new Insets(20));

//		compass stuff
		compass = makeView(new Image(new FileInputStream("src\\main\\resources\\compass.png")),
				420 - compassRadius, 425 + 50, compassRadius * 2, compassRadius * 2, 1);

		drawNeedle();

		windBox.getChildren().add(windSpd);

		todayObjects.getChildren().add(windContainer);
		todayObjects.getChildren().add(windBox);
		todayObjects.getChildren().add(compass);
		todayObjects.getChildren().add(needle);
		todayObjects.getChildren().add(windDirBox);

	}

	private void drawNeedle() {

		final double compassCenterX = compass.getX() + compassRadius;
		final double compassCenterY = compass.getY() + compassRadius;

		needle = new Line();
		needle.setFill(Color.RED);
		needle.setStroke(Color.RED);
		needle.setStrokeWidth(2);

		needle.setStartX(compassCenterX);
		needle.setStartY(compassCenterY);

		double angleFactor;


		switch (windDirection) {
			case "E":
				angleFactor = 0;
				break;
			case "ENE":
				angleFactor = 1;
				break;
			case "NE":
				angleFactor = 2;
				break;
			case "NNE":
				angleFactor = 3;
				break;
			case "N":
				angleFactor = 4;
				break;
			case "NNW":
				angleFactor = 5;
				break;
			case "NW":
				angleFactor = 6;
				break;
			case "WNW":
				angleFactor = 7;
				break;
			case "W":
				angleFactor = 8;
				break;
			case "WSW":
				angleFactor = 9;
				break;
			case "SW":
				angleFactor = 10;
				break;
			case "SSW":
				angleFactor = 11;
				break;
			case "S":
				angleFactor = 12;
				break;
			case "SSE":
				angleFactor = 13;
				break;
			case "SE":
				angleFactor = 14;
				break;
			case "ESE":
				angleFactor = 15;
				break;
			default:
				angleFactor = 0;
		}

		needle.setEndX(compassCenterX + (needleLength * cos(angleFactor * Math.PI / 8)));
		needle.setEndY(compassCenterY - (needleLength * sin(angleFactor * Math.PI / 8)));

		windDir = new Text(windDirection);
		windDir.setStrokeWidth(1.5);
		windDir.setFont(Font.font("Tahoma", FontWeight.NORMAL, FontPosture.REGULAR, 20));
		windDir.setFill(Color.BLUE);

		windDirBox.setLayoutX(compassCenterX - compassRadius);
		windDirBox.setPrefWidth(compassRadius * 2);
		windDirBox.setAlignment(Pos.TOP_CENTER);
		windDirBox.getChildren().add(windDir);

		if(angleFactor > 8) {
			windDirBox.setLayoutY(compassCenterY - 25);
		}
		else {
			windDirBox.setLayoutY(compassCenterY);
		}
	}
}
