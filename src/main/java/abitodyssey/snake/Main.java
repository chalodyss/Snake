// Copyright (c) 2024, Charles T.

package abitodyssey.snake;


import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.Random;


class Game {

    static IntegerProperty score = new SimpleIntegerProperty(0);

    static Random          rand  = new Random();
    static Rectangle       food  = new Rectangle(100, 200, 20, 20);
    static Rectangle[]     snake;
    static int             length;
    static char            direction;

    static {
        reset();
    }


    private Game() {}

    static void reset() {
        length    = 1;
        direction = 'W';

        score.set(0);
        food.setFill(Color.LIME);
        food.setX(100);
        food.setY(200);

        snake = new Rectangle[256];
        snake[0] = new Rectangle(20, 20, Color.BLACK);
        snake[0].setX(300);
        snake[0].setY(200);
    }

    static void update() {
        eat();
        move();
    }

    static boolean isGameOver() {
        if (snake[0].getY() < 0 || snake[0].getX() < 0
            || snake[0].getY() > 480 || snake[0].getX() > 480) {
            return true;
        } else {
            for (var i = 1; i < length; i++) {
                if ((snake[0].getX() == snake[i].getX()) && (snake[0].getY() == snake[i].getY())) {
                    return true;
                }
            }
        }

        return false;
    }

    static void eat() {
        if (snake[0].getX() == food.getX() && snake[0].getY() == food.getY() && length < 256) {
            snake[length] = new Rectangle(20, 20, Color.LIGHTGRAY);
            snake[length].setX(snake[length - 1].getX());
            snake[length].setY(snake[length - 1].getY());
            length++;
            score.set(score.get() + 1);
            createFood();
        }
    }

    static void move() {
        for (var i = length - 1; i > 0; i--) {
            snake[i].setX(snake[i - 1].getX());
            snake[i].setY(snake[i - 1].getY());
        }

        switch (direction) {
            case 'N' -> snake[0].setY(snake[0].getY() - 20);
            case 'W' -> snake[0].setX(snake[0].getX() - 20);
            case 'S' -> snake[0].setY(snake[0].getY() + 20);
            case 'E' -> snake[0].setX(snake[0].getX() + 20);
        }
    }

    static void createFood() {
        int[][] map   = new int[25][25];
        int[]   cases = new int[625];
        var     k     = 0;

        for (var i = 0; i < length; i++) {
            var m = (int) snake[i].getY() / 20;
            var n = (int) snake[i].getX() / 20;
            map[m][n] = 1;
        }

        for (var i = 0; i < 25; i++) {
            for (var j = 0; j < 25; j++) {
                if (map[i][j] == 0) {
                    cases[k] = i * 25 + j;
                    k++;
                }
            }
        }

        k = rand.nextInt(k);
        food.setX((cases[k] % 25) * 20);
        food.setY((int) (cases[k] / 25) * 20);
    }

}

class Renderer {

    private Renderer() { }

    static void clean(Pane board) {
        board.getChildren().clear();
    }

    static void render(Pane board) {
        if (!board.getChildren().contains(Game.food))
            board.getChildren().add(Game.food);

        for (var i = 0; i < Game.length; i++) {
            if (!board.getChildren().contains(Game.snake[i])) {
                board.getChildren().add(Game.snake[i]);
            }
        }
    }

}

class Controller {

    @FXML
    Pane            board;
    @FXML
    Label           score;

    AnimationTimer  loop;


    Controller() {
        loop = new AnimationTimer() {
            long lastTime;

            @Override
            public void handle(long now) {
                long time = now - lastTime;

                if (time >= 70_000_000L) {
                    if (!Game.isGameOver()) {
                        Game.update();
                        Renderer.render(board);
                    } else {
                        stop();
                    }
                    lastTime = now;
                }
            }
        };
    }

    @FXML
    void initialize() {
        score.textProperty().bind(Bindings.convert(Game.score));
        Renderer.render(board);
    }

    @FXML
    void start() {
        loop.start();
    }

    @FXML
    void reset() {
        loop.stop();
        Game.reset();
        Renderer.clean(board);
        Renderer.render(board);
    }

    void move(KeyEvent e) {
        switch (e.getCode()) {
            case UP    -> Game.direction = 'N';
            case RIGHT -> Game.direction = 'E';
            case LEFT  -> Game.direction = 'W';
            case DOWN  -> Game.direction = 'S';
        }
    }

}

public class Main extends Application {

    public void start(Stage stage) {
        try {
            Controller controller = new Controller();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/View.fxml"));
            loader.setController(controller);

            BorderPane root = loader.load();

            Scene scene = new Scene(root);
            scene.setOnKeyPressed(controller::move);

            stage.setResizable(false);
            stage.setTitle("Snake");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
