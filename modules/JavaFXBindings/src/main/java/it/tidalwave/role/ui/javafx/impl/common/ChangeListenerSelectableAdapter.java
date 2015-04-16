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
package it.tidalwave.role.ui.javafx.impl.common;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.concurrent.Executor;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.util.AsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.role.ui.Selectable.Selectable;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor @Slf4j
public class ChangeListenerSelectableAdapter implements ChangeListener<PresentationModel>
  {
    @Nonnull
    protected final Executor executor;

    @Override
    public void changed (final @Nonnull ObservableValue<? extends PresentationModel> ov,
                         final @Nonnull PresentationModel oldItem,
                         final @CheckForNull PresentationModel item)
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
                    log.trace("No Selectable role for {}", item); // ok, do nothing
                  }
              });
          }
      }
  }
