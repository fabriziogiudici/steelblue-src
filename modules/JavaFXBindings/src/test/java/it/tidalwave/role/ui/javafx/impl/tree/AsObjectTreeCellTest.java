/*
 * #%L
 * *********************************************************************************************************************
 *
 * SteelBlue
 * http://steelblue.tidalwave.it - hg clone https://bitbucket.org/tidalwave/steelblue-src
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
package it.tidalwave.role.ui.javafx.impl.tree;

import it.tidalwave.util.As;
import it.tidalwave.role.spi.DefaultDisplayable;
import it.tidalwave.role.ui.javafx.impl.CellBinder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static it.tidalwave.role.Displayable.Displayable;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
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
    public void setupFixture()
      {
        cellBinder = mock(CellBinder.class);
        underTest = new AsObjectTreeCell<>();
        underTest.cellBinder = cellBinder;
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test
    public void must_set_text_from_Displayable_when_non_empty()
      {
        final As asObject = mock(As.class);
        when(asObject.as(eq(Displayable))).thenReturn(new DefaultDisplayable("foo"));

        underTest.updateItem(asObject, false);

        assertThat(underTest.getText(), is("foo"));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test
    public void must_set_empty_test_when_empty()
      {
        final As asObject = mock(As.class);
        when(asObject.as(eq(Displayable))).thenReturn(new DefaultDisplayable("foo"));

        underTest.updateItem(asObject, true);

        assertThat(underTest.getText(), is(""));
      }

    // TODO: test setting of ContextMenu
  }