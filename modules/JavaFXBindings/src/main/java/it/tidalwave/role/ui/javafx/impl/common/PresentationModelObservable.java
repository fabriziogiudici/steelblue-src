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
package it.tidalwave.role.ui.javafx.impl.common;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.function.Supplier;
import it.tidalwave.role.Aggregate;
import it.tidalwave.role.ui.PresentationModel;
import javafx.beans.value.ObservableValueBase;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TreeTableColumn;
import lombok.RequiredArgsConstructor;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor
public final class PresentationModelObservable extends ObservableValueBase
  {
    @Nonnull
    private final Supplier<PresentationModel> rowPresentationModelSupplier;

    @Nonnull
    private final Supplier<String> columnKeySupplier;

    @Override @Nonnull
    public final PresentationModel getValue()
      {
        final PresentationModel rowPm = rowPresentationModelSupplier.get();
        final Optional<Aggregate<PresentationModel>> aggregate =
                (Optional<Aggregate<PresentationModel>>)(Object)rowPm.maybeAs(Aggregate.class);
        // FIXME: uses the column header names, should be an internal id instead
        return aggregate.flatMap(a -> a.getByName(columnKeySupplier.get()))
                        .map(columnPm -> PresentationModelAsDelegateDecorator.decorating(columnPm, rowPm))
                        .orElse(PresentationModel.empty());
      }

    @Nonnull
    public static PresentationModelObservable of (
            @Nonnull final TableColumn.CellDataFeatures<PresentationModel, PresentationModel> cell)
      {
        return new PresentationModelObservable(
                () -> cell.getValue(),
                () -> cell.getTableColumn().getText());
      }

    @Nonnull
    public static PresentationModelObservable of (
            @Nonnull final TreeTableColumn.CellDataFeatures<PresentationModel, PresentationModel> cell)
      {
        return new PresentationModelObservable(
                () -> cell.getValue().getValue(),
                () -> cell.getTreeTableColumn().getText());
      }
  }
