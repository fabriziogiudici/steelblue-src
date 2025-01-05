/*
 * *************************************************************************************************************************************************************
 *
 * SteelBlue: DCI User Interfaces
 * http://tidalwave.it/projects/steelblue
 *
 * Copyright (C) 2015 - 2025 by Tidalwave s.a.s. (http://tidalwave.it)
 *
 * *************************************************************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 *
 * *************************************************************************************************************************************************************
 *
 * git clone https://bitbucket.org/tidalwave/steelblue-src
 * git clone https://github.com/tidalwave-it/steelblue-src
 *
 * *************************************************************************************************************************************************************
 */
package it.tidalwave.ui.javafx;

import jakarta.annotation.Nonnull;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.io.IOException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.application.Application;
import javafx.application.Platform;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.tidalwave.util.Key;
import it.tidalwave.util.PreferencesHandler;
import lombok.Getter;
import lombok.Setter;

/***************************************************************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public abstract class JavaFXApplicationWithSplash extends Application
  {
    private static final String K_BASE_NAME = "it.tidalwave.javafx";

    /** A property representing the initial main window size as a percentual of the screen size. */
    public static final Key<Double> K_INITIAL_SIZE = Key.of(K_BASE_NAME + ".initialSize", Double.class);

    /** Whether the application should start maximized. */
    public static final Key<Boolean> K_MAXIMIZED = Key.of(K_BASE_NAME + ".maximized", Boolean.class);

    /** Whether the application should start at full screen. */
    public static final Key<Boolean> K_FULL_SCREEN = Key.of(K_BASE_NAME + ".fullScreen", Boolean.class);

    /** Whether the application should always stay at full screen. */
    public static final Key<Boolean> K_FULL_SCREEN_LOCKED = Key.of(K_BASE_NAME + ".fullScreenLocked", Boolean.class);

    /** The minimum duration of the splash screen. */
    public static final Key<Duration> K_MIN_SPLASH_DURATION = Key.of(K_BASE_NAME + ".minSplashDuration", Duration.class);

    private static final String DEFAULT_APPLICATION_FXML = "Application.fxml";

    private static final String DEFAULT_SPLASH_FXML = "Splash.fxml";

    private static final Duration DEFAULT_MIN_SPLASH_DURATION = Duration.seconds(2);

    // Don't use Slf4j and its static logger - give Main a chance to initialize things
    private final Logger log = LoggerFactory.getLogger(JavaFXApplicationWithSplash.class);

    private Splash splash;

    private boolean maximized;

    private boolean fullScreen;

    private boolean fullScreenLocked;

    @Getter @Setter
    protected String applicationFxml = DEFAULT_APPLICATION_FXML;

    @Getter @Setter
    protected String splashFxml = DEFAULT_SPLASH_FXML;

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    @Override
    public void init()
      {
        log.info("init()");
        splash = new Splash(this, splashFxml, this::createScene);
        splash.init();
      }

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    @Override
    public void start (@Nonnull final Stage stage)
      {
        log.info("start({})", stage);
        final var preferencesHandler = PreferencesHandler.getInstance();
        fullScreen = preferencesHandler.getProperty(K_FULL_SCREEN).orElse(false);
        fullScreenLocked = preferencesHandler.getProperty(K_FULL_SCREEN_LOCKED).orElse(false);
        maximized = preferencesHandler.getProperty(K_MAXIMIZED).orElse(false);

        final var splashStage = new Stage(StageStyle.TRANSPARENT);
        stage.setMaximized(maximized);
//        splashStage.setMaximized(maximized); FIXME: doesn't work
        configureFullScreen(stage);
//        configureFullScreen(splashStage); FIXME: deadlocks JDK 1.8.0_40

        if (!maximized && !fullScreen)
          {
            splashStage.centerOnScreen();
          }

        final var splashCreationTime = System.currentTimeMillis();
        splash.show(splashStage);

        getExecutor().execute(() -> // FIXME: use JavaFX Worker?
          {
            initializeInBackground();
            Platform.runLater(() ->
              {
                try
                  {
                    final var applicationNad = createParent();
                    final var scene = createScene((Parent)applicationNad.getNode());
                    stage.setOnCloseRequest(event -> onClosing());
                    stage.setScene(scene);
                    onStageCreated(stage, applicationNad);
                    stage.setFullScreen(fullScreen);
                    final double scale = preferencesHandler.getProperty(K_INITIAL_SIZE).orElse(0.65);
                    final var screenSize = Screen.getPrimary().getBounds();
                    stage.setWidth(scale * screenSize.getWidth());
                    stage.setHeight(scale * screenSize.getHeight());
                    stage.show();
                    splashStage.toFront();

                    final var duration = preferencesHandler.getProperty(K_MIN_SPLASH_DURATION).orElse(DEFAULT_MIN_SPLASH_DURATION);
                    final var delay = Math.max(0, splashCreationTime + duration.toMillis() - System.currentTimeMillis());
                    final var dismissSplash = new Timeline(new KeyFrame(Duration.millis(delay), e -> splash.dismiss()));
                    Platform.runLater(dismissSplash::play);
                  }
                catch (IOException e)
                  {
                    log.error("", e);
                  }
              });
          });
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    protected void onStageCreated (@Nonnull final Stage stage, @Nonnull final NodeAndDelegate<?> applicationNad)
      {
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    @Nonnull
    protected abstract NodeAndDelegate<?> createParent()
      throws IOException;

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    protected abstract void initializeInBackground();

    /***********************************************************************************************************************************************************
     * Invoked when the main {@link Stage} is being closed.
     **********************************************************************************************************************************************************/
    protected void onClosing()
      {
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    @Nonnull
    protected Executor getExecutor()
      {
        return Executors.newSingleThreadExecutor();
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    protected Scene createScene (@Nonnull final Parent parent)
      {
        final var scene = new Scene(parent);
        final var jMetro = new JMetro(Style.DARK);
        jMetro.setScene(scene);
        return scene;
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    private void configureFullScreen (@Nonnull final Stage stage)
      {
        stage.setFullScreen(fullScreen);

        if (fullScreen && fullScreenLocked)
          {
            stage.setFullScreenExitHint("");
            stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
          }
      }
  }
