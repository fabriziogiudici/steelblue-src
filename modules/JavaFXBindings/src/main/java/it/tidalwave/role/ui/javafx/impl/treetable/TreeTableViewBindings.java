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
 *
 *
 * *********************************************************************************************************************
 * #L%
 */
package it.tidalwave.role.ui.javafx.impl.treetable;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.concurrent.Executor;
import javafx.util.Callback;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.role.ui.javafx.impl.common.CellBinder;
import it.tidalwave.role.ui.javafx.impl.common.TreeItemDelegateSupport;
import it.tidalwave.role.ui.javafx.impl.common.PresentationModelObservable;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Slf4j
public class TreeTableViewBindings extends TreeItemDelegateSupport
  {
    private final Callback<TreeTableColumn<PresentationModel, PresentationModel>,
            TreeTableCell<PresentationModel, PresentationModel>> cellFactory;

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public TreeTableViewBindings (@Nonnull final Executor executor, @Nonnull final CellBinder cellBinder)
      {
        super(executor);
        cellFactory = __ -> AsObjectTreeTableCell.of(cellBinder);
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    public void bind (@Nonnull final TreeTableView<PresentationModel> treeTableView,
                      @Nonnull final PresentationModel pm,
                      @Nonnull final Optional<Runnable> callback)
      {
        assertIsFxApplicationThread();
        log.debug("bind({}, {}, {})", treeTableView, pm, callback);

        setRootProperty(pm, treeTableView.rootProperty());
        setCellFactory(treeTableView);
        treeTableView.setShowRoot(shouldShowRoot(pm));
        bindSelectionListener(treeTableView.getSelectionModel().selectedItemProperty());
        callback.ifPresent(Runnable::run); // FIXME: thread?
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    private void setCellFactory (@Nonnull final TreeTableView<PresentationModel> treeTableView)
      {
        final ObservableList rawColumns = treeTableView.getColumns(); // FIXME cast
        final ObservableList<TreeTableColumn<PresentationModel, PresentationModel>> columns =
                (ObservableList<TreeTableColumn<PresentationModel, PresentationModel>>)rawColumns;

        for (final TreeTableColumn<PresentationModel, PresentationModel> column : columns)
          {
            column.setCellValueFactory(PresentationModelObservable::of);
            column.setCellFactory(cellFactory);
          }
      }
  }
