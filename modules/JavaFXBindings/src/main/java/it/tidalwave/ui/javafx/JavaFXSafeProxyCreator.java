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
package it.tidalwave.ui.javafx;

import javax.annotation.Nonnull;
import java.lang.reflect.Proxy;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import it.tidalwave.role.ui.javafx.impl.util.JavaFXSafeProxy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class JavaFXSafeProxyCreator
  {
    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @RequiredArgsConstructor
    public static class NodeAndDelegate
      {
        @Getter @Nonnull
        private final Node node;

        @Nonnull
        private final Object delegate;

        public <T> T getDelegate()
          {
            return (T)delegate;
          }
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public static <T> NodeAndDelegate createNodeAndDelegate (final @Nonnull Class<?> clazz,
                                                             final @Nonnull String resource)
      {
        try
          {
            final FXMLLoader loader = new FXMLLoader(clazz.getResource(resource));
            final Node node = (Node)loader.load();
            final T jfxController = (T)loader.getController();
            final Class<T> interfaceClass = (Class<T>)jfxController.getClass().getInterfaces()[0]; // FIXME
            final T safeJfxController = JavaFXSafeProxyCreator.createSafeProxy(jfxController, interfaceClass);

            return new NodeAndDelegate(node, safeJfxController);
          }
        catch (IOException e)
          {
            throw new RuntimeException(e);
          }
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public static <T> T createSafeProxy (final @Nonnull T target, final Class<T> interfaceClass)
      {
        return (T)Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                                         new Class[] { interfaceClass },
                                         new JavaFXSafeProxy<>(target));
      }
  }
