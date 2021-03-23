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

import javax.annotation.CheckForNull;
import it.tidalwave.util.annotation.VisibleForTesting;
import it.tidalwave.util.As;
import it.tidalwave.role.ui.javafx.impl.common.CellBinder;
import javafx.scene.control.TreeTableCell;
import lombok.AllArgsConstructor;

/***********************************************************************************************************************
 *
 * A specialisation of {@link TreeTableCell} which binds to an {@link As}-capable item.
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@AllArgsConstructor(staticName = "of")
class AsObjectTreeTableCell<T extends As> extends TreeTableCell<T, T>
  {
    @VisibleForTesting final CellBinder cellBinder;

    @Override
    public void updateItem (@CheckForNull final T item, final boolean empty)
      {
        super.updateItem(item, empty);
        cellBinder.bind(this, item, empty);
      }
  }
