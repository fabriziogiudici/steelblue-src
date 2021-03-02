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
package it.tidalwave.ui.javafx;

import javax.annotation.Nonnull;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.application.Platform;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import it.tidalwave.role.ui.javafx.JavaFXBinder;
import it.tidalwave.role.ui.javafx.impl.util.JavaFXSafeProxy;
import it.tidalwave.role.ui.javafx.impl.DefaultJavaFXBinder;
import it.tidalwave.role.ui.javafx.impl.util.ReflectionUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static lombok.AccessLevel.PRIVATE;

/***********************************************************************************************************************
 *
 * This facility class create a thread-safe proxy for the JavaFX delegate (controller). Thread-safe means that it can
 * be called by any thread and the JavaFX UI related stuff will be safely invoked in the JavaFX UI Thread.
 * It is usually used in this way:
 *
 * <pre>
 * // This is a Spring bean
 * public class JavaFxFooBarPresentation implements FooBarPresentation
 *   {
 *     private static final String FXML_URL = "/my/package/javafx/FooBar.fxml";
 *
 *     @Inject
 *     private FlowController flowController;
 *
 *     private final NodeAndDelegate nad = createNodeAndDelegate(getClass(), FXML_URL);
 *
 *     private final FooBarPresentation delegate = nad.getDelegate();
 *
 *     public void showUp()
 *       {
 *         flowController.doSomething(nad.getNode());
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
 * and returns a {@link NodeAndDelegate} that contains both the visual {@link Node} and its delegate (controller).
 *
 * The latter is wrapped by a safe proxy that makes sure that any method invocation (such as {@code showData()} in the
 * example is again executed in the JavaFX UI Thread. This means that the Presentation object methods can be invoked
 * in any thread.
 *
 * For method returning {@code void}, the method invocation is asynchronous; that is, the caller is not blocked waiting
 * for the method execution completion. If a return value is provided, the invocation is synchronous, and the caller
 * will correctly wait the completion of the execution in order to get the result value.
 *
 * A typical JavaFX delegate (controller) looks like:
 *
 * <pre>
 * // This is not a Spring bean - created by the FXMLLoader
 * public class JavaFxFooBarPresentationDelegate implements FooBarPresentation
 *   {
 *     @FXML
 *     private Label label;
 *
 *     @FXML
 *     private Button button;
 *
 *     @Inject // the only thing that can be injected, by means of JavaFXSafeProxyCreator
 *     private JavaFxBinder binder;
 *
 *     @Override
 *     public void bind (final UserAction action)
 *       {
 *         binder.bind(button, action);
 *       }
 *
 *     @Override
 *     public void showData (final String data)
 *       {
 *         label.setText(data);
 *       }
 *  }
 * </pre>
 *
 * Not only all the methods invoked on the delegate are guaranteed to run in the JavaFX UI thread, but also its
 * constructor, as per JavaFX requirements.
 *
 * A Presentation Delegate must not try to have dependency injection from Spring (for instance, by means of AOP),
 * otherwise a deadlock could be triggered.
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class JavaFXSafeProxyCreator
  {
    public final static Map<Class<?>, Object> BEANS = new HashMap<>();

    @Getter
    private static final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

    @Getter
    private static final JavaFXBinder javaFxBinder = new DefaultJavaFXBinder(executor);

    static
      {
        executor.setWaitForTasksToCompleteOnShutdown(false);
        executor.setThreadNamePrefix("javafxBinder-");
        // Fix for STB-26
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(10000);
        BEANS.put(JavaFXBinder.class, javaFxBinder);
        BEANS.put(Executor.class, executor);
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @RequiredArgsConstructor(access = PRIVATE)
    public static class NodeAndDelegate
      {
        @Getter @Nonnull
        private final Node node;

        @Nonnull
        private final Object delegate;

        @Nonnull
        public <T> T getDelegate()
          {
            return (T)delegate;
          }

        @Nonnull
        public static <T> NodeAndDelegate load (final @Nonnull Class<T> clazz, final @Nonnull String resource)
          throws IOException
          {
            log.debug("NodeAndDelegate({}, {})", clazz, resource);
            assert Platform.isFxApplicationThread() : "Not in JavaFX UI Thread";
            final FXMLLoader loader = new FXMLLoader(clazz.getResource(resource));
            final Node node = (Node)loader.load();
            final T jfxController = loader.getController();
            ReflectionUtils.injectDependencies(jfxController, BEANS);
            final Class<?>[] interfaces = jfxController.getClass().getInterfaces();

            if (interfaces.length == 0)
              {
                log.warn("{} has no interface: not creating safe proxy", jfxController.getClass());
                log.debug(">>>> load({}, {}) completed", clazz, resource);
                return new NodeAndDelegate(node, jfxController);
              }
            else
              {
                final Class<T> interfaceClass = (Class<T>)interfaces[0]; // FIXME
                final T safeDelegate = JavaFXSafeProxyCreator.createSafeProxy(jfxController, interfaceClass);
                log.debug(">>>> load({}, {}) completed", clazz, resource);
                return new NodeAndDelegate(node, safeDelegate);
              }
          }
      }

    /*******************************************************************************************************************
     *
     * Creates a {@link NodeAndDelegate} for the given presentation class. The FXML resource name is inferred by
     * default, For instance, is the class is named {@code JavaFXFooBarPresentation}, the resource name is
     * {@code FooBar.fxml} and searched in the same packages as the class.
     *
     * @see #createNodeAndDelegate(java.lang.Class, java.lang.String)
     *
     * @since 1.0-ALPHA-13
     *
     * @param   presentationClass   the class of the presentation for which the resources must be created.
     *
     ******************************************************************************************************************/
    @Nonnull
    public static <T> NodeAndDelegate createNodeAndDelegate (final @Nonnull Class<?> presentationClass)
      {
        final String resource = presentationClass.getSimpleName().replaceAll("^JavaFX", "")
                                                                 .replaceAll("^JavaFx", "")
                                                                 .replaceAll("Presentation$", "")
                                                                 + ".fxml";
        return createNodeAndDelegate(presentationClass, resource);
      }

    /*******************************************************************************************************************
     *
     * Creates a {@link NodeAndDelegate} for the given presentation class.
     *
     * @param   presentationClass   the class of the presentation for which the resources must be created.
     * @param   fxmlResourcePath    the path of the FXML resource
     *
     ******************************************************************************************************************/
    @Nonnull
    public static <T> NodeAndDelegate createNodeAndDelegate (final @Nonnull Class<?> presentationClass,
                                                             final @Nonnull String fxmlResourcePath)
      {
        log.debug("createNodeAndDelegate({}, {})", presentationClass, fxmlResourcePath);

        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<NodeAndDelegate> nad = new AtomicReference<>();
        final AtomicReference<RuntimeException> exception = new AtomicReference<>();

        if (Platform.isFxApplicationThread())
          {
            try
              {
                return NodeAndDelegate.load(presentationClass, fxmlResourcePath);
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
                nad.set(NodeAndDelegate.load(presentationClass, fxmlResourcePath));
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

        if (nad.get() == null)
          {
            throw new RuntimeException("Likely deadlock in the JavaFX Thread: couldn't create NodeAndDelegate");
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
