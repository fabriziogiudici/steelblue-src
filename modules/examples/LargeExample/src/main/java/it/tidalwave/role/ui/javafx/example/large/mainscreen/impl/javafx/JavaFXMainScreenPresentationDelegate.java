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
package it.tidalwave.role.ui.javafx.example.large.mainscreen.impl.javafx;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.nio.file.Path;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeView;
import it.tidalwave.util.ui.UserNotificationWithFeedback;
import it.tidalwave.role.ui.BoundProperty;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.role.ui.UserAction;
import it.tidalwave.role.ui.javafx.JavaFXBinder;
import it.tidalwave.role.ui.javafx.example.large.mainscreen.MainScreenPresentation;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: $
 *
 **********************************************************************************************************************/
@Slf4j
public class JavaFXMainScreenPresentationDelegate implements MainScreenPresentation
  {
    @FXML
    private Button btButton;

    @FXML
    private Button btDialogOk;

    @FXML
    private Button btDialogOkCancel;

    @FXML
    private Button btPickFile;

    @FXML
    private Button btPickDirectory;

    @FXML
    private TextField tfTextField;

    @FXML
    private ListView<PresentationModel> lvListView;

    @FXML
    private ComboBox<PresentationModel> cbComboBox;

    @FXML
    private TableView<PresentationModel> tvTableView;

    @FXML
    private TreeView<PresentationModel> tvTreeView;

    @FXML
    private TreeTableView<PresentationModel> ttvTreeTableView;

    @FXML
    private TextArea taLog;

    @Inject
    private JavaFXBinder binder;

    @Override
    public void bind (final @Nonnull UserAction action,
                      final @Nonnull UserAction actionDialogOk,
                      final @Nonnull UserAction actionDialogCancelOk,
                      final @Nonnull UserAction actionPickFile,
                      final @Nonnull UserAction actionPickDirectory,
                      final @Nonnull FormFields fields)
      {
        binder.bind(btButton, action);
        binder.bind(btDialogOk, actionDialogOk);
        binder.bind(btDialogOkCancel, actionDialogCancelOk);
        binder.bind(btPickFile, actionPickFile);
        binder.bind(btPickDirectory, actionPickDirectory);
        binder.bindBidirectionally(tfTextField, fields.textProperty, fields.booleanProperty);
      }

    @Override
    public void showUp()
      {
      }

    @Override
    public void populate (final @Nonnull PresentationModel listPm,final @Nonnull PresentationModel arrayPm)
      {
        binder.bind(lvListView, listPm, () -> log.info("Finished setup of lvListView"));
        binder.bind(cbComboBox, listPm, () -> log.info("Finished setup of cbComboBox"));
        binder.bind(tvTableView, arrayPm, () -> log.info("Finished setup of tvTableView"));
//        binder.bind(tvTreeView, arrayPm, () -> log.info("Finished setup of tvTreeView"));
        binder.bind(ttvTreeTableView, arrayPm, () -> log.info("Finished setup of ttvTreeTableView"));
      }

    @Override
    public void notify (final @Nonnull UserNotificationWithFeedback notification)
      {
        binder.showInModalDialog(new Label(notification.getText()), notification);
      }

    @Override
    public void notify (final @Nonnull String message)
      {
        taLog.appendText(message + "\n");
      }

    @Override
    public void pickFile (final @Nonnull BoundProperty<Path> selectedFile,
                          final @Nonnull UserNotificationWithFeedback notification)
      {
        binder.openFileChooserFor(notification, selectedFile);
      }

    @Override
    public void pickDirectory (final @Nonnull BoundProperty<Path> selectedFolder,
                               final @Nonnull UserNotificationWithFeedback notification)
      {
        binder.openDirectoryChooserFor(notification, selectedFolder);
      }
  }
