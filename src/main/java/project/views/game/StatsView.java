package project.views.game;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import project.controllers.MenuController;
import project.models.game.GameModel;
import project.models.game.Stats;
import project.views.View;


import javafx.scene.control.Label;

public class StatsView extends Application implements View{
    private static final double width = 900;
    private static final double height = 600;
    private final GameModel model;
    private final Stats stats;
    private Stage stage;

    public StatsView(Stage stage,GameModel model,Stats stats) {
        this.stage = stage;
        this.model = model;
        this.stats = stats;
        start(stage);
    }

    @Override
    public void start(Stage primaryStage) {
        Label speedLabel = new Label("Speed: "+stats.getMPM()+" WPM");
        Label precisionLabel = new Label("Precision: "+stats.getAccuracy()+" %");
        Label regularityLabel = new Label("Regularity: "+stats.getRegularity());
        speedLabel.setFont(new Font(20));
        precisionLabel.setFont(new Font(20));
        regularityLabel.setFont(new Font(20));

        // Add the labels to a flow layout
        FlowPane flowPane = new FlowPane(Orientation.VERTICAL);
        flowPane.setAlignment(Pos.CENTER);
        flowPane.setColumnHalignment(HPos.CENTER);
        flowPane.setHgap(10);
        flowPane.setVgap(50);
        flowPane.setPadding(new Insets(20, 20, 20, 20));

        // Restart button to restart the game with the same settings²
        Button restartButton=new Button("Restart");
        restartButton.setOnAction(event -> {
            try{
                stage.hide();
                MenuController.getInstance().startGame();
            } catch(Exception e){
                e.printStackTrace();
            }
        });

        flowPane.getChildren().addAll(speedLabel,precisionLabel,regularityLabel,restartButton);

        // Set up the scene and show the stage
        Scene scene = new Scene(flowPane,width,height);
        stage.setScene(scene);
        stage.setTitle("Statistics");
        stage.show();
    }

    @Override
    public void update(){}

    @Override
    public void setVisible(boolean visible){
        if(visible) this.stage.show();
        else this.stage.hide();
    }
}
