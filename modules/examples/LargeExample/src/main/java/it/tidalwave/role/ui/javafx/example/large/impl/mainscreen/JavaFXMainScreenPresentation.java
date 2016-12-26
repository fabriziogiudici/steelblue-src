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
package it.tidalwave.role.ui.javafx.example.large.impl.mainscreen;

import it.tidalwave.role.ui.javafx.example.large.impl.FlowController;
import it.tidalwave.ui.javafx.JavaFXSafeProxyCreator;
import lombok.Delegate;
import static it.tidalwave.ui.javafx.JavaFXSafeProxyCreator.createNodeAndDelegate;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: $
 *
 **********************************************************************************************************************/
public class JavaFXMainScreenPresentation implements MainScreenPresentation
  {
    static interface Exclusions
      {
        public void showUp();
      }

    private static final String FXML_URL = "/it/tidalwave/role/ui/javafx/example/large/mainscreen/MainScreen.fxml";

    private final JavaFXSafeProxyCreator.NodeAndDelegate nad = createNodeAndDelegate(getClass(), FXML_URL);

    @Delegate(excludes = Exclusions.class)
    private final MainScreenPresentation delegate = nad.getDelegate();

    @Override
    public void showUp()
      {
        FlowController.setContent(nad.getNode());
      }
  }
