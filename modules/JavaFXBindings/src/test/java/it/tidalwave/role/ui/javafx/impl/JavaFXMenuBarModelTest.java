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
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;
import it.tidalwave.util.Callback;
import it.tidalwave.role.ui.Displayable;
import it.tidalwave.role.ui.MenuBarModel;
import it.tidalwave.role.ui.UserAction;
import org.testng.annotations.Test;
import static org.mockito.Mockito.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

/***************************************************************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public class JavaFXMenuBarModelTest extends UserActionsTestSupport<JavaFXMenuBarModel, MenuBar>
  {
    private UserAction actionNoMenuBar;

    @Override
    public void start (@Nonnull final Stage stage)
      {
        createActions();
        actionNoMenuBar = UserAction.of(mock(Callback.class), List.of(Displayable.of("foo bar")));
        underTest = new JavaFXMenuBarModel(() ->
            List.of(actionFileOpen, actionFileClose, actionFileCloseAll, actionEditUndo,
                    actionEditRedo, actionSelectSelectAll, actionSelectDeselect, actionNoMenuBar));
        control = new MenuBar();
      }

    @Test
    public void test_populate()
      {
        // when
        underTest.populateImpl(binder, control);
        // then
        final var menus = control.getMenus();
        assertThat(menus.size(), is(3));
        final var menuFile = menus.get(0);
        final var menuEdit = menus.get(1);
        final var menuSelect = menus.get(2);
        assertThat(menuFile.getText(), is("File"));
        assertThat(menuEdit.getText(), is("Edit"));
        assertThat(menuSelect.getText(), is("Select"));

        final var menuFileItems = menuFile.getItems();
        assertThat(menuFileItems.size(), is(3));
        final var menuFileOpen = menuFileItems.get(0);
        final var menuFileClose = menuFileItems.get(1);
        final var menuFileCloseAll = menuFileItems.get(2);

        final var menuEditItems = menuEdit.getItems();
        assertThat(menuEditItems.size(), is(2));
        final var menuEditUndo = menuEditItems.get(0);
        final var menuEditRedo = menuEditItems.get(1);

        final var menuSelectItems = menuSelect.getItems();
        assertThat(menuSelectItems.size(), is(2));
        final var menuSelectSelectAll = menuSelectItems.get(0);
        final var menuSelectDeselect = menuSelectItems.get(1);

        verify(binder).bind(menuFileOpen, actionFileOpen);
        verify(binder).bind(menuFileClose, actionFileClose);
        verify(binder).bind(menuFileCloseAll, actionFileCloseAll);

        verify(binder).bind(menuEditUndo, actionEditUndo);
        verify(binder).bind(menuEditRedo, actionEditRedo);

        verify(binder).bind(menuSelectSelectAll, actionSelectSelectAll);
        verify(binder).bind(menuSelectDeselect, actionSelectDeselect);

        verifyNoMoreInteractions(binder);
        // actionNoMenuBar did not appear anywhere
      }
  }
