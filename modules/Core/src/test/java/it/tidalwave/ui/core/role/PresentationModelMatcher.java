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
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import it.tidalwave.util.AsException;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mockito.ArgumentMatcher;
import org.hamcrest.Matcher;
import static java.util.stream.Collectors.*;
import static it.tidalwave.util.ShortNames.*;

/***************************************************************************************************************************************************************
 *
 * A {@link Matcher} for {@link PresentationModel}.
 *
 * @stereotype  mockito matcher
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@NotThreadSafe @NoArgsConstructor(staticName = "presentationModel") @Slf4j
public class PresentationModelMatcher implements ArgumentMatcher<PresentationModel>
  {
    private final StringBuilder pmDescription = new StringBuilder("PresentationModel");

    private String separator = "";

    private final List<Class<?>> expectedRoleTypes = new ArrayList<>();

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    @Nonnull
    public PresentationModelMatcher withRole (@Nonnull final Class<?> roleType)
      {
        expectedRoleTypes.add(roleType);
        pmDescription.append(separator).append(" with role ").append(shortName(roleType));
        separator = ", ";
        return this;
      }

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    @Override
    public boolean matches (@Nullable final PresentationModel pm)
      {
        if (pm == null)
          {
            return false;
          }

        for (final var roleType : expectedRoleTypes)
          {
            try
              {
                pm.as(roleType);
              }
            catch (AsException e)
              {
                final var actualRoles = pm.asMany(Object.class);
                final Collection<Class<?>> actualRoleTypes =
                        actualRoles.stream().map(Object::getClass).collect(toList());

                log.error("Failed matching: expected roles types:");
                expectedRoleTypes.forEach(ert -> log.error("        {}", shortName(ert)));
                log.error("Failed matching: actual roles types:");
                actualRoleTypes.forEach(art -> log.error("        {}", shortName(art, true)));
                log.error("Failed matching: actual roles:");
                actualRoles.forEach(ar -> log.error("        {}", shortId(ar)));

                return false;
              }
          }

        return true;
      }

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    @Override @Nonnull
    public String toString()
      {
        return pmDescription.toString();
      }
  }
