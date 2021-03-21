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
package it.tidalwave.role.ui.javafx.impl;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;
import it.tidalwave.util.As;
import it.tidalwave.util.Finder;
import it.tidalwave.role.Aggregate;
import it.tidalwave.role.Composite;
import it.tidalwave.role.ui.Displayable;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import static java.util.stream.Collectors.toList;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Slf4j @UtilityClass
public class Logging
  {
    public static final String INDENT = " ".repeat(100);

    public static void logObjects (@Nonnull final String prefix, @Nonnull final Collection<?> objects)
      {
        if (!log.isDebugEnabled())
          {
            return;
          }

        if (objects.isEmpty())
          {
            log.debug(">>>>{} <empty>", prefix);
          }
        else
          {
            for (final Object object : objects)
              {
                logObject(prefix, object);
              }
          }
      }

    public static void logObject (@Nonnull final String indent, @Nonnull final Object object)
      {
        if (!log.isDebugEnabled())
          {
            return;
          }

        if (object instanceof Displayable)
          {
            log.debug(">>>>     {}{}: {}", indent, object.getClass().getName(), ((Displayable)object).getDisplayName());
          }
        else
          {
            log.debug(">>>>     {}{}", indent, object);
          }

        if (object instanceof Aggregate)
          {
            final Aggregate<?> aggregate = (Aggregate<?>)object;
            // FIXME: should iterate on all values
            final Optional<?> name = aggregate.getByName("Name");
            final Optional<?> value = aggregate.getByName("Value");
            name.ifPresent(n -> logObject(indent + "    name  ", n));
            value.ifPresent(v -> logObject(indent + "    value ", v));
          }

        if (object instanceof As)
          {
            log.debug(">>>>    {} Composite roles", indent);
            logObjects(indent + "    ",
                       ((As)object).asMany(Object.class).stream().filter(o -> o != object).collect(toList()));
          }

        if (object instanceof Composite)
          {
            final Finder<?> finder = ((Composite<?, ?>)object).findChildren();
            log.debug(">>>>    {} Composite children", indent);
            logObjects(indent + "    ", finder.results().stream().filter(o -> o != object).collect(toList()));
          }
      }
  }