import javafx.application.Application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
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
 - Line: https://docs.oracle.com/javase/8/javafx/api/javafx/scene/shape/Line.html
 - BackgroundImage: https://docs.oracle.com/javase/8/javafx/api/javafx/scene/layout/BackgroundImage.html
 - BackgroundSize: https://docs.oracle.com/javase/8/javafx/api/javafx/scene/layout/BackgroundSize.html
 - StackPane: https://www.geeksforgeeks.org/java/javafx-stackpane-class/

 								-= Resources =-
 - Sun: https://freepngimg.com/nature/sun
 - Clouds: https://gallery.yopriceville.com/Free-Clipart-Pictures/Cloud-PNG/Cartoon_Clouds_Set_Transparent_PNG_Clip_Art_Image
 - Water Drop: https://www.flaticon.com/free-icon/water-drop_5611083
 - Thermostat: https://www.pinterest.com/pin/65654107055768701/
 - Compass: https://www.vecteezy.com/vector-art/27512006-magnetic-compass-art-design-for-travel-tourism-exploration-concept-graphic-element-for-navigation-orientation-vector-illustration
 - Wind: https://favpng.com/download/UANwyahk#google_vignette
 - Moon: https://favpng.com/download/H48BGtMf

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

	ArrayList<Period> forecast;

	// Today's weather scene components

	Scene todayScene;

	String date, description, windSpeed, windDirection;
	int temperature;
	Text shortDesc, tempHigh, windSpd, windDir, rainProb;

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
    ImageView rainDrop;
    int rainChance;

	Group todayObjects = new Group();




	Button upButton, downButton;
	HBox upButtonBox, arrowUpBox;
	HBox downButtonBox, arrowDownBox;
	Background upButtonBG, downButtonBG;

	//Forecast Scene components

	Scene forecastScene;

	Group forecastObjects = new Group();

	HBox allContainers;
	VBox todayBox, tomorrowBox, dayAfterBox;

	Rectangle background;
	VBox todayContainer, tomorrowContainer, dayAfterContainer;
	ImageView sun1, sun2, sun3, moon1, moon2, moon3;
	ImageView wind1, wind2, wind3, rain1, rain2, rain3;
	Text date1, date2, date3;


	public static void main(String[] args) { launch(args); }

	@Override
	public void start(Stage primaryStage) throws Exception, FileNotFoundException {

		//				-= App Init =-

		primaryStage.getIcons().add(new Image(new FileInputStream("src\\main\\resources\\icon.png")));
		primaryStage.setTitle("Chicago Weather");
		primaryStage.setResizable(false);

		forecast = WeatherAPI.getForecast("LOT",77,70);
		if (forecast == null){
			throw new RuntimeException("Forecast did not load");
		}

		//				-= Today's Weather Scene =-

		date = forecast.get(day).startTime.toString();
		description = forecast.get(day).shortForecast;
		temperature = forecast.get(day).temperature;
		windSpeed = forecast.get(day).windSpeed;
		windDirection = forecast.get(day).windDirection;
        rainChance = forecast.get(day).probabilityOfPrecipitation.value;

		sun = makeView(new Image(new FileInputStream("src\\main\\resources\\Sun.png")),
									225, 100, 150, 150, 1);

		makeSky(parseDescription(description));
		makeDescription();
		makeStats();
		makeButton1(primaryStage);

		//				-= Three-Day Forecast Scene =-

		makeBackground();
		makeBoxes();




		//				-= App Display =-

		todayScene = new Scene(todayObjects, SCENEWIDTH, SCENEHEIGHT);
		forecastScene = new Scene(forecastObjects, SCENEWIDTH, SCENEHEIGHT);
		primaryStage.setScene(todayScene);
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

	    makeRain();

		todayObjects.getChildren().add(statsContainer);

		todayObjects.getChildren().add(tempContainer);
		todayObjects.getChildren().add(temperatureBox);

		todayObjects.getChildren().add(rainContainer);
		todayObjects.getChildren().add(rainBox);
	}

	private void makeTemp() throws FileNotFoundException {

		tempContainer = new Rectangle(220, 437.5, Color.WHITE);
		tempContainer.setX(70);
		tempContainer.setY(425);
		tempContainer.setArcHeight(25);
		tempContainer.setArcWidth(25);
		tempContainer.setOpacity(0.3);

		tempHigh = new Text(temperature + "°F");
		tempHigh.setFont(Font.font("Tahoma", FontWeight.NORMAL, FontPosture.REGULAR, 40));

		temperatureBox.setLayoutX(70);
		temperatureBox.setLayoutY(425);
		temperatureBox.setPrefSize(220, 437.5);
		temperatureBox.setAlignment(Pos.TOP_CENTER);
		temperatureBox.setPadding(new Insets(20));

		temperatureBox.getChildren().add(tempHigh);

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

    private void makeRain() throws FileNotFoundException {

        rainContainer = new Rectangle(220, (437.5 - 30) / 2 + 5, Color.WHITE);
        rainContainer.setX(310);
        rainContainer.setY(425 + (437.5 - 30) / 2 + 5 + 20);
        rainContainer.setArcWidth(25);
        rainContainer.setArcHeight(25);
        rainContainer.setOpacity(0.3);

        rainBox = new VBox();
        rainBox.setLayoutX(310);
        rainBox.setLayoutY(425 + (437.5 - 30) / 2 + 5 + 20);
        rainBox.setPrefSize(220, (437.5 - 30) / 2 + 5);
        rainBox.setAlignment(Pos.TOP_CENTER);
        rainBox.setPadding(new Insets(20));

        if(rainChance > 75) {
            rainDrop = makeView(new Image(new FileInputStream("src\\main\\resources\\WaterDrop100%.png")),
                    0, 0, 150, 150, 1);
        }
        else if(rainChance > 50) {
            rainDrop = makeView(new Image(new FileInputStream("src\\main\\resources\\WaterDrop75%.png")),
                    0, 0, 150, 150, 1);
        }
        else if(rainChance > 25) {
            rainDrop = makeView(new Image(new FileInputStream("src\\main\\resources\\WaterDrop50%.png")),
                    0, 0, 150, 150, 1);
        }
        else {
            rainDrop = makeView(new Image(new FileInputStream("src\\main\\resources\\WaterDrop25%.png")),
                    0, 0, 150, 150, 1);
        }

        rainProb = new Text(rainChance + "% chance of rain");
        rainProb.setFont(Font.font("Tahoma", FontWeight.NORMAL, FontPosture.REGULAR, 20));
        rainProb.setFill(Color.WHITE);

        rainBox.getChildren().add(rainProb);
        rainBox.getChildren().add(rainDrop);

    }

	private void makeButton1(Stage primaryStage) throws FileNotFoundException {


		upButton = new Button();
		upButton.setPrefWidth(70);
		upButton.setPrefHeight(30);
		upButton.setOpacity(1);

		upButton.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream("src\\main\\resources\\ArrowDim.png")), null, null, null,
				new BackgroundSize(100 ,50, true, true, true, true))));
				// No idea how BackgroundSize works, I just made everything true and it worked

		upButton.setOnAction(e -> {

			primaryStage.setScene(forecastScene);
			System.out.println("Pressed");
		});

		upButton.setOnMouseEntered(e -> {
            try {
                upButton.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream("src\\main\\resources\\ArrowBright.png")), null, null, null,
                        new BackgroundSize(100 ,50, true, true, true, true))));
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
		});

		upButton.setOnMouseExited(e -> {
			try {
				upButton.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream("src\\main\\resources\\ArrowDim.png")), null, null, null,
						new BackgroundSize(100 ,50, true, true, true, true))));
			} catch (FileNotFoundException ex) {
				throw new RuntimeException(ex);
			}
		});

		upButtonBox = new HBox(upButton);
		upButtonBox.setPrefWidth(600);
		upButtonBox.setPrefHeight(50);
		upButtonBox.setAlignment(Pos.TOP_CENTER);
		upButtonBox.setLayoutX(0);
		upButtonBox.setLayoutY(850);

		todayObjects.getChildren().add(upButtonBox);

	}

	private void makeBackground() {
		Rectangle background = colorRectangle("#101e63", "#103363");
		background.setWidth(600);
		background.setHeight(900);

		forecastObjects.getChildren().add(background);
	}


	/*
		HBox allContainers;
	VBox todayBox, tomorrowBox, dayAfterBox;

	Rectangle background;
	Rectangle todayContainer, tomorrowContainer, dayAfterContainer;
	ImageView sun1, sun2, sun3, moon1, moon2, moon3;
	ImageView wind1, wind2, wind3, rain1, rain2, rain3;
	Text date1, date2, date3;
	 */

	private void makeBoxes() throws FileNotFoundException {

		// When the time is between 6pm-6am, the Period at index 0 will be overnight and
		// the daytime reading for today will be lost. This offsets a specific day's day and night reading
		// in the period arrayList by 1. The code below will account for that:
		int periodOffset = 0;
		if(!forecast.get(0).isDaytime) {
			periodOffset = 1;
		}

//		Pane testBox = makeForecastDay(0, 1);
		todayBox = makeForecastDay(0 - periodOffset);
		todayBox.setPrefWidth(150);
		todayBox.setPrefHeight(800);

		tomorrowBox = makeForecastDay(2 - periodOffset);

		dayAfterBox = makeForecastDay(4 - periodOffset);



		allContainers = new HBox(todayBox, tomorrowBox, dayAfterBox);
		allContainers.setPadding(new Insets(25));
		allContainers.setSpacing(50);
		allContainers.setAlignment(Pos.CENTER);

		forecastObjects.getChildren().add(allContainers);
	}

	private VBox makeForecastDay(int dayInd) throws FileNotFoundException {

		if(dayInd < 0) {
			dayInd = 0;
		}

		StackPane dateBox, rainBox, windBox, tempBox;

		//				-= Date =-

		Rectangle dateContainer = new Rectangle(150, 50, Color.WHITE);
		dateContainer.setOpacity(0.3);
		dateContainer.setArcHeight(25);
		dateContainer.setArcWidth(25);

		Text date = new Text(forecast.get(dayInd).name);
		date.setFont(Font.font("Tahoma", FontWeight.NORMAL, FontPosture.REGULAR, 30));
		date.setFill(Color.WHITE);

		if(dayInd == 0) {
			date.setText("Today");
		}

		dateBox = new StackPane(dateContainer, date);

		//				-= Rain =-

		Rectangle rainContainer = new Rectangle(150, 150, Color.WHITE);
		rainContainer.setOpacity(0.3);
		rainContainer.setArcWidth(25);
		rainContainer.setArcHeight(25);

		ImageView rainIcon = new ImageView(new Image(new FileInputStream("src\\main\\resources\\rainCloud.png")));
		rainIcon.setFitWidth(150);
		rainIcon.setFitHeight(150);
		rainIcon.setOpacity(0.5);

		Text rainText = new Text(forecast.get(dayInd).probabilityOfPrecipitation.value + "%");
		rainText.setFont(Font.font("Tahoma", FontWeight.NORMAL, FontPosture.REGULAR, 50));
		rainText.setFill(Color.WHITE);

		rainBox = new StackPane(rainContainer, rainIcon, rainText);

		//				-= Wind =-

		Rectangle windContainer = new Rectangle(150, 150, Color.WHITE);
		windContainer.setOpacity(0.3);
		windContainer.setArcWidth(25);
		windContainer.setArcHeight(25);

		ImageView windIcon = new ImageView(new Image(new FileInputStream("src\\main\\resources\\Wind.png")));
		windIcon.setFitWidth(150);
		windIcon.setFitHeight(150);
		windIcon.setOpacity(0.5);

		windBox = new StackPane(windContainer, windIcon);

		//				-= Temperature =-



		VBox forecastDay = new VBox(dateBox, rainBox, windBox);
		forecastDay.setPrefWidth(150);
		forecastDay.setPrefHeight(850);
		return forecastDay;
	}

	private VBox makeForecastBackground(int dayInd, int nightInd) {
		VBox toRet = new VBox();

		return toRet;
	}
}
