/*
 * #%L
 * *********************************************************************************************************************
 *
 * SteelBlue
 * http://steelblue.tidalwave.it - hg clone https://bitbucket.org/tidalwave/steelblue-src
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
package it.tidalwave.role.ui.javafx.impl.list;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.concurrent.Executor;
import javafx.util.Callback;
import javafx.collections.ObservableList;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyEvent;
import javafx.application.Platform;
import com.google.common.annotations.VisibleForTesting;
import it.tidalwave.util.NotFoundException;
import it.tidalwave.util.AsException;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.role.ui.UserActionProvider;
import it.tidalwave.role.ui.javafx.impl.DelegateSupport;
import lombok.extern.slf4j.Slf4j;
import static javafx.collections.FXCollections.observableArrayList;
import static javafx.scene.input.KeyCode.*;
import static it.tidalwave.role.SimpleComposite.SimpleComposite;
import static it.tidalwave.role.ui.Selectable.Selectable;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class ListViewBindings extends DelegateSupport
  {
    private final Callback<ListView<PresentationModel>, ListCell<PresentationModel>> cellFactory = 
            (listView) -> new AsObjectListCell<>();
    
    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public ListViewBindings (final @Nonnull Executor executor)
      {
        super(executor);
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @VisibleForTesting final ChangeListener<PresentationModel> changeListener =
            (final @Nonnull ObservableValue<? extends PresentationModel> ov, 
             final @Nonnull PresentationModel oldItem, 
             final @CheckForNull PresentationModel item) -> 
      {
        if (item != null) // no selection
          {
            executor.execute(() -> 
              {
                try
                  {
                    item.as(Selectable).select();
                  }
                catch (AsException e)
                  {
                    log.debug("No Selectable role for {}", item); // ok, do nothing
                  }
              });
          }
      };
    
    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public void bind (final @Nonnull ListView<PresentationModel> listView, 
                      final @Nonnull PresentationModel pm,
                      final @Nonnull Runnable callback)
      {
        listView.setCellFactory(cellFactory);
    
        // FIXME: WEAK LISTENERS

        // FIXME: this won't work with any external navigation system, such as CEC menus
        // TODO: try by having CEC selection emulating RETURN and optionally accepting RETURN here
        listView.setOnKeyPressed((KeyEvent event) -> 
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
                        selectedPm.as(UserActionProvider.class).getDefaultAction().actionPerformed();
                      }
                    catch (AsException | NotFoundException e)
                      {
                        // ok no action  
                      }
                  });
              }
          });
        
        
        executor.execute(() -> // TODO: use FXWorker
          {
            final ObservableList items = observableArrayList(pm.as(SimpleComposite).findChildren().results());
            Platform.runLater(() ->
              {
                listView.setItems(items);
                final ReadOnlyObjectProperty<PresentationModel> pmProperty = listView.getSelectionModel().selectedItemProperty();
                pmProperty.removeListener(changeListener);
                pmProperty.addListener(changeListener);
                callback.run();
              });
          });
      }
  }
