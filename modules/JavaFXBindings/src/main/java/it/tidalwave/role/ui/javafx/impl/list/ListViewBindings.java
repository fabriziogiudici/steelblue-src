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
package it.tidalwave.role.ui.javafx.impl.list;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.Executor;
import it.tidalwave.role.ui.javafx.impl.Logging;
import javafx.util.Callback;
import javafx.collections.ObservableList;
import javafx.beans.value.ChangeListener;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.application.Platform;
import it.tidalwave.util.NotFoundException;
import it.tidalwave.role.SimpleComposite;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.role.ui.javafx.impl.CellBinder;
import it.tidalwave.role.ui.javafx.impl.DefaultCellBinder;
import it.tidalwave.role.ui.javafx.impl.RoleBag;
import it.tidalwave.role.ui.javafx.impl.common.AsObjectListCell;
import it.tidalwave.role.ui.javafx.impl.common.ChangeListenerSelectableAdapter;
import it.tidalwave.role.ui.javafx.impl.common.DelegateSupport;
import lombok.extern.slf4j.Slf4j;
import static javafx.collections.FXCollections.observableArrayList;
import static javafx.scene.input.KeyCode.*;
import static it.tidalwave.role.SimpleComposite.SimpleComposite;
import static it.tidalwave.role.ui.UserActionProvider.UserActionProvider;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Slf4j
public class ListViewBindings extends DelegateSupport
  {
//    private final CellBinder cellBinder;
//
    private final Callback<ListView<PresentationModel>, ListCell<PresentationModel>> cellFactory;

    private final ChangeListener<PresentationModel> changeListener = new ChangeListenerSelectableAdapter(executor);

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public ListViewBindings (final @Nonnull Executor executor, final @Nonnull CellBinder cellBinder)
      {
        super(executor);
        cellFactory = listView -> new AsObjectListCell<>(cellBinder);
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    public void bind (final @Nonnull ListView<PresentationModel> listView,
                      final @Nonnull PresentationModel pm,
                      final @Nonnull Optional<Runnable> callback)
      {
        listView.setCellFactory(cellFactory);

        // FIXME: WEAK LISTENERS

        // FIXME: this won't work with any external navigation system, such as CEC menus
        // TODO: try by having CEC selection emulating RETURN and optionally accepting RETURN here
        listView.setOnKeyPressed(event ->
          {
            if (Arrays.asList(SPACE, ENTER).contains(event.getCode()))
              {
                final PresentationModel selectedPm = listView.getSelectionModel().getSelectedItem();
                // TODO: must call the default action - but should we look up it again?
                // Otherwise emulate mouse double click on the cell
                log.debug("ListView onKeyPressed: {}", selectedPm);

                executor.execute(() ->
                  {
                    try
                      {
                        // FIXME: it would be nicer to retrieve the cell and its associated RoleBag?
                        final RoleBag roleMap = new RoleBag(selectedPm, Arrays.asList(UserActionProvider));
                        DefaultCellBinder.findDefaultUserAction(roleMap).actionPerformed();
                      }
                    catch (NotFoundException e)
                      {
                        // ok no action
                      }
                  });
              }
          });

        final ReadOnlyObjectProperty<PresentationModel> pmProperty = listView.getSelectionModel().selectedItemProperty();
        pmProperty.removeListener(changeListener);
        listView.setItems(observableArrayList()); // quick clear in case of long operations FIXME doesn't work
        executor.execute(() -> // TODO: use FXWorker
          {
            final SimpleComposite<PresentationModel> composite = pm.as(SimpleComposite);
            final ObservableList<PresentationModel> items = observableArrayList(composite.findChildren().results());
            Logging.logObjects("", items);

            Platform.runLater(() ->
              {
                listView.setItems(items);
                pmProperty.addListener(changeListener);
                callback.ifPresent(Runnable::run);
              });
          });
      }
  }
