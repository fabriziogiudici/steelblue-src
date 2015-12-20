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
package it.tidalwave.role.ui.javafx.impl;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import it.tidalwave.util.As;
import lombok.NoArgsConstructor;
import lombok.ToString;
import static java.util.Collections.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@NoArgsConstructor @ToString
public class RoleBag 
  {
    private final Map<Class<?>, List<Object>> map = new HashMap<>();
    
    public RoleBag (final @Nonnull As source, final @Nonnull List<Class<?>> roleTypes)
      {
        roleTypes.forEach(roleType -> copyRoles(source, roleType));
      }
    
    public <ROLE_TYPE> void put (final @Nonnull Class<ROLE_TYPE> roleClass, final @Nonnull ROLE_TYPE role)
      {
        putMany(roleClass, singletonList(role));
      }
    
    public <ROLE_TYPE> void putMany (final @Nonnull Class<ROLE_TYPE> roleClass,
                                     final @Nonnull Collection<? extends ROLE_TYPE> roles)
      {
        map.put(roleClass, new ArrayList<>(roles));
      }
    
    @Nonnull
    public <ROLE_TYPE> Optional<ROLE_TYPE> get (final @Nonnull Class<ROLE_TYPE> roleClass)
      {
        return getMany(roleClass).stream().findFirst();
      }
    
    @Nonnull
    public <ROLE_TYPE> List<ROLE_TYPE> getMany (final @Nonnull Class<ROLE_TYPE> roleClass)
      {
        return unmodifiableList((List<ROLE_TYPE>)map.getOrDefault(roleClass, emptyList()));
      }
    
    private <ROLE_TYPE> void copyRoles (final @Nonnull As item, final @Nonnull Class<ROLE_TYPE> roleClass)
      {
        putMany(roleClass, item.asMany(roleClass));
      }
  }
