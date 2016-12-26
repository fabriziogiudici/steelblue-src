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
package it.tidalwave.role.ui.spi;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import it.tidalwave.role.Aggregate;
import it.tidalwave.role.spi.MapAggregate;
import it.tidalwave.role.ui.PresentationModel;
import lombok.NoArgsConstructor;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@NoArgsConstructor(staticName = "builder")
public class MapAggregateSupplement
  {
    private final Map<String, PresentationModel> map = new HashMap<>();

    @Nonnull
    public MapAggregateSupplement with (final @Nonnull String name, final @Nonnull Object ... roles)
      {
        map.put(name, new DefaultPresentationModel("", roles));
        return this;
      }

    @Nonnull
    public Aggregate create()
      {
        return new MapAggregate<>(map);
      }
  }
