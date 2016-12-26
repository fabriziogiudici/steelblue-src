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
package it.tidalwave.role.ui.javafx.example.large.impl.mainscreen;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeView;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.role.ui.UserAction;
import it.tidalwave.role.ui.javafx.JavaFXBinder;
import javafx.scene.control.ComboBox;
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
    private ListView<PresentationModel> lvListView;

    @FXML
    private ComboBox<PresentationModel> cbComboBox;

    @FXML
    private TableView<PresentationModel> tvTableView;

    @FXML
    private TreeView<PresentationModel> tvTreeView;

    @FXML
    private TreeTableView<PresentationModel> ttvTreeTableView;

    @Inject
    private JavaFXBinder binder;

    @Override
    public void bind (final @Nonnull UserAction action,
                      final @Nonnull PresentationModel listPm)
      {
        binder.bind(btButton, action);
        binder.bind(lvListView, listPm, () -> log.info("Changed selection on lvListView"));
        binder.bind(cbComboBox, listPm, () -> log.info("Changed selection on cbComboBox"));
      }

    @Override
    public void showUp()
      {
      }
  }
