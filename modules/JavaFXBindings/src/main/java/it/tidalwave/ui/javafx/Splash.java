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
import java.io.IOException;
import javafx.util.Duration;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor @Slf4j
public class Splash
  {
    @Nonnull
    private final Object application;

    private Pane splashPane;

    private Stage splashStage;

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public void init()
      {
        try
          {
            splashPane = FXMLLoader.load(application.getClass().getResource("Splash.fxml"));
          }
        catch (IOException e)
          {
            log.warn("", e);
          }
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public void show()
      {
        splashStage = new Stage(StageStyle.UNDECORATED);
        final Scene splashScene = new Scene(splashPane);
        splashStage.setScene(splashScene);
        splashStage.centerOnScreen();
        splashStage.show();
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public void dismiss()
      {
//        loadProgress.progressProperty().unbind();
//        loadProgress.setProgress(1);
//        progressText.setText("Done.");
        splashStage.toFront();
        final FadeTransition fadeSplash = new FadeTransition(Duration.seconds(0.5), splashPane);
        fadeSplash.setFromValue(1.0);
        fadeSplash.setToValue(0.0);
        fadeSplash.setOnFinished(new EventHandler<ActionEvent>()
          {
            @Override
            public void handle (final @Nonnull ActionEvent actionEvent)
              {
                splashStage.close();
              }
          });

        fadeSplash.play();
      }
  }
