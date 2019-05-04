/*
 * #%L
 * *********************************************************************************************************************
 *
 * SteelBlue
 * http://steelblue.tidalwave.it - git clone git@bitbucket.org:tidalwave/steelblue-src.git
 * %%
 * Copyright (C) 2015 - 2019 Tidalwave s.a.s. (http://tidalwave.it)
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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.Executor;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.application.Platform;
import it.tidalwave.util.Callback;
import it.tidalwave.util.ui.UserNotificationWithFeedback;
import it.tidalwave.util.ui.UserNotificationWithFeedback.Feedback;
import it.tidalwave.role.ui.BoundProperty;
import it.tidalwave.role.ui.javafx.impl.common.DelegateSupport;
import it.tidalwave.role.ui.spi.Feedback8;
import javafx.scene.control.Alert;
import lombok.SneakyThrows;
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
    public void showInModalDialog (final @Nonnull UserNotificationWithFeedback notification,
                                   final @Nonnull Optional<Node> node)
      {
        Platform.runLater(new Runnable() // FIXME: should not be needed
          {
            @Override
            public void run()
              {
                log.debug("modalDialog({}, {})", node, notification);

//                final Dialog<ButtonType> dialog = new Dialog<>();
                final Dialog<ButtonType> dialog = new Alert(Alert.AlertType.NONE);
                dialog.initOwner(mainWindow);
                dialog.setTitle(notification.getCaption());
                dialog.setResizable(false);
                dialog.setContentText(notification.getText());
                node.ifPresent(n -> dialog.getDialogPane().setContent(n));

                final Feedback feedback = notification.getFeedback();
                final boolean hasOnCancel = hasOnCancel(feedback);

                final ObservableList<ButtonType> buttonTypes = dialog.getDialogPane().getButtonTypes();
                buttonTypes.clear();
                buttonTypes.add(ButtonType.OK);

                if (hasOnCancel)
                  {
                    buttonTypes.add(ButtonType.CANCEL);
                  }

//                okButton.disableProperty().bind(new PropertyAdapter<>(valid)); // FIXME: doesn't work

                final Optional<ButtonType> result = dialog.showAndWait();

                if (!result.isPresent())
                  {
                    if (hasOnCancel)
                      {
                        wrap(feedback::onCancel);
                      }
                    else
                      {
                        wrap(feedback::onConfirm);
                      }
                  }
                else
                  {
                    if (result.get() == ButtonType.OK)
                      {
                        wrap(feedback::onConfirm);
                      }
                    else if (result.get() == ButtonType.CANCEL)
                      {
                        wrap(feedback::onCancel);
                      }
                    else
                      {
                        throw new IllegalStateException("Unexpected button pressed: " + result.get());
                      }
                  }
              }
          });
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @SneakyThrows(Throwable.class)
    private static void wrap (final @Nonnull Callback callback)
      {
        callback.call();
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
