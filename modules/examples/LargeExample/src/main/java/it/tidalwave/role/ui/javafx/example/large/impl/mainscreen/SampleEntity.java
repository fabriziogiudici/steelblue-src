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
package it.tidalwave.role.ui.javafx.example.large.impl.mainscreen;

import javax.annotation.Nonnull;
import lombok.Delegate;
import it.tidalwave.util.As;
import it.tidalwave.util.spi.AsSupport;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: $
 *
 **********************************************************************************************************************/
public class SampleEntity implements As
  {
    @Nonnull
    private final String id;

    @Delegate
    private final AsSupport asDelegate;

    public SampleEntity (final @Nonnull String id, final @Nonnull Object ... rolesOrFactories)
      {
        this.id = id;
        asDelegate = new AsSupport(this, rolesOrFactories);
      }

    @Override @Nonnull
    public String toString()
      {
        return String.format("SampleEntity(%s)", id);
      }
  }
