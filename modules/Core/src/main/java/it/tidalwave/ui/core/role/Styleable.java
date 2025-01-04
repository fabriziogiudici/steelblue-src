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
import java.util.Collection;
import it.tidalwave.ui.core.role.impl.DefaultStyleable;

/***************************************************************************************************************************************************************
 *
 * A role which declares a set of styles for rendering.
 * 
 * @stereotype Role
 *
 * @since   2.0-ALPHA-1
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@FunctionalInterface
public interface Styleable
  {
    public static final Class<Styleable> _Styleable_ = Styleable.class;
    
    @Nonnull
    public Collection<String> getStyles();

    /***********************************************************************************************************************************************************
     * Creates a new instance from a collection of strings
     *
     * @param   styles    the style names
     * @return            the new instance
     * @since             3.2-ALPHA-2
     **********************************************************************************************************************************************************/
    @Nonnull
    public static Styleable of (@Nonnull final Collection<String> styles)
      {
        return new DefaultStyleable(styles);
      }

    /***********************************************************************************************************************************************************
     * Creates a new instance from a collection of strings
     *
     * @param   styles    the style names
     * @return            the new instance
     * @since             3.2-ALPHA-2
     **********************************************************************************************************************************************************/
    @Nonnull
    public static Styleable of (@Nonnull final String ... styles)
      {
        return new DefaultStyleable(styles);
      }
  }
