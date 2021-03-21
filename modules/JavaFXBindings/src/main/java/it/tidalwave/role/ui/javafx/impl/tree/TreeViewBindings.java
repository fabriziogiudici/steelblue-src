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
import java.util.Optional;
import java.util.concurrent.Executor;
import java.beans.PropertyChangeListener;
import it.tidalwave.role.ui.javafx.impl.common.ChangeListenerSelectableAdapter;
import javafx.util.Callback;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.application.Platform;
import it.tidalwave.util.annotation.VisibleForTesting;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.role.ui.javafx.impl.common.CellBinder;
import it.tidalwave.role.ui.javafx.impl.common.DelegateSupport;
import it.tidalwave.role.ui.javafx.impl.common.JavaFXWorker;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.role.ui.javafx.impl.common.JavaFXWorker.childrenPm;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Slf4j
public class TreeViewBindings extends DelegateSupport
  {
    @VisibleForTesting final Callback<TreeView<PresentationModel>, TreeCell<PresentationModel>> treeCellFactory;

    private final ObsoletePresentationModelDisposer presentationModelDisposer = new ObsoletePresentationModelDisposer();

    @VisibleForTesting final ChangeListenerSelectableAdapter changeListener = new ChangeListenerSelectableAdapter(executor);

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public TreeViewBindings (@Nonnull final Executor executor, @Nonnull final CellBinder cellBinder)
      {
        super(executor);
        treeCellFactory = treeView -> new AsObjectTreeCell<>(cellBinder);
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    public void bind (@Nonnull final TreeView<PresentationModel> treeView,
                      @Nonnull final PresentationModel pm,
                      @Nonnull final Optional<Runnable> callback)
      {
        assertIsFxApplicationThread();
        log.debug("bind({}, {}, {})", treeView, pm, callback);

        final ObjectProperty<TreeItem<PresentationModel>> rootProperty = treeView.rootProperty();
        rootProperty.removeListener(presentationModelDisposer);
        rootProperty.addListener(presentationModelDisposer);
        rootProperty.set(createTreeItem(pm, 0));
        callback.ifPresent(Runnable::run);

        treeView.setCellFactory(treeCellFactory);

        final ReadOnlyObjectProperty<TreeItem<PresentationModel>> selectionProperty =
                treeView.getSelectionModel().selectedItemProperty();
        selectionProperty.removeListener(changeListener.asTreeItemChangeListener());
        selectionProperty.addListener(changeListener.asTreeItemChangeListener());
     }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private TreeItem<PresentationModel> createTreeItem (@Nonnull final PresentationModel pm, final int recursion)
      {
        assertIsFxApplicationThread();
        final TreeItem<PresentationModel> item = new TreeItem<>(pm);

        final PropertyChangeListener recreateChildrenOnUpdateListener = __ ->
          Platform.runLater(() ->
            {
              log.debug("On recreateChildrenOnUpdateListener");
              item.getChildren().clear(); // FIXME: should update it incrementally
              createChildren(item, pm, recursion + 1);
              item.setExpanded(true);
            });

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
    private void createChildren (@Nonnull final TreeItem<PresentationModel> parentItem,
                                 @Nonnull final PresentationModel pm,
                                 final int recursion)
      {
        assertIsFxApplicationThread();
        JavaFXWorker.run(executor,
                         () -> JavaFXWorker.childrenPm(pm, recursion),
                         items -> items.forEach(item -> parentItem.getChildren().add(createTreeItem(item, recursion))));
      }
  }
