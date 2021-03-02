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
 *
 *
 * *********************************************************************************************************************
 * #L%
 */
package it.tidalwave.role.ui.javafx.impl.tree;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.TreeItem;
import it.tidalwave.util.spi.AsDelegateProvider;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.util.spi.EmptyAsDelegateProvider;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class ObsoletePresentationModelDisposerTest
  {
    private ObsoletePresentationModelDisposer fixture;

    private List<PresentationModel> allPMs;

    @BeforeMethod
    public void setupFixture()
      {
        AsDelegateProvider.Locator.set(new EmptyAsDelegateProvider());
        fixture = new ObsoletePresentationModelDisposer();
        allPMs = new ArrayList<>();
      }

    @Test
    public void changed_must_dispose_all_PresentationModels()
      {
        // given
        final TreeItem<PresentationModel> treeItem = createTreeItemWithChildren(0);
        assertThat(allPMs.size(), is(85));
        // when
        fixture.changed(null, treeItem, null);
        // then
        for (final PresentationModel pm : allPMs)
          {
            verify(pm).dispose();
            verifyNoMoreInteractions(pm);
          }
      }

    @Nonnull
    private TreeItem<PresentationModel> createTreeItemWithChildren (final int level)
      {
        final PresentationModel pm = mock(PresentationModel.class);
        allPMs.add(pm);
        final TreeItem<PresentationModel> treeItem = new TreeItem<>(pm);

        if (level < 3)
          {
            for (int n = 0; n < 4; n++)
              {
                treeItem.getChildren().add(createTreeItemWithChildren(level + 1));
              }
          }

        return treeItem;
      }
  }