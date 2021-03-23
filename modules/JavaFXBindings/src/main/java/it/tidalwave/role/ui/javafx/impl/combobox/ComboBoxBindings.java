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
package it.tidalwave.role.ui.javafx.impl.combobox;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import javafx.collections.ObservableList;
import javafx.util.Callback;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.beans.value.ChangeListener;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ComboBox;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.role.ui.UserAction;
import it.tidalwave.role.ui.UserActionProvider;
import it.tidalwave.role.ui.javafx.impl.list.AsObjectListCell;
import it.tidalwave.role.ui.javafx.impl.common.CellBinder;
import it.tidalwave.role.ui.javafx.impl.common.ChangeListenerSelectableAdapter;
import it.tidalwave.role.ui.javafx.impl.common.DelegateSupport;
import it.tidalwave.role.ui.javafx.impl.common.JavaFXWorker;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.role.ui.javafx.impl.common.JavaFXWorker.childrenPm;
import static javafx.scene.input.KeyCode.*;
import static it.tidalwave.role.ui.UserActionProvider._UserActionProvider_;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Slf4j
public class ComboBoxBindings extends DelegateSupport
  {
    @Nonnull
    private final CellBinder cellBinder;

    private final Callback<ListView<PresentationModel>, ListCell<PresentationModel>> cellFactory;

    private final ChangeListener<PresentationModel> changeListener = new ChangeListenerSelectableAdapter(executor);

    /*******************************************************************************************************************
     *
     * Event handler that executes the default action bound to the given combobox item.
     *
     ******************************************************************************************************************/
    private final EventHandler<ActionEvent> eventHandler = event ->
      {
        final ComboBox<PresentationModel> comboBox = (ComboBox<PresentationModel>)event.getSource();
        final PresentationModel selectedPm = comboBox.getSelectionModel().getSelectedItem();
        selectedPm.maybeAs(_UserActionProvider_)
                  .flatMap(UserActionProvider::getOptionalDefaultAction)
                  .ifPresent(UserAction::actionPerformed);
      };

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public ComboBoxBindings (@Nonnull final Executor executor, @Nonnull final CellBinder cellBinder)
      {
        super(executor);
        this.cellBinder = cellBinder;
        cellFactory = comboBox -> new AsObjectListCell<>(cellBinder);
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    public void bind (@Nonnull final ComboBox<PresentationModel> comboBox,
                      @Nonnull final PresentationModel pm,
                      @Nonnull final Optional<Runnable> callback)
      {
        comboBox.setCellFactory(cellFactory);
        comboBox.setButtonCell(new AsObjectListCell<>(cellBinder));
        comboBox.setOnAction(eventHandler);

        // FIXME: WEAK LISTENERS

        // FIXME: this won't work with any external navigation system, such as CEC menus
        // TODO: try by having CEC selection emulating RETURN and optionally accepting RETURN here
        comboBox.setOnKeyPressed(event ->
          {
            if (List.of(SPACE, ENTER).contains(event.getCode()))
              {
                comboBox.show();
              }
          });

        final ReadOnlyObjectProperty<PresentationModel> selectedProperty = comboBox.getSelectionModel().selectedItemProperty();
        selectedProperty.removeListener(changeListener);
        JavaFXWorker.run(executor,
                         () -> childrenPm(pm),
                         items -> finalize(comboBox, items, selectedProperty, callback));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    private void finalize (@Nonnull final ComboBox<PresentationModel> comboBox,
                           @Nonnull final ObservableList<PresentationModel> items,
                           @Nonnull final ReadOnlyObjectProperty<PresentationModel> selectedProperty,
                           @Nonnull final Optional<Runnable> callback)
      {
        comboBox.setItems(items);

        if (!items.isEmpty())
          {
            comboBox.getSelectionModel().select(items.get(0));
          }

        selectedProperty.addListener(changeListener);
        callback.ifPresent(Runnable::run);
      }
  }
