/*
 * #%L
 * *********************************************************************************************************************
 *
 * SteelBlue
 * http://steelblue.tidalwave.it - hg clone https://bitbucket.org/tidalwave/steelblue-src
 * %%
 * Copyright (C) 2015 - 2015 Tidalwave s.a.s. (http://tidalwave.it)
 * %%
 *
 * *********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * *********************************************************************************************************************
 *
 * $Id$
 *
 * *********************************************************************************************************************
 * #L%
 */
package it.tidalwave.ui.javafx;

import javax.annotation.Nonnull;
import java.util.concurrent.Executors;
import java.util.concurrent.Executor;
import java.io.IOException;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.application.Application;
import javafx.application.Platform;
import com.aquafx_project.AquaFx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.Getter;
import lombok.Setter;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public abstract class JavaFXApplicationWithSplash extends Application
  {
    // Don't use Slf4j and its static logger - give Main a chance to initialize things
    private final Logger log = LoggerFactory.getLogger(JavaFXApplicationWithSplash.class);
    
    private final Splash splash = new Splash(this);
    
    @Getter @Setter
    private boolean fullScreen;
    
    @Getter @Setter
    private boolean fullScreenLocked;

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void init()
      {
        log.info("init()");
        splash.init();
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void start (final @Nonnull Stage stage)
      throws Exception
      {
        log.info("start({})", stage);
        stage.setFullScreen(fullScreen);
        final Stage splashStage = new Stage(StageStyle.UNDECORATED);
        configureFullScreen(stage);
        configureFullScreen(splashStage);
        splash.show(splashStage);

        getExecutor().execute(() -> // FIXME: use JavaFX Worker?
          {
            initializeInBackground();
            Platform.runLater(() ->
              {
                try
                  {
                    final Parent application = createParent();
                    final Scene scene = new Scene(application);

                    if (isOSX())
                      {
                        setMacOSXLookAndFeel(scene);
                      }

                    stage.setOnCloseRequest(event -> onClosing());
                    stage.setScene(scene);
                    onStageCreated(stage);
                    stage.show();
                    splash.dismiss();
                  }
                catch (IOException e)
                  {
                    log.error("", e);
                  }
              });
          });
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    protected void onStageCreated (@Nonnull Stage stage)
      {
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    protected abstract Parent createParent()
      throws IOException;

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    protected abstract void initializeInBackground();

    /*******************************************************************************************************************
     *
     * Invoked when the main {@link Stage} is being closed.
     *
     ******************************************************************************************************************/
    protected void onClosing()
      {
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    protected Executor getExecutor()
      {
        return Executors.newSingleThreadExecutor();
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    private void setMacOSXLookAndFeel (final @Nonnull Scene scene)
      {
        if (isOSX())
          {
            log.info("Setting Aqua style");
            AquaFx.style();
          }
      }

    /*******************************************************************************************************************
     *
     * TODO: delegate to a provider
     *
     ******************************************************************************************************************/
    public static boolean isOSX()
      {
        return System.getProperty("os.name").contains("OS X");
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    private void configureFullScreen (final @Nonnull Stage stage)
      {
        stage.setFullScreen(fullScreen);
        
        if (fullScreen && fullScreenLocked)
          {
            stage.setFullScreenExitHint("");
            stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
          }
      }
  }
