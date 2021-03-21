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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import it.tidalwave.role.ui.UserAction;
import it.tidalwave.util.As;
import lombok.NoArgsConstructor;
import lombok.ToString;
import static it.tidalwave.role.ui.UserActionProvider._UserActionProvider_;
import static java.util.Collections.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@NoArgsConstructor @ToString
public class RoleBag
  {
    private final Map<Class<?>, List<Object>> map = new HashMap<>();

    public RoleBag (@Nonnull final As source, @Nonnull final List<Class<?>> roleTypes)
      {
        roleTypes.forEach(roleType -> copyRoles(source, roleType));
      }

    public <ROLE_TYPE> void put (@Nonnull final Class<ROLE_TYPE> roleClass, @Nonnull final ROLE_TYPE role)
      {
        putMany(roleClass, singletonList(role));
      }

    public <ROLE_TYPE> void putMany (@Nonnull final Class<ROLE_TYPE> roleClass,
                                     @Nonnull final Collection<? extends ROLE_TYPE> roles)
      {
        map.put(roleClass, new ArrayList<>(roles));
      }

    @Nonnull
    public <ROLE_TYPE> Optional<ROLE_TYPE> get (@Nonnull final Class<ROLE_TYPE> roleClass)
      {
        return getMany(roleClass).stream().findFirst();
      }

    @Nonnull
    public <ROLE_TYPE> List<ROLE_TYPE> getMany (@Nonnull final Class<ROLE_TYPE> roleClass)
      {
        return unmodifiableList((List<ROLE_TYPE>)map.getOrDefault(roleClass, emptyList()));
      }

    /*******************************************************************************************************************
     *
     * Returns the default user action, which is he first action of the first
     * {@link it.tidalwave.role.ui.UserActionProvider}.
     *
     * @return    the user action
     *
     ******************************************************************************************************************/
    @Nonnull
    public Optional<UserAction> getDefaultUserAction()
      {
        return getMany(_UserActionProvider_).stream()
                                            .flatMap(a -> a.getOptionalDefaultAction().stream())
                                            .findFirst();
      }

    private <ROLE_TYPE> void copyRoles (@Nonnull final As item, @Nonnull final Class<ROLE_TYPE> roleClass)
      {
        putMany(roleClass, item.asMany(roleClass));
      }
  }
