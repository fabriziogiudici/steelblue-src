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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.application.Platform;
import it.tidalwave.role.ui.javafx.impl.util.JavaFXSafeProxy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * This facility class create a thread-safe proxy for the JavaFX delegate (controller). Thread-safe means that it can
 * be called by any thread and the JavaFX UI related stuff will be safely invoked in the JavaFX UI Thread.
 * It is usually used in this way:
 * 
 * <pre>
 * @Configurable
 * public class JavaFxFooBarPresentation implements FooBarPresentation
 *   {
 *     private static final String FXML_URL = "/my/package/javafx/FooBar.fxml";
 *     
 *     @Inject
 *     private Provider &lt;Service>&gt; service;
 *     
 *     private final NodeAndDelegate nad = createNodeAndDelegate(getClass(), FXML_URL);
 * 
 *     private final FooBarPresentation delegate = nad.getDelegate();
 *     
 *     public void showUp()  
 *       {
 *         service.get().doSomething(nad.getNode());
 *       }
 * 
 *     public void showData (final String data)
 *       {
 *         delegate.showData(data);
 *       }
 *   }
 * </pre>
 *
 * The method {@link #createNodeAndDelegate(java.lang.Class, java.lang.String)} safely invokes the {@link FXMLLoader}
 * and returns a {@link NodeAndDelegate} that contains both the visual {@link Node} and its delegate (controller). The
 * latter is wrapped by a safe proxy that makes sure that any method invpcation (such as {@code showData()} in the
 * example is again executed in the JavaFX UI Thread.
 * 
 * For method returning {@code void}, the method invocation is asynchronous; that is, the caller is not blocked waiting
 * for the method execution completion. If a return value is provided, the invocation is synchronous, and the caller 
 * will correctly wait the completion of the execution in order to get the result value.
 * 
 * A typical JavaFX delegate (controller) looks like:
 * 
 * <pre>
 * @Configurable
 * public class JavaFxFooBarPresentationDelegate implements FooBarPresentation
 *   {
 *     @FXML
 *     private Label label;
 *     
 *     @Inject
 *     private Provider<Service> service;
 *     
 *     @Override
 *     public void showData (final String data)
 *       {
 *         label.setText(data);
 *         service.get().doSomething();
 *       }
 *  }
 * </pre>
 * 
 * No only all the methods invoked on the delegate are guaranteed to run in the JavaFX UI thread, but also its 
 * constructor, as per JavaFX requirements.
 * 
 * There are some caveat to take care of, though, to avoid triggering a deadlock, when the presentation is integrated
 * with Spring and dependency injection, as in the example above. First, any injected service into the JavaFX delegate
 * must be referenced by an indirect {@link Provider}. Second, any method that dereferences an injected object and
 * that is called during the Spring initialization code (for instance, by another class {@link @PostConstruct} must 
 * not return any value.
 * 
 * The reason for the deadlock is that during Spring bean initialization and creation (for instance 
 * {@code JavaFxFooBarPresentation}) the set of beans is locked. Then {@code createNodeAndDelegate()} requires the
 * creation of {@code JavaFxFooBarPresentationDelegate} in the JavaFX UI thread. At this point, any direct 
 * [@code @Inject} (not by means of {@code Provider}) would require access to the set of Spring beans - and here we go
 * with the deadlock. 
 * 
 * Only using {@code Provider}s avoids the problem since they defer the access to the Spring beans. In case of 
 * dependency resolution, being in a {@code void} method avoids the problem as the invocation is synchronous.
 * 
 * Note that, Because the implementation doesn't use {@code  synchronized} but other concurrent synchronization
 * facilities, is likely that the deadlock is not detected by a debugger.
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class JavaFXSafeProxyCreator
  {
    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public static class NodeAndDelegate 
      {
        @Getter @Nonnull
        private final Node node;

        @Nonnull
        private final Object safeDelegate;
        
        @Nonnull
        public <T> T getDelegate()
          {
            return (T)safeDelegate;
          }

        public <T> NodeAndDelegate (final @Nonnull Class<T> clazz, final @Nonnull String resource)
          throws IOException
          {
            log.debug("NodeAndDelegate({}, {})", clazz, resource);
            assert Platform.isFxApplicationThread() : "Not in JavaFX UI Thread";
            final FXMLLoader loader = new FXMLLoader(clazz.getResource(resource));
            node = (Node)loader.load();
            final T jfxController = loader.getController();
            final Class<T> interfaceClass = (Class<T>)jfxController.getClass().getInterfaces()[0]; // FIXME
            safeDelegate = JavaFXSafeProxyCreator.createSafeProxy(jfxController, interfaceClass);
            log.debug(">>>> load({}, {}) completed", clazz, resource);
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
        log.debug("createNodeAndDelegate({}, {})", clazz, resource);
    
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<NodeAndDelegate> nad = new AtomicReference<>();
        final AtomicReference<RuntimeException> exception = new AtomicReference<>();
      
        if (Platform.isFxApplicationThread()) 
          {
            try
              {
                return new NodeAndDelegate(clazz, resource);
              }
            catch (IOException e)
              {
                exception.set(new RuntimeException(e));
              }
          }

        Platform.runLater(() -> 
          {
            try
              {
                nad.set(new NodeAndDelegate(clazz, resource));
              }
            catch (RuntimeException e)
              {
                exception.set(e);
              }
            catch (Exception e)
              {
                exception.set(new RuntimeException(e));
              }
            
            latch.countDown();
          });
        
        try
          {
            latch.await(10, TimeUnit.SECONDS); // FIXME
          }
        catch (InterruptedException e) 
          {
            throw new RuntimeException(e);
          }

        if (exception.get() != null)
          {
            throw exception.get();
          }
        
        return nad.get();
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
