/*
 * #%L
 * *********************************************************************************************************************
 *
 * SteelBlue
 * http://steelblue.tidalwave.it - git clone git@bitbucket.org:tidalwave/steelblue-src.git
 * %%
 * Copyright (C) 2015 - 2021 Tidalwave s.a.s. (http://tidalwave.it)
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
package it.tidalwave.role.ui.javafx.impl.tree;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.beans.PropertyChangeListener;
import javafx.util.Callback;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.application.Platform;
import it.tidalwave.util.AsException;
import it.tidalwave.util.annotation.VisibleForTesting;
import it.tidalwave.role.SimpleComposite;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.role.ui.javafx.impl.CellBinder;
import it.tidalwave.role.ui.javafx.impl.common.DelegateSupport;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.role.SimpleComposite.*;
import static it.tidalwave.role.ui.Selectable._Selectable_;
import static it.tidalwave.role.ui.javafx.impl.Logging.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Slf4j
public class TreeViewBindings extends DelegateSupport
  {
//    @Nonnull
//    private final CellBinder cellBinder;
//
    @VisibleForTesting final Callback<TreeView<PresentationModel>, TreeCell<PresentationModel>> treeCellFactory;

    private final ObsoletePresentationModelDisposer presentationModelDisposer = new ObsoletePresentationModelDisposer();

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public TreeViewBindings (final @Nonnull Executor executor, final @Nonnull CellBinder cellBinder)
      {
        super(executor);
//        this.cellBinder = cellBinder;
        treeCellFactory = treeView -> new AsObjectTreeCell<>(cellBinder);
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @VisibleForTesting final ChangeListener<TreeItem<PresentationModel>> treeItemChangeListener = (ov, oldItem, item) ->
      {
        executor.execute(() ->
          {
            try
              {
                item.getValue().as(_Selectable_).select();
              }
            catch (AsException e)
              {
                TreeViewBindings.log.debug("No Selectable role for {}", item); // ok, do nothing
              }
          });
      };

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    public void bind (final @Nonnull TreeView<PresentationModel> treeView,
                      final @Nonnull PresentationModel pm,
                      final @Nonnull Optional<Runnable> callback)
      {
        assertIsFxApplicationThread();
        log.debug("bind({}, {}, {})", treeView, pm, callback);

        final ObjectProperty<TreeItem<PresentationModel>> rootProperty = treeView.rootProperty();
        rootProperty.removeListener(presentationModelDisposer);
        rootProperty.addListener(presentationModelDisposer);
        rootProperty.set(createTreeItem(pm, 0));
        callback.ifPresent(Runnable::run);

        treeView.setCellFactory(treeCellFactory);

        final ReadOnlyObjectProperty<TreeItem<PresentationModel>> pmProperty =
                treeView.getSelectionModel().selectedItemProperty();
        pmProperty.removeListener(treeItemChangeListener);
        pmProperty.addListener(treeItemChangeListener);
     }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private TreeItem<PresentationModel> createTreeItem (final @Nonnull PresentationModel pm, final int recursion)
      {
        assertIsFxApplicationThread();
        final TreeItem<PresentationModel> item = new TreeItem<>(pm);

        final PropertyChangeListener recreateChildrenOnUpdateListener = event ->
          {
            Platform.runLater(() ->
              {
                log.debug("On recreateChildrenOnUpdateListener");
                item.getChildren().clear(); // FIXME: should update it incrementally
                createChildren(item, pm, recursion + 1);
                item.setExpanded(true);
              });
          };

        pm.addPropertyChangeListener(PresentationModel.PROPERTY_CHILDREN, recreateChildrenOnUpdateListener);
        // FIXME: only if already expanded, otherwise defer the call when expanded
        createChildren(item, pm, recursion + 1);

        return item;
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    // FIXME: add on demand, upon node expansion
    private void createChildren (final @Nonnull TreeItem<PresentationModel> parentItem,
                                 final @Nonnull PresentationModel pm,
                                 final int recursion)
      {
        assertIsFxApplicationThread();
        final String prefix = INDENT.substring(0, recursion * 8);
        final SimpleComposite<PresentationModel> composite = pm.as(_SimpleComposite_);
        logObject(prefix, composite);

        // FIXME: results() in bg thread
        final List<? extends PresentationModel> childPMs = composite.findChildren().results();
        logObjects(prefix, childPMs);

        for (final PresentationModel childPm : childPMs)
          {
            parentItem.getChildren().add(createTreeItem(childPm, recursion));
          }
      }
  }
