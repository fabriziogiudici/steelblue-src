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
package it.tidalwave.ui.core.role;

import jakarta.annotation.Nonnull;
import it.tidalwave.ui.core.role.impl.DefaultMutableDisplayable;

/***************************************************************************************************************************************************************
 *
 * A specialized {@link it.tidalwave.ui.core.role.LocalizedDisplayable} which is mutable.
 *
 * @stereotype Role
 *
 * @since   2.0-ALPHA-1
 * @author  Fabrizio Giudici
 * @it.tidalwave.javadoc.stable
 *
 **************************************************************************************************************************************************************/
public interface MutableLocalizedDisplayable extends MutableDisplayable, LocalizedDisplayable
  {
    public static final Class<MutableLocalizedDisplayable> _MutableLocalizedDisplayable_ =
            MutableLocalizedDisplayable.class;

    /***********************************************************************************************************************************************************
     * Creates an instance with an initial given display name in {@code Locale.ENGLISH}.
     *
     * @param  displayName    the display name
     * @return                the new instance
     * @since                 3.2-ALPHA-1
     **********************************************************************************************************************************************************/
    @Nonnull
    public static MutableLocalizedDisplayable of (@Nonnull final String displayName)
      {
        return of(displayName, "???");
      }

    /***********************************************************************************************************************************************************
     * Creates an instance with an initial given display name in {@code Locale.ENGLISH} and an explicit identifier for
     * {@code toString()}.
     *
     * @param  displayName    the display name
     * @param  toStringName   the name to be rendered when {@code toString()} is called
     * @return                the new instance
     * @since                 3.2-ALPHA-1
     **********************************************************************************************************************************************************/
    @Nonnull
    public static MutableLocalizedDisplayable of (@Nonnull final String displayName,
                                                  @Nonnull final String toStringName)
      {
        return new DefaultMutableDisplayable(displayName, toStringName);
      }
  }
