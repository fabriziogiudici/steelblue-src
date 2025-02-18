/*
 * *************************************************************************************************************************************************************
 *
 * SteelBlue: DCI User Interfaces
 * http://tidalwave.it/projects/steelblue
 *
 * Copyright (C) 2015 - 2025 by Tidalwave s.a.s. (http://tidalwave.it)
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
import java.io.PrintWriter;

/***************************************************************************************************************************************************************
 *
 * The role of an object that can be rendered into a {@link String} as plain text. Note that while it has a method
 * with the same signature as {@link StringRenderable}, it has modified semantics since it guarantees that the returned
 * string is a plain text.
 *
 * @stereotype Role
 *
 * @since   2.0-ALPHA-1
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@FunctionalInterface
public interface PlainTextRenderable extends StringRenderable
  {
    public static final Class<PlainTextRenderable> _PlainTextRenderable_ = PlainTextRenderable.class;

    /***********************************************************************************************************************************************************
     *
     * @since 3.2-ALPHA-1 (was previously on {@code Feedback8}
     **********************************************************************************************************************************************************/
    public default void renderTo (@Nonnull final StringBuilder stringBuilder,
                                  @Nonnull final Object ... args)
      {
        stringBuilder.append(render(args));
      }

    /***********************************************************************************************************************************************************
     *
     * @since 3.2-ALPHA-1 (was previously on {@code Feedback8}
     **********************************************************************************************************************************************************/
    public default void renderTo (@Nonnull final PrintWriter printWriter,
                                  @Nonnull final Object ... args)
      {
        printWriter.print(render(args));
      }
  }
