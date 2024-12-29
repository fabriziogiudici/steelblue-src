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
package it.tidalwave.ui.core.role.impl;

import jakarta.annotation.Nonnull;
import java.text.Collator;
import java.util.Comparator;
import java.io.Serializable;
import it.tidalwave.util.As;
import it.tidalwave.ui.core.role.Displayable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import static it.tidalwave.ui.core.role.Displayable._Displayable_;

/***************************************************************************************************************************************************************
 *
 * A {@link Comparator} for classes implementing the {@link As} interface containing a {@link Displayable} role.
 *
 * @author  Fabrizio Giudici
 * @it.tidalwave.javadoc.draft Will be moved to a different package
 *
 **************************************************************************************************************************************************************/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AsDisplayableComparator implements Comparator<As>, Serializable
  {
    private static final long serialVersionUID = 7452490266897348L;

    private static final AsDisplayableComparator INSTANCE = new AsDisplayableComparator();

    @Nonnull
    public static AsDisplayableComparator getInstance()
      {
        return INSTANCE;
      }

    @Override
    public int compare (@Nonnull final As o1, @Nonnull final As o2)
      {
        return Collator.getInstance().compare(o1.as(_Displayable_).getDisplayName(), o2.as(_Displayable_).getDisplayName());
      }
  }