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
package it.tidalwave.role.ui.javafx.impl.treetable;

import javax.annotation.Nonnull;
import it.tidalwave.role.ui.Displayable;
import javafx.util.Callback;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;
import javafx.scene.control.TreeTableColumn;
import it.tidalwave.util.AsException;
import it.tidalwave.role.Aggregate;
import it.tidalwave.role.ui.PresentationModel;

/***********************************************************************************************************************
 *
 * @author  fritz
 *
 **********************************************************************************************************************/
class TreeTableAggregateAdapter implements Callback<TreeTableColumn.CellDataFeatures<PresentationModel, PresentationModel>, ObservableValue<PresentationModel>>
  {
    private static final PresentationModel EMPTY = PresentationModel.of("???", Displayable.of("???"));

    @Override
    public ObservableValue<PresentationModel> call(final TreeTableColumn.CellDataFeatures<PresentationModel, PresentationModel> cell)
      {
        return new ObservableValueBase<>() // FIXME: use a concrete specialization?
          {
            @Override @Nonnull
            public PresentationModel getValue()
              {
                try
                  {
                    final PresentationModel rowPm = cell.getValue().getValue();
                    final Aggregate<PresentationModel> aggregate = rowPm.as(Aggregate.class);
                    // FIXME: uses the column header names, should be an internal id instead
                    return aggregate.getByName(cell.getTreeTableColumn().getText())
                                    .map(columnPm -> (PresentationModel)new PresentationModelAsDelegateDecorator(
                                            columnPm,
                                            rowPm))
                                    .orElse(EMPTY);
                  }
                catch (AsException e)
                  {
                    return EMPTY;
                  }
              }
          };
      }
  }
