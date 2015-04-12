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
package it.tidalwave.role.ui.javafx;

import javax.annotation.Nonnull;
import java.util.WeakHashMap;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * @author Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class StackPaneSelector // FIXME: rename, introduce interface
  {
    private final WeakHashMap<String, StackPane> stackPaneMapByArea = new WeakHashMap<>();

    public void registerArea (final @Nonnull String area, final @Nonnull StackPane stackPane)
      {
        log.debug("addArea({}, {})", area, stackPane);
        stackPaneMapByArea.put(area, stackPane);
      }

    public void add (final @Nonnull String area, final @Nonnull Node node)
      {
        node.setVisible(false);
        findStackPaneFor(area).getChildren().add(node);
      }

    public void setShownNode (final @Nonnull Node node)
      {
        log.info("setShownNode({})", node);

        for (final StackPane stackPane : stackPaneMapByArea.values())
          {
            final ObservableList<Node> children = stackPane.getChildren();

            if (children.contains(node))
              {
                for (final Node child : children)
                  {
                    child.setVisible(false);
                  }

                node.setVisible(true); // at last
                return;
              }
          }

        throw new IllegalArgumentException("Node not in a managed StackPange: " + node);

      }

    @Nonnull
    private StackPane findStackPaneFor (final @Nonnull String area)
      {
        final StackPane stackPane = stackPaneMapByArea.get(area);

        if (stackPane == null)
          {
            throw new IllegalArgumentException("Area not handled: " + area);
          }

        return stackPane;
      }
 }
