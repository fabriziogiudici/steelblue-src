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
package it.tidalwave.role.ui.javafx.impl;

import javax.annotation.Nonnull;
import java.util.List;
import it.tidalwave.util.Callback;
import it.tidalwave.role.ui.Displayable;
import it.tidalwave.role.ui.MenuBarModel;
import it.tidalwave.role.ui.UserAction;
import it.tidalwave.role.ui.javafx.JavaFXBinder;
import static org.mockito.Mockito.mock;

/***************************************************************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public class UserActionsTestSupport<T, C> extends TestNGApplicationTest
  {
    protected T underTest;

    protected C control;

    protected JavaFXBinder binder;

    protected UserAction actionFileOpen;

    protected UserAction actionFileClose;

    protected UserAction actionFileCloseAll;

    protected UserAction actionEditUndo;

    protected UserAction actionEditRedo;

    protected UserAction actionSelectSelectAll;

    protected UserAction actionSelectDeselect;

    protected void createActions()
      {
        binder = mock(JavaFXBinder.class);
        actionFileOpen = createAction("Open", "File");
        actionFileClose = createAction("Close", "File");
        actionFileCloseAll = createAction("Close all", "File");
        actionEditUndo = createAction("Edit", "Edit");
        actionEditRedo = createAction("Undo", "Edit");
        actionSelectSelectAll = createAction("Select all", "Select");
        actionSelectDeselect = createAction("Deselect", "Select");
      }

    @Nonnull
    private static UserAction createAction (@Nonnull final String displayName, @Nonnull final String path)
      {
        return UserAction.of(mock(Callback.class), List.of(Displayable.of(displayName), MenuBarModel.MenuPlacement.under(path)));
      }
  }
