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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import javafx.application.Platform;
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
import it.tidalwave.util.ui.UserNotificationWithFeedback;
import it.tidalwave.util.ui.UserNotificationWithFeedback.Feedback;
import it.tidalwave.role.ui.BoundProperty;
import it.tidalwave.role.ui.javafx.impl.common.DelegateSupport;
import it.tidalwave.role.ui.spi.Feedback8;
import it.tidalwave.util.Callback;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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

                final Feedback feedback = notification.getFeedback();
                final boolean hasOnCancel = hasOnCancel(feedback);
                buttonPane.getChildren().addAll(createButtons(dialogStage, feedback, hasOnCancel));

                vbox.getChildren().add(node);
                vbox.getChildren().add(buttonPane);

//                okButton.disableProperty().bind(new PropertyAdapter<>(valid)); // FIXME: doesn't work

                dialogStage.setOnCloseRequest(event -> executor.execute(() ->
                  {
                    try
                      {
                        if (hasOnCancel)
                          {
                            feedback.onCancel();
                          }
                        else
                          {
                            feedback.onConfirm();
                          }
                      }
                    catch (Exception e)
                      {
                        log.error("", e);
                      }
                  }));

                dialogStage.setScene(new Scene(vbox));
                dialogStage.centerOnScreen();
                dialogStage.showAndWait();
              }
          });
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private List<Button> createButtons (final @Nonnull Stage dialogStage,
                                        final @Nonnull Feedback feedback,
                                        final boolean hasOnCancel)
      {
        final List<Button> buttons = new ArrayList<>();
        final Button okButton = new Button("Ok");
        okButton.setDefaultButton(true);
        okButton.setOnAction(new DialogCloserHandler(executor, dialogStage, feedback::onConfirm));
        buttons.add(okButton);

        if (hasOnCancel)
          {
            final Button cancelButton = new Button("Cancel");
            cancelButton.setCancelButton(true);
            cancelButton.setOnAction(new DialogCloserHandler(executor, dialogStage, feedback::onCancel));
            buttons.add(cancelButton);
          }

        if (isOSX())
          {
            Collections.reverse(buttons);
          }

        return buttons;
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    // FIXME: refactor as methods of Feedback and Feedback8
    private static boolean hasOnCancel (final @Nonnull Feedback feedback)
      {
        try
          {
            if (feedback instanceof Feedback8)
              {
                final Field onCancelField = feedback.getClass().getDeclaredField("onCancel");
                onCancelField.setAccessible(true);
                return !onCancelField.get(feedback).equals(Callback.EMPTY);
              }
            else
              {
                final Method emptyMethod = Feedback.class.getMethod("onCancel");
                final Method actualMethod = feedback.getClass().getMethod("onCancel");
                return !actualMethod.equals(emptyMethod);
              }
          }
        catch (NoSuchMethodException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e)
          {
            throw new RuntimeException(e); // never occurs
          }
      }
  }
