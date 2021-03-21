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
import java.beans.PropertyChangeListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.application.Platform;
import it.tidalwave.util.annotation.VisibleForTesting;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.role.ui.javafx.impl.common.CellBinder;
import it.tidalwave.role.ui.javafx.impl.common.ChangeListenerSelectableAdapter;
import it.tidalwave.role.ui.javafx.impl.common.DelegateSupport;
import it.tidalwave.role.ui.javafx.impl.common.PresentationModelObservable;
import it.tidalwave.role.ui.javafx.impl.tree.ObsoletePresentationModelDisposer;
import javafx.util.Callback;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.role.ui.javafx.impl.common.JavaFXWorker.childrenPm;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Slf4j
public class TreeTableViewBindings extends DelegateSupport
  {
    private final ObsoletePresentationModelDisposer presentationModelDisposer = new ObsoletePresentationModelDisposer();

    private final Callback<TreeTableColumn<PresentationModel, PresentationModel>,
            TreeTableCell<PresentationModel, PresentationModel>> cellFactory;

    @VisibleForTesting final ChangeListenerSelectableAdapter changeListener =
            new ChangeListenerSelectableAdapter(executor);

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

        final ObjectProperty<TreeItem<PresentationModel>> rootProperty = treeTableView.rootProperty();
        rootProperty.removeListener(presentationModelDisposer);
        rootProperty.addListener(presentationModelDisposer);
        rootProperty.set(createTreeItem(pm, 0));

        final ObservableList rawColumns = treeTableView.getColumns(); // FIXME cast
        final ObservableList<TreeTableColumn<PresentationModel, PresentationModel>> columns =
                (ObservableList<TreeTableColumn<PresentationModel, PresentationModel>>)rawColumns;

        for (final TreeTableColumn<PresentationModel, PresentationModel> column : columns)
          {
            column.setCellValueFactory(PresentationModelObservable::of);
            column.setCellFactory(cellFactory);
          }

        final ReadOnlyObjectProperty<TreeItem<PresentationModel>> selectedItemProperty =
                treeTableView.getSelectionModel().selectedItemProperty();
        selectedItemProperty.removeListener(changeListener.asTreeItemChangeListener());
        selectedItemProperty.addListener(changeListener.asTreeItemChangeListener());
        callback.ifPresent(Runnable::run); // FIXME: thread?
     }

    /*******************************************************************************************************************
     *
     * Creates a single {@link TreeItem} for the given the {@link PresentationModel}. When the {@code PresentationModel}
     * fires the {@link PresentationModel#PROPERTY_CHILDREN} property change event, children are recreated.
     *
     * @param   pm        the {@code PresentationModel}
     * @param   depth     the depth level (used only for logging)
     * @return            the
     *
     ******************************************************************************************************************/
    @Nonnull
    private TreeItem<PresentationModel> createTreeItem (@Nonnull final PresentationModel pm, final int depth)
      {
        final TreeItem<PresentationModel> item = new TreeItem<>(pm);

        final PropertyChangeListener recreateChildrenOnUpdateListener = __ ->
          Platform.runLater(() ->
            {
              item.getChildren().clear();
              createChildren(item, depth + 1);
              item.setExpanded(true);
            });

        pm.addPropertyChangeListener(PresentationModel.PROPERTY_CHILDREN, recreateChildrenOnUpdateListener);
        createChildren(item, depth + 1); // FIXME: only if already expanded, otherwise defer the call when
        // expanded

        return item;
      }

    /*******************************************************************************************************************
     *
     * Creates the children for a {@link TreeItem}.
     *
     * @param   parentItem  the {@code TreeItem}
     * @param   depth       the depth level (used only for logging)
     *
     ******************************************************************************************************************/
    // FIXME: add on demand, upon node expansion
    private void createChildren (@Nonnull final TreeItem<PresentationModel> parentItem, final int depth)
      {
        final PresentationModel parentPm = parentItem.getValue();
        final ObservableList<TreeItem<PresentationModel>> children = parentItem.getChildren();
        childrenPm(parentPm, depth).forEach(childPm -> children.add(createTreeItem(childPm, depth)));
      }
  }
