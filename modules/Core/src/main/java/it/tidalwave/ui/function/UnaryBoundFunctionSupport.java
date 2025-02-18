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
package it.tidalwave.ui.function;

import jakarta.annotation.Nonnull;
import it.tidalwave.ui.core.ChangingSource;

/***************************************************************************************************************************************************************
 *
 * @since   2.0-ALPHA-1
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public abstract class UnaryBoundFunctionSupport<DOMAIN_TYPE, CODOMAIN_TYPE>
                   extends BoundFunctionSupport<DOMAIN_TYPE, CODOMAIN_TYPE>
  {
    @Nonnull
    protected final ChangingSource<DOMAIN_TYPE> source;

    protected CODOMAIN_TYPE value;

    protected UnaryBoundFunctionSupport (@Nonnull final ChangingSource<DOMAIN_TYPE> source)
      {
        this.source = source;
        source.addPropertyChangeListener(event ->
                onSourceChange((DOMAIN_TYPE)event.getOldValue(), (DOMAIN_TYPE)event.getNewValue()));
      }

    protected void onSourceChange (@Nonnull final DOMAIN_TYPE oldSourceValue, @Nonnull final DOMAIN_TYPE newSourceValue)
      {
        final var oldValue = function(oldSourceValue);
        value = function(newSourceValue);
        fireValueChanged(oldValue, value);
      }

    @Nonnull
    protected abstract CODOMAIN_TYPE function (final DOMAIN_TYPE value);

    @Override @Nonnull
    public final CODOMAIN_TYPE get()
      {
        return value;
      }
  }
