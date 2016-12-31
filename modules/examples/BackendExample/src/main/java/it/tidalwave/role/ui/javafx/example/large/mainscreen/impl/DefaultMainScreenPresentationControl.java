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
import it.tidalwave.role.ui.AggregatePresentationModelBuilder;
import it.tidalwave.role.ui.BoundProperty;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.role.ui.Selectable;
import it.tidalwave.role.ui.UserAction;
import it.tidalwave.role.ui.UserActionProvider;
import it.tidalwave.role.ui.spi.DefaultPresentationModel;
import it.tidalwave.role.ui.spi.UserActionSupport8;
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
 * @stereotype  Control
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: $
 *
 **********************************************************************************************************************/
public class DefaultMainScreenPresentationControl implements MainScreenPresentationControl
  {
    private static final Path USER_HOME = Paths.get(System.getProperty("user.home"));

    // The presentation is injected by the infrastructure in form of its interface.
    @Inject
    private MainScreenPresentation presentation;

    // As usual, also other required objects can be injected.
    @Inject
    private Dao dao;

    private final FormFields fields = new FormFields();

    // For each button on the presentation that can do something, a UserAction is provided.
    private final UserAction buttonAction = new UserActionSupport8(this::onButtonPressed,
                                                                   new DefaultDisplayable("Press me"));

    private final UserAction actionDialogOk = new UserActionSupport8(this::onButtonDialogOkPressed,
                                                                     new DefaultDisplayable("Dialog with ok"));

    private final UserAction actionDialogCancelOk = new UserActionSupport8(this::onButtonDialogOkCancelPressed,
                                                                           new DefaultDisplayable("Dialog with ok/cancel"));

    private final UserAction actionPickFile = new UserActionSupport8(this::onButtonPickFilePressed,
                                                                     new DefaultDisplayable("Pick file"));

    private final UserAction actionPickDirectory = new UserActionSupport8(this::onButtonPickDirectoryPressed,
                                                                          new DefaultDisplayable("Pick directory"));

    // Then there can be a set of variables that represent the internal state of the control.
    private int status = 1;

    /*******************************************************************************************************************
     *
     * At {@link PostConstruct} time the control just peforms the binding to the presentation.
     *
     ******************************************************************************************************************/
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

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     * This method demonstrates the typical idiom for populating data:
     *
     * 1. A dao is called to provide raw data - let's say in form of collections;
     * 2. Objects in the collection are transformed into PresentationModels.
     * 3. The PresentationModels are then passed to the presentation.
     *
     ******************************************************************************************************************/
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

    /*******************************************************************************************************************
     *
     * Factory method for the PresentationModel of SimpleEntity instances.
     *
     * It aggregates a few extra roles into the PresentationModel that are used by the control, such as callbacks
     * for action associated to the context menu. Also a Displayable role is usually injected to control the rendering
     * of entities.
     *
     ******************************************************************************************************************/
    @Nonnull
    private PresentationModel pmFor (final @Nonnull SimpleEntity entity)
      {
        final Selectable selectable = () -> onSelected(entity);
        final UserAction action1 = new UserActionSupport8(() -> action1(entity), new DefaultDisplayable("Action 1"));
        final UserAction action2 = new UserActionSupport8(() -> action2(entity), new DefaultDisplayable("Action 2"));
        final UserAction action3 = new UserActionSupport8(() -> action3(entity), new DefaultDisplayable("Action 3"));
        return new DefaultPresentationModel(entity,
                                            new DefaultDisplayable("Item #" + entity.getName()),
                                            selectable,
                                            UserActionProvider.of(action1, action2, action3));
      }

    /*******************************************************************************************************************
     *
     * Factory method for the PresentationModel of SimpleDciEntity instances.
     *
     ******************************************************************************************************************/
    @Nonnull
    private PresentationModel pmFor (final @Nonnull SimpleDciEntity entity)
      {
        // FIXME: column names
        final Aggregate aggregate = AggregatePresentationModelBuilder.newInstance()
                                        .with("C1", new DefaultDisplayable(entity.getName()))
                                        .with("C2", new DefaultDisplayable("" + entity.getAttribute1()))
                                        .with("C3", new DefaultDisplayable("" + entity.getAttribute2()))
                                        .create();
        final Selectable selectable = () -> onSelected(entity);
        final UserAction action1 = new UserActionSupport8(() -> action1(entity), new DefaultDisplayable("Action 1"));
        final UserAction action2 = new UserActionSupport8(() -> action2(entity), new DefaultDisplayable("Action 2"));
        final UserAction action3 = new UserActionSupport8(() -> action3(entity), new DefaultDisplayable("Action 3"));
        // No explicit Displayable here, as the one inside SimpleDciEntity is used.
        return new DefaultPresentationModel(entity,
                                            aggregate,
                                            selectable,
                                            UserActionProvider.of(action1, action2, action3));
      }

    // Below simple business methonds, as per usual business.

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    private void onButtonPressed()
      {
        presentation.notify("Button pressed");
        status++;
        fields.textProperty.set(Integer.toString(status));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    private void onButtonDialogOkPressed()
      {
        presentation.notify(notificationWithFeedback()
                .withCaption("Notification")
                .withText("Now press the button")
                .withFeedback(feedback().withOnConfirm(() -> presentation.notify("Pressed ok"))));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    private void onButtonDialogOkCancelPressed()
      {
        presentation.notify(notificationWithFeedback()
                .withCaption("Notification")
                .withText("Now press the button")
                .withFeedback(feedback().withOnConfirm(() -> presentation.notify("Pressed ok"))
                                        .withOnCancel(() -> presentation.notify("Pressed cancel"))));
      }

    /*******************************************************************************************************************
     *
     * This method demonstrates how to pick a file name by using the proper UI dialog.
     *
     ******************************************************************************************************************/
    private void onButtonPickFilePressed()
      {
        final BoundProperty<Path> selectedFile = new BoundProperty<>(USER_HOME);
        presentation.pickFile(selectedFile,
            notificationWithFeedback()
                .withCaption("Pick a file")
                .withFeedback(feedback().withOnConfirm(() -> presentation.notify("Selected file: " + selectedFile.get()))
                                        .withOnCancel(() -> presentation.notify("Selection cancelled"))));
      }

    /*******************************************************************************************************************
     *
     * This method demonstrates how to pick a directory name by using the proper UI dialog.
     *
     ******************************************************************************************************************/
    private void onButtonPickDirectoryPressed()
      {
        final BoundProperty<Path> selectedFolder = new BoundProperty<>(USER_HOME);
        presentation.pickDirectory(selectedFolder,
            notificationWithFeedback()
                .withCaption("Pick a directory")
                .withFeedback(feedback().withOnConfirm(() -> presentation.notify("Selected folder: " + selectedFolder.get()))
                                        .withOnCancel(() -> presentation.notify("Selection cancelled"))));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    private void onSelected (final @Nonnull Object object)
      {
        presentation.notify("Selected " + object);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    private void action1 (final @Nonnull Object object)
      {
        presentation.notify("Action 1 on " + object);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    private void action2 (final @Nonnull Object object)
      {
        presentation.notify("Action 2 on " + object);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    private void action3 (final @Nonnull Object object)
      {
        presentation.notify("Action 3 on " + object);
      }
  }
