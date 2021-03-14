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
package it.tidalwave.role.ui.javafx.impl.tableview;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.concurrent.Executor;
import javafx.util.Callback;
import javafx.collections.ObservableList;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.application.Platform;
import it.tidalwave.util.AsException;
import it.tidalwave.util.VisibleForTesting;
import it.tidalwave.role.SimpleComposite;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.role.ui.javafx.impl.CellBinder;
import it.tidalwave.role.ui.javafx.impl.common.DelegateSupport;
import lombok.extern.slf4j.Slf4j;
import static javafx.collections.FXCollections.observableArrayList;
import static it.tidalwave.role.ui.javafx.impl.Logging.*;
import static it.tidalwave.role.ui.Selectable.Selectable;
import static it.tidalwave.role.SimpleComposite.SimpleComposite;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class TableViewBindings extends DelegateSupport
  {
    @Nonnull
    private final CellBinder cellBinder;

    private Callback<TableColumn<PresentationModel, PresentationModel>,
                     TableCell<PresentationModel, PresentationModel>> cellFactory;

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @VisibleForTesting final ChangeListener<PresentationModel> changeListener = (ov, oldItem, item) ->
      {
        if (item == null)
          {
            log.warn("NULL ITEM in listener callback: old value: {}", oldItem);
//                Thread.dumpStack();
            return;
          }

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
      };

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public TableViewBindings (final @Nonnull Executor executor, final @Nonnull CellBinder cellBinder)
      {
        super(executor);
        this.cellBinder = cellBinder;
        cellFactory = tableView -> new AsObjectTableCell<>(cellBinder);
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    public void bind (final @Nonnull TableView<PresentationModel> tableView,
                      final @Nonnull PresentationModel pm,
                      final @Nonnull Optional<Runnable> callback)
      {
        assertIsFxApplicationThread();
        log.debug("bind({}, {}, {})", tableView, pm, callback);

        final ReadOnlyObjectProperty<PresentationModel> pmProperty = tableView.getSelectionModel().selectedItemProperty();
        pmProperty.removeListener(changeListener);

        executor.execute(() ->
          {
            final SimpleComposite<PresentationModel> composite = pm.as(SimpleComposite);
            final ObservableList<PresentationModel> items = observableArrayList(composite.findChildren().results());
            log.debug(">>>> {}", composite);
            logObjects("", items);

            Platform.runLater(() ->
              {
                tableView.setItems(items);
                pmProperty.addListener(changeListener);

                final TableAggregateAdapter tableAggregateAdapter = new TableAggregateAdapter();
                final ObservableList rawColumns = tableView.getColumns(); // FIXME
                ((ObservableList<TableColumn<PresentationModel, PresentationModel>>)rawColumns).stream().forEach(column ->
                  {
                    column.setCellValueFactory(tableAggregateAdapter);
                    column.setCellFactory(cellFactory);
                  });

                callback.ifPresent(Runnable::run);
              });
          });
      }
  }
