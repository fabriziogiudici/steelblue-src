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
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.application.Application;
import javafx.application.Platform;
import com.aquafx_project.AquaFx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    
    private Splash splash = new Splash(this);

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
        splash.show();

        getExecutor().execute(new Runnable() // FIXME: use JavaFX Worker?
          {
            @Override
            public void run()
              {
                initializeInBackground();
                Platform.runLater(new Runnable()
                  {
                    @Override
                    public void run()
                      {
                        try
                          {
                            final Parent application = createParent();
                            final Scene scene = new Scene(application);

                            if (isOSX())
                              {
                                setMacOSXLookAndFeel(scene);
                              }

                            stage.setScene(scene);
                            onStageCreated(stage);
                            stage.show();

                            stage.setOnCloseRequest(new EventHandler<WindowEvent>()
                              {
                                @Override
                                public void handle (final @Nonnull WindowEvent event)
                                  {
                                    log.info("handle({})", event);
                                    onClosing();
                                  }
                              });

                            splash.dismiss();
                          }
                        catch (IOException e)
                          {
                            log.error("", e);
                          }
                      }
                  });
              }
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
  }
