/*
 * #%L
 * *********************************************************************************************************************
 *
 * SteelBlue
 * http://steelblue.tidalwave.it - git clone git@bitbucket.org:tidalwave/steelblue-src.git
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
package it.tidalwave.role.ui.javafx.impl.dialog;

import javax.annotation.Nonnull;
import java.util.concurrent.Executor;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import it.tidalwave.util.ui.UserNotificationWithFeedback;
import it.tidalwave.role.ui.BoundProperty;
import it.tidalwave.role.ui.javafx.impl.common.DelegateSupport;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class DialogBindings extends DelegateSupport
  {
    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public DialogBindings (final @Nonnull Executor executor)
      {
        super(executor);
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    public void showInModalDialog (final @Nonnull Node node, final @Nonnull UserNotificationWithFeedback notification)
      {
        showInModalDialog(node, notification, new BoundProperty<>(true));
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    public void showInModalDialog (final @Nonnull Node node,
                                   final @Nonnull UserNotificationWithFeedback notification,
                                   final @Nonnull BoundProperty<Boolean> valid)
      {
        Platform.runLater(new Runnable() // FIXME: should not be needed
          {
            @Override
            public void run()
              {
                log.info("modalDialog({}, {})", node, notification);

                final Stage dialogStage = new Stage(StageStyle.DECORATED);
                dialogStage.setResizable(false);
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                dialogStage.initOwner(mainWindow);
                dialogStage.setTitle(notification.getCaption());

                final VBox vbox = new VBox();
                vbox.setPadding(new Insets(8, 8, 8, 8));
                final FlowPane buttonPane = new FlowPane();
                buttonPane.setAlignment(Pos.CENTER_RIGHT);
                buttonPane.setHgap(8);

                final Button okButton = new Button("Ok");
                final Button cancelButton = new Button("Cancel");

                if (isOSX())
                  {
                    buttonPane.getChildren().add(cancelButton);
                    buttonPane.getChildren().add(okButton);
                  }
                else
                  {
                    buttonPane.getChildren().add(okButton);
                    buttonPane.getChildren().add(cancelButton);
                  }

                vbox.getChildren().add(node);
                vbox.getChildren().add(buttonPane);

                okButton.setDefaultButton(true);
                cancelButton.setCancelButton(true);

                final DialogCloserHandler closeAndConfirm = new DialogCloserHandler(executor, dialogStage)
                  {
                    @Override
                    protected void doSomething() throws Exception
                      {
                        notification.getFeedback().onConfirm();
                      }
                  };

                final DialogCloserHandler closeAndCancel = new DialogCloserHandler(executor, dialogStage)
                  {
                    @Override
                    protected void doSomething() throws Exception
                      {
                        notification.getFeedback().onCancel();
                      }
                  };

//                okButton.disableProperty().bind(new PropertyAdapter<>(valid)); // FIXME: doesn't work

                dialogStage.setOnCloseRequest(event -> executor.execute(() ->
                  {
                    try
                      {
                        notification.getFeedback().onCancel();
                      }
                    catch (Exception e)
                      {
                        log.error("", e);
                      }
                  }));

                okButton.setOnAction(closeAndConfirm);
                cancelButton.setOnAction(closeAndCancel);

                dialogStage.setScene(new Scene(vbox));
                dialogStage.centerOnScreen();
                dialogStage.showAndWait();
              }
          });
      }
  }
