/*
 * #%L
 * *********************************************************************************************************************
 *
 * SteelBlue
 * http://steelblue.tidalwave.it - git clone git@bitbucket.org:tidalwave/steelblue-src.git
 * %%
 * Copyright (C) 2015 - 2021 Tidalwave s.a.s. (http://tidalwave.it)
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
 *
 *
 * *********************************************************************************************************************
 * #L%
 */
package it.tidalwave.role.ui.javafx.impl.treetable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.util.As;
import it.tidalwave.util.AsException;
import lombok.Delegate;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/***********************************************************************************************************************
 *
 * A decorator for {@link PresentationModel} that also searches for roles in a specified delegate as a fallback.
 *
 * @stereotype Decorator
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor @ToString
public class PresentationModelAsDelegateDecorator implements PresentationModel
  {
    @Delegate(excludes = As.class) @Nonnull
    private final PresentationModel pmDelegate;


    private final As asDelegate;

    @Override @Nonnull
    public <T> T as (final @Nonnull Class<T> type)
      {
        try
          {
            return pmDelegate.as(type);
          }
        catch (AsException e)
          {
            return asDelegate.as(type);
          }
      }

    @Override @Nonnull
    public <T> T as (final @Nonnull Class<T> type, final @Nonnull NotFoundBehaviour<T> notFoundBehaviour)
      {
        throw new UnsupportedOperationException("Not implemented yet");
      }

    @Override @Nonnull
    public <T> Collection<T> asMany (final @Nonnull Class<T> type)
      {
        final List<T> results = new ArrayList<>();
        results.addAll(pmDelegate.asMany(type));
        results.addAll(asDelegate.asMany(type));

        return results;
      }
  }
