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
package it.tidalwave.role.ui.javafx.example.large.mainscreen.impl;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Collection;
import java.nio.file.Path;
import java.nio.file.Paths;
import it.tidalwave.role.Aggregate;
import it.tidalwave.role.spi.DefaultDisplayable;
import it.tidalwave.role.ui.BoundProperty;
import it.tidalwave.role.ui.UserAction;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.role.ui.Selectable;
import it.tidalwave.role.ui.UserActionProviderSupplement;
import it.tidalwave.role.ui.spi.MapAggregateSupplement;
import it.tidalwave.role.ui.spi.DefaultPresentationModel;
import it.tidalwave.role.ui.spi.UserActionLambda;
import it.tidalwave.role.ui.javafx.example.large.data.Dao;
import it.tidalwave.role.ui.javafx.example.large.data.SimpleEntity;
import it.tidalwave.role.ui.javafx.example.large.data.SimpleDciEntity;
import it.tidalwave.role.ui.javafx.example.large.mainscreen.MainScreenPresentation;
import it.tidalwave.role.ui.javafx.example.large.mainscreen.MainScreenPresentation.FormFields;
import it.tidalwave.role.ui.javafx.example.large.mainscreen.MainScreenPresentationControl;
import static it.tidalwave.util.ui.UserNotificationWithFeedback.notificationWithFeedback;
import static it.tidalwave.role.ui.spi.Feedback8.feedback;
import static it.tidalwave.role.ui.spi.PresentationModelCollectors.toCompositePresentationModel;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: $
 *
 **********************************************************************************************************************/
public class DefaultMainScreenPresentationControl implements MainScreenPresentationControl
  {
    private static final Path USER_HOME = Paths.get(System.getProperty("user.home"));

    @Inject
    private MainScreenPresentation presentation;

    @Inject
    private Dao dao;

    private final FormFields fields = new FormFields();

    private int status = 1;

    private final UserAction buttonAction = new UserActionLambda(new DefaultDisplayable("Press me"),
                                                                 () -> onButtonPressed());

    private final UserAction actionDialogOk = new UserActionLambda(new DefaultDisplayable("Dialog with ok"),
                                                                 () -> onButtonDialogOkPressed());

    private final UserAction actionDialogCancelOk = new UserActionLambda(new DefaultDisplayable("Dialog with ok/cancel"),
                                                                 () -> onButtonDialogOkCancelPressed());

    private final UserAction actionPickFile = new UserActionLambda(new DefaultDisplayable("Pick file"),
                                                                 () -> onButtonPickFilePressed());

    private final UserAction actionPickDirectory = new UserActionLambda(new DefaultDisplayable("Pick directory"),
                                                                 () -> onButtonPickDirectoryPressed());

    @PostConstruct
    private void initialize()
      {
        presentation.bind(buttonAction,
                          actionDialogOk,
                          actionDialogCancelOk,
                          actionPickFile,
                          actionPickDirectory,
                          fields);
      }

    @Override
    public void start()
      {
        presentation.showUp();
        final Collection<SimpleEntity> entities1 = dao.getSimpleEntities();
        final Collection<SimpleDciEntity> entities2 = dao.getDciEntities();
        final PresentationModel pm1 = entities1.stream().map(e -> pmFor(e)).collect(toCompositePresentationModel());
        final PresentationModel pm2 = entities2.stream().map(e -> pmFor(e)).collect(toCompositePresentationModel());
        presentation.populate(pm1, pm2);
      }

    @Nonnull
    private PresentationModel pmFor (final @Nonnull SimpleEntity entity)
      {
        final Selectable selectable = () -> onSelected(entity);
        final UserAction action1 = new UserActionLambda(new DefaultDisplayable("Action 1"), () -> action1(entity));
        final UserAction action2 = new UserActionLambda(new DefaultDisplayable("Action 2"), () -> action2(entity));
        final UserAction action3 = new UserActionLambda(new DefaultDisplayable("Action 3"), () -> action3(entity));
        return new DefaultPresentationModel(entity,
                                            new DefaultDisplayable(entity.getName()),
                                            selectable,
                                            UserActionProviderSupplement.of(action1, action2, action3));
      }

    @Nonnull
    private PresentationModel pmFor (final @Nonnull SimpleDciEntity entity)
      {
        // FIXME: column names
        final Aggregate aggregate = MapAggregateSupplement.builder()
                                        .with("C1", new DefaultDisplayable(entity.getName()))
                                        .with("C2", new DefaultDisplayable("" + entity.getAttribute1()))
                                        .with("C3", new DefaultDisplayable("" + entity.getAttribute2()))
                                        .create();
        final Selectable selectable = () -> onSelected(entity);
        final UserAction action1 = new UserActionLambda(new DefaultDisplayable("Action 1"), () -> action1(entity));
        final UserAction action2 = new UserActionLambda(new DefaultDisplayable("Action 2"), () -> action2(entity));
        final UserAction action3 = new UserActionLambda(new DefaultDisplayable("Action 3"), () -> action3(entity));
        return new DefaultPresentationModel(entity,
                                            aggregate,
                                            selectable,
                                            UserActionProviderSupplement.of(action1, action2, action3));
      }

    private void onButtonPressed()
      {
        presentation.notify("Button pressed");
        status++;
        fields.textProperty.set(Integer.toString(status));
      }

    private void onButtonDialogOkPressed()
      {
        presentation.notify(notificationWithFeedback()
                .withCaption("Notification")
                .withText("Now press the button")
                .withFeedback(feedback().withOnConfirm(() -> presentation.notify("Pressed ok"))));
      }

    private void onButtonDialogOkCancelPressed()
      {
        presentation.notify(notificationWithFeedback()
                .withCaption("Notification")
                .withText("Now press the button")
                .withFeedback(feedback().withOnConfirm(() -> presentation.notify("Pressed ok"))
                                        .withOnCancel(() -> presentation.notify("Pressed cancel"))));
      }

    private void onButtonPickFilePressed()
      {
        final BoundProperty<Path> selectedFile = new BoundProperty<>(USER_HOME);
        presentation.pickFile(selectedFile,
            notificationWithFeedback()
                .withCaption("Pick a file")
                .withFeedback(feedback().withOnConfirm(() -> presentation.notify("Selected file: " + selectedFile.get()))
                                        .withOnCancel(() -> presentation.notify("Selection cancelled"))));
      }

    private void onButtonPickDirectoryPressed()
      {
        final BoundProperty<Path> selectedFolder = new BoundProperty<>(USER_HOME);
        presentation.pickDirectory(selectedFolder,
            notificationWithFeedback()
                .withCaption("Pick a directory")
                .withFeedback(feedback().withOnConfirm(() -> presentation.notify("Selected folder: " + selectedFolder.get()))
                                        .withOnCancel(() -> presentation.notify("Selection cancelled"))));
      }

    private void onSelected (final @Nonnull Object object)
      {
        presentation.notify("Selected " + object);
      }

    private void action1 (final @Nonnull Object object)
      {
        presentation.notify("Action 1 on " + object);
      }

    private void action2 (final @Nonnull Object object)
      {
        presentation.notify("Action 2 on " + object);
      }

    private void action3 (final @Nonnull Object object)
      {
        presentation.notify("Action 3 on " + object);
      }
  }
