/*
 * *************************************************************************************************************************************************************
 *
 * SteelBlue: DCI User Interfaces
 * http://tidalwave.it/projects/steelblue
 *
 * Copyright (C) 2015 - 2024 by Tidalwave s.a.s. (http://tidalwave.it)
 *
 * *************************************************************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 *
 * *************************************************************************************************************************************************************
 *
 * git clone https://bitbucket.org/tidalwave/steelblue-src
 * git clone https://github.com/tidalwave-it/steelblue-src
 *
 * *************************************************************************************************************************************************************
 */
package it.tidalwave.ui.core.spi;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.function.Supplier;
import it.tidalwave.util.As;
import it.tidalwave.ui.core.ToolBarModel;
import it.tidalwave.role.ui.UserAction;
import it.tidalwave.role.ui.UserActionProvider;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import static java.util.Collections.emptyList;
import static it.tidalwave.role.ui.UserActionProvider._UserActionProvider_;

/***************************************************************************************************************************************************************
 *
 * A support implementation for {@link ToolBarModel}.
 *
 * @param   <B>               the concrete type of the binder
 * @param   <T>               the concrete type of the toolbar
 * @since   1.1-ALPHA-6
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ToolBarModelSupport<B, T> implements ToolBarModel
  {
    @Delegate
    private final As as = As.forObject(this);

    /** The default supplier of {@link UserAction}s, can be injected for testing. */
    @Nonnull
    protected final Supplier<Collection<? extends UserAction>> userActionsSupplier;

    /***********************************************************************************************************************************************************
     * Default constructor.
     **********************************************************************************************************************************************************/
    protected ToolBarModelSupport()
      {
        userActionsSupplier = () -> maybeAs(_UserActionProvider_).map(UserActionProvider::getActions).orElse(emptyList());
      }

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    @SuppressWarnings("unchecked")
    public final void populate (@Nonnull final Object binder, @Nonnull final Object toolBar)
      {
        populateImpl((B)binder, (T)toolBar);
      }

    /***********************************************************************************************************************************************************
     * Populates the menu bar with menus.
     * @param   binder    the binder
     * @param   toolBar   the toolbar
     **********************************************************************************************************************************************************/
    protected abstract void populateImpl (@Nonnull B binder, @Nonnull T toolBar);
  }