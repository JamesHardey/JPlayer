package com.example.jplayer.controller;


import com.example.jplayer.JVplayer;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;



public class JVPlayerController implements Initializable {
    @FXML
    private BorderPane root;
    @FXML
    private Button btn;
    @FXML
    private AnchorPane pane;
    private Stage stage;
    @FXML
    private VBox control;
    private VBox newControl;
    private Media md;
    private MediaPlayer mp;
    private MediaView mv;
    private Duration dur;
    @FXML
    private FontAwesomeIconView playIcon;
    @FXML
    private FontAwesomeIconView volIcon;
    @FXML
    private Slider prog;
    @FXML
    private Slider vol;
    @FXML
    private Label time;

    private final Vector<String> history;
    private final Vector<String> name;
    private int num;
    private double increment;
    boolean stop;
    private boolean full;
    private StackPane p;
    private boolean start;

    public JVPlayerController() {
        this.history = new Vector<>(10);
        this.name = new Vector<>(10);
        this.num = 0;
        this.increment = 1.0;
        this.stop = false;
        this.start = false;
    }

    public void initialize(final URL url, final ResourceBundle rb) {
        final FadeTransition ft = new FadeTransition();
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.setDuration(Duration.seconds(5.0));
        this.p = new StackPane();
        try {
            final Parent roota = FXMLLoader.load((Objects.requireNonNull(JVplayer.class.getResource("LoadFXML.fxml"))));
            this.root.setCenter(roota);
            ft.setNode(this.root.getCenter());
            ft.play();
            ft.setOnFinished(e -> {
                this.root.setCenter(this.pane);
                this.prog.setValue(0.0);
                this.vol.setValue(0.0);
            });
        }
        catch (IOException ex) {
            Logger.getLogger(JVPlayerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void reload() {
        final FadeTransition ft = new FadeTransition();
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.setDuration(Duration.seconds(5.0));
        try {
            final Parent roota = FXMLLoader.load(Objects.requireNonNull(this.getClass().getResource("LoadFXML.fxml")));
            this.root.setCenter(roota);
            ft.setNode(this.root.getCenter());
            ft.play();
            ft.setOnFinished(e -> this.root.setCenter(this.pane));
        }
        catch (IOException ex) {
            Logger.getLogger(JVPlayerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void loadVideo() {
        final FileChooser fi = new FileChooser();
        fi.setInitialDirectory(new File("C://"));
        final File fa = fi.showOpenDialog((Window)null);
        if (fa == null) {
            return;
        }
        try {
            final File fd = fa.getAbsoluteFile();
            final String videoName = fa.getName();
            final String path = fd.toURI().toString();
            this.start = true;
            this.load(path, videoName);
            this.full = false;
            this.addHistoryAndName(path, videoName);
        }
        catch (Exception ex) {
            System.out.println("Error "+ Arrays.toString(ex.getStackTrace()));
        }
    }

    private void load(final String path, final String filename) {
        if (this.getMPlayer() != null) {
            if (this.getMPlayer().getStatus() == MediaPlayer.Status.PLAYING) {
                this.mp.stop();
            }
            this.update("pause");
        }
        final String filename2 = filename.substring(0, filename.lastIndexOf("."));
        (this.stage = (Stage)this.root.getScene().getWindow()).setTitle(filename2);
        this.mv = new MediaView();
        this.root.setCenter(this.mv);
        this.mv.setPreserveRatio(true);
        final double add = this.control.getHeight() + 5.0;
        this.control.prefWidthProperty().bind(this.root.widthProperty());
        this.pane.prefHeightProperty().bind(this.root.heightProperty());
        this.pane.prefWidthProperty().bind(this.root.widthProperty());
        this.mv.fitWidthProperty().bind(this.root.widthProperty());
        this.mv.fitHeightProperty().bind(this.root.heightProperty().subtract(add));
        this.md = new Media(path);

        this.setMPlayer(this.mp = new MediaPlayer(this.md));
        this.mp.setOnReady(() -> {
            this.dur = this.mp.getMedia().getDuration();
            this.updateValues();
        });
        this.mv.setMediaPlayer(this.mp);


        this.mv.setOnMouseClicked(e -> {
            if (e.getClickCount() >= 2) {
                this.stage = (Stage)this.root.getScene().getWindow();
                if (!this.full) {
                    this.full = true;
                    this.p.getChildren().add(this.mv);
                    this.root.setCenter(this.p);
                    this.stage.setFullScreenExitHint("");
                    this.stage.setFullScreen(this.full);
                    this.root.setBottom(null);
                }
                else {
                    this.full = false;
                    this.p.getChildren().removeAll();
                    this.root.setCenter(this.mv);
                    this.stage.setFullScreen(this.full);
                    this.root.setBottom(this.control);
                }
            }
        });
        this.root.setOnKeyPressed(e -> {
            if (this.mp != null) {
                if (e.getCode() == KeyCode.RIGHT) {
                    this.moveForward();
                }
                if (e.getCode() == KeyCode.LEFT) {
                    this.moveBackward();
                }
            }
        });
        this.mp.currentTimeProperty().addListener((observable, oldValue, newValue) -> this.updateValues());
        this.vol.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (this.vol.isValueChanging()) {
                this.mp.setVolume(this.vol.getValue() / 100.0);
                if (this.vol.getValue() >= 50.0) {
                    this.volIcon.setGlyphName("VOLUME_UP");
                }
                else if (this.vol.getValue() == 0.0) {
                    this.volIcon.setGlyphName("VOLUME_OFF");
                }
                else {
                    this.volIcon.setGlyphName("VOLUME_DOWN");
                }
            }
            this.updateValues();
        });
        this.prog.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (this.prog.isValueChanging()) {
                if (this.dur != null) {
                    this.mp.seek(this.dur.multiply(this.prog.getValue() / 100.0));
                }
                this.updateValues();
            }
        });
        this.mp.setAutoPlay(true);
        final Duration cur = this.mp.getCurrentTime();
        this.dur = this.mp.getMedia().getDuration();
        this.time.setText(this.formatTime(cur, this.dur));
        System.out.println("-----------");
        this.update("play");
    }

    private String formatTime(final Duration elapsed, final Duration duration) {
        int intElapsed = (int)Math.floor(elapsed.toSeconds());
        final int elapsedHours = intElapsed / 3600;
        intElapsed -= elapsedHours * 3600; // Subtract hours

        final int elapsedMinutes = intElapsed / 60;
        final int elapsedSeconds = intElapsed % 60;

        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int)Math.floor(duration.toSeconds());
            final int durationHours = intDuration / 3600;
            intDuration -= durationHours * 3600; // Subtract hours

            final int durationMinutes = intDuration / 60;
            final int durationSeconds = intDuration % 60;

            if (durationHours > 0) {
                return String.format("%d:%02d:%02d/%d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds, durationHours, durationMinutes, durationSeconds);
            }
            return String.format("%02d:%02d/%02d:%02d", elapsedMinutes, elapsedSeconds, durationMinutes, durationSeconds);
        } else {
            if (elapsedHours > 0) {
                return String.format("%d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds);
            }
            return String.format("%02d:%02d", elapsedMinutes, elapsedSeconds);
        }
    }


//    private String formatTime(final Duration elapsed, final Duration duration) {
//        int intElapsed = (int)Math.floor(elapsed.toSeconds());
//        final int elapsedHours = intElapsed / 3600;
//        if (elapsedHours > 0) {
//            intElapsed -= elapsedHours * 60 * 60;
//        }
//        final int elapsedMinutes = intElapsed / 60;
//        final int elapsedSeconds = intElapsed - elapsedHours * 60 * 60 - elapsedMinutes * 60;
//        if (duration.greaterThan(Duration.ZERO)) {
//            int intDuration = (int)Math.floor(duration.toSeconds());
//            final int durationHours = intDuration / 3600;
//            if (durationHours > 0) {
//                intDuration -= durationHours * 60 * 60;
//            }
//            final int durationMinutes = intDuration / 60;
//            final int durationSeconds = intDuration - durationHours * 60 * 60 - durationMinutes * 60;
//            if (durationHours > 0) {
//                return String.format("%2d:%02d:%02d/%2d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds, durationHours, durationMinutes, durationSeconds);
//            }
//            return String.format("%02d:%02d/%02d:%02d", elapsedMinutes, elapsedSeconds, durationMinutes, durationSeconds);
//        }
//        else {
//            if (elapsedHours > 0) {
//                return String.format("%d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds);
//            }
//            return String.format("%02d:%02d", elapsedMinutes, elapsedSeconds);
//        }
//    }

    protected void updateValues() {
        if (this.time != null && this.prog != null && this.vol != null && this.dur != null) {
            final Duration[] currentTime = new Duration[1];
            Platform.runLater(() -> {
                currentTime[0] = this.mp.getCurrentTime();
                this.time.setText(this.formatTime(currentTime[0], this.dur));
                this.prog.setDisable(this.dur.isUnknown());
                if (!this.prog.isDisabled() && this.dur.greaterThan(Duration.ZERO) && !this.prog.isValueChanging()) {
                    this.prog.setValue(currentTime[0].divide(this.dur).toMillis() * 100.0);
                }
                if (!this.vol.isValueChanging()) {
                    this.vol.setValue((int)Math.round(this.mp.getVolume() * 100.0));
                }
            });
        }
    }

    protected int getMediaHeight(final String path) {
        this.md = new Media(path);
        return this.md.getHeight();
    }

    protected int getMediaWidth(final String path) {
        this.md = new Media(path);
        return this.md.getWidth();
    }

    private void update(final String status) {
        if ("play".equals(status)) {
            this.playIcon.setGlyphName(FontAwesomeIcon.PAUSE.name());
        }
        if ("pause".equals(status)) {
            this.playIcon.setGlyphName(FontAwesomeIcon.PLAY.name());
        }
    }

    private void checkViewPort() {
        final Rectangle2D value = new Rectangle2D(200.0, 200.0, 400.0, 400.0);
        this.mv.viewportProperty().setValue(value);
    }

    @FXML
    void playPause(ActionEvent event) {
        if (start) {
            stop = true;
            if (this.mp.getStatus() != null) {
                if (this.mp.getStatus() == MediaPlayer.Status.PLAYING) {
                    this.update("pause");
                    this.mp.pause();
                }
                if (this.mp.getStatus() == MediaPlayer.Status.PAUSED) {
                    this.update("play");
                    this.mp.play();
                }
                if (this.mp.getStatus() == MediaPlayer.Status.STOPPED && this.historyExist()) {
                    this.load(this.getLastOnHistory(), this.getLastOnNames());
                }
            }
        }
        else {
            final Alert a = new Alert(Alert.AlertType.ERROR);
            a.show();
        }
    }

    @FXML
    void stopping(MouseEvent event) {
        final int x = event.getClickCount();
        if (this.mp.getStatus() != null) {
            if (x == 1) {
                this.mp.pause();
                this.update("pause");
            }
            else if (x == 2) {
                this.mp.stop();
                this.update("pause");
                this.returnJVPlayer();
            }
        }
    }

    private void returnJVPlayer() {
        (this.stage = (Stage)this.root.getScene().getWindow()).setTitle("JVPlayer");
        this.root.setCenter(this.pane);
    }

    private boolean historyExist() {
        return !this.history.isEmpty();
    }

    private String getLastOnHistory() {
        return this.history.lastElement();
    }

    private String getLastOnNames() {
        return this.name.lastElement();
    }

    private void addHistoryAndName(final String path, final String name) {
        if (!this.history.contains(path)) {
            this.history.add(path);
            this.name.add(name);
        }
    }

    private void setMPlayer(final MediaPlayer mp) {
        this.mp = mp;
    }

    private MediaPlayer getMPlayer() {
        return this.mp;
    }

    @FXML
    private void backSkip() {
        if (this.historyExist()) {
            final MediaPlayer md = this.getMPlayer();
            final String url = md.getMedia().getSource();
            this.num = this.history.indexOf(url);
            --this.num;
            if (this.num < 0) {
                this.num = this.history.indexOf(this.history.lastElement());
            }
            this.load(this.history.get(this.num), this.name.get(this.num));
        }
    }

    @FXML
    private void forwardSkip() {
        if (this.historyExist()) {
            final MediaPlayer mpl = this.getMPlayer();
            final String url = mpl.getMedia().getSource();
            this.num = this.history.indexOf(url);
            ++this.num;
            final int x = this.history.indexOf(this.history.lastElement());
            if (this.num > x) {
                this.num = 0;
            }
            this.load(this.history.get(this.num), this.name.get(this.num));
        }
    }

    @FXML
    private void forward() {
        this.moveForward();
    }

    @FXML
    private void backward() {
        this.moveBackward();
    }

    @FXML
    private void screenShot() {
        /*final SnapshotParameters sp = new SnapshotParameters();
        final Image image = (Image)this.pane.snapshot((SnapshotParameters)null, (WritableImage)null);
        final File file = new File("snapshot1.png");
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, (BufferedImage)null), "png", file);
        }
        catch (IOException ex) {}*/
    }

    private void moveForward() {
        this.mp.seek(this.mp.getCurrentTime().add(Duration.seconds(5.0)));
    }

    private void moveBackward() {
        this.mp.seek(this.mp.getCurrentTime().subtract(Duration.seconds(5.0)));
    }
}