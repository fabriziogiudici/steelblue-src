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
package it.tidalwave.ui.javafx.impl.util;

import jakarta.annotation.Nonnull;
import it.tidalwave.util.ReflectionUtils;
import static it.tidalwave.ui.javafx.impl.util.JavaFXSafeComponentBuilder.builderFor;

/***************************************************************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public abstract class JavaFXPresentationBuilder<I, T extends I>
  {
    @Nonnull
    private final JavaFXSafeComponentBuilder<I, T> builder;

    public JavaFXPresentationBuilder()
      {
        final var t = ReflectionUtils.getTypeArguments(JavaFXPresentationBuilder.class, getClass());
        builder = builderFor((Class<I>)t.get(0), (Class<T>)t.get(1));
      }

    @Nonnull
    public final synchronized I create (@Nonnull final Object referenceHolder)
      {
        return builder.createInstance(referenceHolder);
      }
  }
