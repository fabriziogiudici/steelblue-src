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
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import it.tidalwave.util.NotFoundException;

/***************************************************************************************************************************************************************
 *
 * A role that provides {@link UserAction}s.
 *
 * @since   2.0-ALPHA-1
 * @stereotype role
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public interface UserActionProvider
  {
    public static final Class<UserActionProvider> _UserActionProvider_ = UserActionProvider.class;

    /***********************************************************************************************************************************************************
     * Returns a collection of {@link UserAction}s.
     *
     * @return  a collection of actions
     **********************************************************************************************************************************************************/
    @Nonnull
    public Collection<? extends UserAction> getActions();

    /***********************************************************************************************************************************************************
     * Returns the default action, if available.
     *
     *
     * @return                      the default action
     * @throws  NotFoundException   if there's no default action
     * @deprecated                  Use {@link #getOptionalDefaultAction()}
     **********************************************************************************************************************************************************/
    @Nonnull @Deprecated
    public UserAction getDefaultAction()
      throws NotFoundException;

    /***********************************************************************************************************************************************************
     * Returns the default action, if available.
     *
     * @since   3.1-ALPHA-2
     * @return                      the default action
     **********************************************************************************************************************************************************/
    @Nonnull
    public default Optional<UserAction> getOptionalDefaultAction()
      {
        try
          {
            return Optional.of(getDefaultAction());
          }
        catch (NotFoundException e)
          {
            return Optional.empty();
          }
      }

    /***********************************************************************************************************************************************************
     * Factory method which creates an instance out of an array of {@link UserAction}s. The first one is considered the
     * default action.
     *
     * @since   3.1-ALPHA-2
     * @param   actions     the actions
     * @return              the {@code UserActionProvider}
     **********************************************************************************************************************************************************/
    @Nonnull
    public static UserActionProvider of (@Nonnull final UserAction ... actions)
      {
        return new UserActionProvider()
          {
            @Override @Nonnull
            public Collection<? extends UserAction> getActions()
              {
                return List.of(actions);
              }

            @Override @Nonnull
            public UserAction getDefaultAction()
              throws NotFoundException
              {
                if (actions.length == 0)
                  {
                    throw new NotFoundException();
                  }

                return actions[0];
              }
          };
      }
  }
