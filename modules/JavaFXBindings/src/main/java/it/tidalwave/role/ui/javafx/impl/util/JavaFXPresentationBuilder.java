/*
 * #%L
 * *********************************************************************************************************************
 *
 * SteelBlue
 * http://steelblue.tidalwave.it - git clone git@bitbucket.org:tidalwave/steelblue-src.git
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
package it.tidalwave.role.ui.javafx.impl.util;

import javax.annotation.Nonnull;
import java.util.List;
import static it.tidalwave.role.ui.javafx.impl.util.JavaFXSafeComponentBuilder.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public abstract class JavaFXPresentationBuilder<I, T extends I>
  {
    @Nonnull
    private final JavaFXSafeComponentBuilder<I, T> builder;

    public JavaFXPresentationBuilder()
      {
        final List<Class<?>> t = ReflectionUtils.getTypeArguments(JavaFXPresentationBuilder.class, getClass());
        builder = builderFor((Class<I>)t.get(0), (Class<T>)t.get(1));
      }

    @Nonnull
    public final synchronized I create (final @Nonnull Object referenceHolder)
      {
        return builder.createInstance(referenceHolder);
      }
  }
