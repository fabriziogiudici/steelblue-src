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

/***************************************************************************************************************************************************************
 *
 * The role of an object that can be visible or not.
 *
 * @stereotype  Role
 * @since       2.0-ALPHA-1
 * @author      Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@FunctionalInterface
public interface Visibility
  {
    public static final Class<Visibility> _Visible_ = Visibility.class;

    /***********************************************************************************************************************************************************
     * A role that is always visible.
     **********************************************************************************************************************************************************/
    public static final Visibility VISIBLE = () -> true;

    /***********************************************************************************************************************************************************
     * A role that is always invisible.
     **********************************************************************************************************************************************************/
    public static final Visibility INVISIBLE = () -> false;

    /***********************************************************************************************************************************************************
     * Returns the current visibility status.
     *
     * @return            {@code true} if the object is visible
     **********************************************************************************************************************************************************/
    @SuppressWarnings("UnusedReturnValue")
    public boolean isVisible();
  }
