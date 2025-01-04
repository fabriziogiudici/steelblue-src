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
package it.tidalwave.ui.javafx.impl;

import javax.annotation.Nonnull;
import java.util.List;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import it.tidalwave.ui.core.ToolBarControl;
import it.tidalwave.ui.core.spi.ToolBarControlSupport;
import it.tidalwave.ui.core.role.UserAction;
import it.tidalwave.ui.javafx.JavaFXBinder;
import it.tidalwave.ui.javafx.JavaFXToolBarControl;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/***************************************************************************************************************************************************************
 *
 * The JavaFX implementation for {@link ToolBarControl}.
 *
 * @author  Fabrizio Giudici
 * @since   1.1-ALPHA-4
 *
 **************************************************************************************************************************************************************/
@NoArgsConstructor @Slf4j
public class DefaultJavaFXToolBarControl extends ToolBarControlSupport<JavaFXBinder, ToolBar, Button> implements JavaFXToolBarControl
  {
    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    @Override @Nonnull
    protected Button createButton (@Nonnull final JavaFXBinder binder, @Nonnull final UserAction action)
      {
        final var button = new Button();
        binder.bind(button, action);
        return button;
      }

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    @Override
    protected void addButtonsToToolBar (@Nonnull final ToolBar toolBar, @Nonnull final List<Button> buttons)
      {
        toolBar.getItems().addAll(buttons);
      }
  }
