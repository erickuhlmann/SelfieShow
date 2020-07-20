package sample;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Random;

public class Main extends Application {

    private static final int TIMER_INTERVAL = 100;
    private static final boolean ROTATE_IMAGE_VIEW = false;
    private static final String IMAGE_FILE_SUFFIX = "r90";

    @FXML
    ImageView imageView;

    @FXML
    GridPane gridPane;

    private static Random random = new Random();

    private Stage primaryStage;
    private Scene primaryScene;

    @Override
    public void start(Stage primaryStage) throws Exception{

        this.primaryStage = primaryStage;

        FXMLLoader loader = new FXMLLoader();
        loader.setController(this);
        loader.setLocation(getClass().getResource("sample.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("SelfieShow");
        primaryScene = new Scene(root, 300, 275);
        primaryStage.setScene(primaryScene);
        primaryStage.show();

        gridPane.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));

        loadImages();

        // get image to resize with window resize
        ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) ->
                resizeImageView(primaryStage, primaryScene);
        primaryStage.widthProperty().addListener(stageSizeListener);
        primaryStage.heightProperty().addListener(stageSizeListener);
        resizeImageView(primaryStage, primaryScene);

        if (ROTATE_IMAGE_VIEW)
            imageView.setRotate(90);

        // Handle key presses
        primaryScene.addEventHandler(KeyEvent.KEY_PRESSED, (event) -> {
            handleKeyPressed(event);
        });

        // initialise and start the timer
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(TIMER_INTERVAL), e -> handleTimerEvent()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

    }

    /**
     * Launch the application.
     * @param args The command line arguments passed in at start up.
     */
    public static void main(String[] args) {
        launch(args);
    }

    private static final int IMAGE_COUNT = 3;
    private Image[] images = new Image[IMAGE_COUNT];

    /**
     * Load the images from files.
     */
    private void loadImages()
    {
        for (int i = 0; i < images.length; i++)
        {
            try
            {
                String fn = makeImageFileName(i+1, IMAGE_FILE_SUFFIX);
                FileInputStream inputStream = new FileInputStream(makeFilePath("resources", "images", fn));
                images[i] = new Image(inputStream);
            }
            catch (FileNotFoundException ex)
            {
                System.out.println(ex.getMessage());
            }
        }
        updateImage();
    }

    /**
     * Update the visible image, randomly selected from the list of images.
     */
    private void updateImage()
    {
        int i = random.nextInt(IMAGE_COUNT);
        imageView.setImage(images[i]);
    }

    /**
     * Resizes the ImageView to fill the stage (window)
     * @param stage The stage (window).
     * @param scene The scene containing the CameraView.
     */
    private void resizeImageView(@NotNull Stage stage, Scene scene)
    {
        imageView.setFitWidth(stage.getWidth());
        imageView.setFitHeight(stage.getHeight());
    }

    /**
     * Make a file name of the format i + suffix + ".jpg".
     * @param i The first part of the filename.
     * @param suffix The second part of the filename.
     * @return The file name.
     */
    private String makeImageFileName(int i, String suffix)
    {
        if (suffix == null)
            suffix = "";
        return Integer.toString(i) + suffix + ".jpg";
    }

    /**
     * Build a file path string using the current separator character.
     * @param names Elements of the file path.
     * @return
     */
    private String makeFilePath(String ...names)
    {
        String result = "";
        if (names.length > 0)
        {
            result = names[0];
            for (int i = 1; i < names.length; i++)
            {
                result = result + File.separator + names[i];
            }
        }
        System.out.println(result);
        return result;
    }

    /**
     * Handle a timer event
     */
    private void handleTimerEvent()
    {
        updateImage();
    }

    /**
     * Handle key pressed events.
     * @param event The key pressed event.
     */
    private void handleKeyPressed(@NotNull KeyEvent event)
    {
        switch (event.getCode())
        {
            case SPACE:
                updateImage();
                break;
            case F12:
                primaryStage.setFullScreen(!primaryStage.isFullScreen());
                updateCursor();
                break;
            case ESCAPE:
                updateCursor();
                break;
        }
    }

    /**
     * Hide the cursor if we are full screen.
     */
    private void updateCursor()
    {
        if (primaryStage.isFullScreen())
        {
            primaryScene.setCursor(Cursor.NONE);
        } else
        {
            primaryScene.setCursor(Cursor.DEFAULT);
        }
    }


}
