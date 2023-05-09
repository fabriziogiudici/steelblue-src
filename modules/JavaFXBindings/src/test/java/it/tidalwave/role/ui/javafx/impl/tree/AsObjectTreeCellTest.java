/*
 * *********************************************************************************************************************
 *
 * SteelBlue: DCI User Interfaces
 * http://tidalwave.it/projects/steelblue
 *
 * Copyright (C) 2015 - 2023 by Tidalwave s.a.s. (http://tidalwave.it)
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
 * git clone https://bitbucket.org/tidalwave/steelblue-src
 * git clone https://github.com/tidalwave-it/steelblue-src
 *
 * *********************************************************************************************************************
 */
package it.tidalwave.role.ui.javafx.impl.tree;

import it.tidalwave.util.As;
import it.tidalwave.role.ui.Displayable;
import it.tidalwave.role.ui.javafx.impl.common.CellBinder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
public class AsObjectTreeCellTest
  {
    private AsObjectTreeCell<As> underTest;

    private CellBinder cellBinder;

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @BeforeMethod
    public void setup()
      {
        cellBinder = mock(CellBinder.class);
        underTest = new AsObjectTreeCell<>(cellBinder);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test
    public void must_set_text_from_Displayable_when_non_empty()
      {
        // given
        final var asObject = mock(As.class);
        when(asObject.as(Displayable.class)).thenReturn(Displayable.of("foo"));
        // when
        underTest.updateItem(asObject, false);
        // then
        assertThat(underTest.getText(), is("foo"));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test
    public void must_set_empty_test_when_empty()
      {
        // given
        final var asObject = mock(As.class);
        when(asObject.as(Displayable.class)).thenReturn(Displayable.of("foo"));
        // when
        underTest.updateItem(asObject, true);
        // then
        assertThat(underTest.getText(), is(""));
      }

    // TODO: test setting of ContextMenu
  }