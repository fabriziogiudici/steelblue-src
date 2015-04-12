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
package it.tidalwave.role.ui.javafx.impl.tree;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javafx.scene.control.TreeCell;
import javafx.scene.control.cell.TextFieldTreeCell;
import com.google.common.annotations.VisibleForTesting;
import org.springframework.beans.factory.annotation.Configurable;
import it.tidalwave.util.As;
import it.tidalwave.role.Displayable;
import it.tidalwave.role.ui.javafx.impl.CellActionBinder;
import it.tidalwave.role.ui.javafx.impl.util.Utils;
import static it.tidalwave.role.Displayable.*;

/***********************************************************************************************************************
 *
 * An implementation of {@link TreeCell} that retrieves the display name from {@link Displayable} and creates a
 * contextualised pop-up menu.
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Configurable
public class AsObjectTreeCell<T extends As> extends TextFieldTreeCell<T>
  {
    @Inject @Nonnull
    @VisibleForTesting CellActionBinder cellActionBinder;

    @Override
    public void updateItem (final @CheckForNull T item, final boolean empty)
      {
        super.updateItem(item, empty);
        setText((item == null) ? "" : item.as(Displayable).getDisplayName());
        cellActionBinder.bindActions(this, item);
        Utils.setRoleStyles(getStyleClass(), item);
      }
  }
