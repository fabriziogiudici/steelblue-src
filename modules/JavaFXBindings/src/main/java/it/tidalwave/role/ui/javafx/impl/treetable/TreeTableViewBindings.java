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
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.application.Platform;
import it.tidalwave.util.AsException;
import it.tidalwave.util.annotation.VisibleForTesting;
import it.tidalwave.role.SimpleComposite;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.role.ui.javafx.impl.CellBinder;
import it.tidalwave.role.ui.javafx.impl.common.DelegateSupport;
import it.tidalwave.role.ui.javafx.impl.tree.ObsoletePresentationModelDisposer;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.role.SimpleComposite.*;
import static it.tidalwave.role.ui.Selectable._Selectable_;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Slf4j
public class TreeTableViewBindings extends DelegateSupport
  {
    @Nonnull
    private final CellBinder cellBinder;

    private final ObsoletePresentationModelDisposer presentationModelDisposer = new ObsoletePresentationModelDisposer();

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public TreeTableViewBindings (@Nonnull final Executor executor, @Nonnull final CellBinder cellBinder)
      {
        super(executor);
        this.cellBinder = cellBinder;
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @VisibleForTesting final ChangeListener<TreeItem<PresentationModel>> treeItemChangeListener = (ov, oldItem, item) ->
        executor.execute(() ->
          {
            try
              {
                item.getValue().as(_Selectable_).select();
              }
            catch (AsException e)
              {
                log.debug("No Selectable role for {}", item); // ok, do nothing
              }
          });

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
        rootProperty.set(createTreeItem(pm));

        final ObservableList rawColumns = treeTableView.getColumns(); // FIXME
        final ObservableList<TreeTableColumn<PresentationModel, PresentationModel>> columns =
                (ObservableList<TreeTableColumn<PresentationModel, PresentationModel>>)rawColumns;

        for (final TreeTableColumn<PresentationModel, PresentationModel> column : columns)
          {
            column.setCellValueFactory(new TreeTableAggregateAdapter());
            column.setCellFactory(c -> new AsObjectTreeTableCell(cellBinder));
          }

        final ReadOnlyObjectProperty<TreeItem<PresentationModel>> selectedItemProperty =
                treeTableView.getSelectionModel().selectedItemProperty();
        selectedItemProperty.removeListener(treeItemChangeListener);
        selectedItemProperty.addListener(treeItemChangeListener);
        callback.ifPresent(Runnable::run);
     }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private TreeItem<PresentationModel> createTreeItem (@Nonnull final PresentationModel pm)
      {
        final TreeItem<PresentationModel> item = new TreeItem<>(pm);

        final PropertyChangeListener recreateChildrenOnUpdateListener = __ ->
          Platform.runLater(() ->
            {
              item.getChildren().clear(); // FIXME: should update it incrementally
              createChildren(item, pm);
              item.setExpanded(true);
            });

        pm.addPropertyChangeListener(PresentationModel.PROPERTY_CHILDREN, recreateChildrenOnUpdateListener);
        createChildren(item, pm); // FIXME: only if already expanded, otherwise defer the call when expanded

        return item;
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    // FIXME: add on demand, upon node expansion
    private void createChildren (@Nonnull final TreeItem<PresentationModel> parentItem,
                                 @Nonnull final PresentationModel pm)
      {
        try
          {
            final SimpleComposite<PresentationModel> composite = pm.as(_SimpleComposite_);
            final ObservableList<TreeItem<PresentationModel>> children = parentItem.getChildren();
            composite.findChildren().results().forEach(childPm -> children.add(createTreeItem(childPm)));
          }
        catch (AsException e)
          {
            // ok, no Composite
          }
      }
  }
