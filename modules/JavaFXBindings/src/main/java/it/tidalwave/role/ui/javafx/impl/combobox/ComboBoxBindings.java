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
package it.tidalwave.role.ui.javafx.impl.combobox;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.concurrent.Executor;
import javafx.util.Callback;
import javafx.collections.ObservableList;
import javafx.beans.value.ChangeListener;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.application.Platform;
import it.tidalwave.util.AsException;
import it.tidalwave.util.NotFoundException;
import it.tidalwave.role.SimpleComposite;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.role.ui.UserActionProvider;
import it.tidalwave.role.ui.javafx.impl.common.DelegateSupport;
import it.tidalwave.role.ui.javafx.impl.common.AsObjectListCell;
import it.tidalwave.role.ui.javafx.impl.common.ChangeListenerSelectableAdapter;
import lombok.extern.slf4j.Slf4j;
import static javafx.collections.FXCollections.observableArrayList;
import static javafx.scene.input.KeyCode.*;
import static it.tidalwave.role.SimpleComposite.SimpleComposite;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class ComboBoxBindings extends DelegateSupport
  {
    private final Callback<ListView<PresentationModel>, ListCell<PresentationModel>> cellFactory =
            (comboBox) -> new AsObjectListCell<>();

    private final ChangeListener<PresentationModel> changeListener = new ChangeListenerSelectableAdapter(executor);

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    private final EventHandler<ActionEvent> eventHandler = event ->
      { 
        try
          {
            final ComboBox<PresentationModel> comboBox = (ComboBox<PresentationModel>)event.getSource();
            final PresentationModel selectedPm = comboBox.getSelectionModel().getSelectedItem();
            selectedPm.as(UserActionProvider.class).getDefaultAction().actionPerformed();
          }
        catch (AsException | NotFoundException e)
          {
            // ok no action
          }
      };
        
    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public ComboBoxBindings (final @Nonnull Executor executor)
      {
        super(executor);
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public void bind (final @Nonnull ComboBox<PresentationModel> comboBox,
                      final @Nonnull PresentationModel pm,
                      final @Nonnull Runnable callback)
      {
        comboBox.setCellFactory(cellFactory);
        comboBox.setButtonCell(new AsObjectListCell<>());
        comboBox.setOnAction(eventHandler);
        
        // FIXME: WEAK LISTENERS

        // FIXME: this won't work with any external navigation system, such as CEC menus
        // TODO: try by having CEC selection emulating RETURN and optionally accepting RETURN here
        comboBox.setOnKeyPressed((KeyEvent event) ->
          {
            if (Arrays.asList(SPACE, ENTER).contains(event.getCode()))
              {
                comboBox.show();
              }
          });

        final ReadOnlyObjectProperty<PresentationModel> pmProperty = comboBox.getSelectionModel().selectedItemProperty();
        pmProperty.removeListener(changeListener);
        executor.execute(() -> // TODO: use FXWorker
          {
            final SimpleComposite<PresentationModel> composite = pm.as(SimpleComposite);
            final ObservableList<PresentationModel> items = observableArrayList(composite.findChildren().results());
            Platform.runLater(() ->
              {
                comboBox.setItems(items);
                
                if (!items.isEmpty())
                  {
                    comboBox.getSelectionModel().select(items.get(0));
                  }
                
                pmProperty.addListener(changeListener);
                callback.run();
              });
          });
      }
  }
