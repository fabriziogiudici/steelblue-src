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
package it.tidalwave.role.ui.javafx.impl.util;

import javax.annotation.Nonnull;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import javafx.application.Platform;
import javafx.fxml.FXML;
import it.tidalwave.role.ui.javafx.Widget;
import it.tidalwave.ui.javafx.JavaFXSafeProxyCreator;
import javafx.fxml.FXMLLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static lombok.AccessLevel.PRIVATE;

/***********************************************************************************************************************
 *
 * @stereotype Factory
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor(access = PRIVATE) @Slf4j
public class JavaFXSafeComponentBuilder<I, T extends I>
  {
    @Nonnull
    private final Class<T> componentClass;

    @Nonnull
    private final Class<I> interfaceClass;

    private WeakReference<T> presentationRef = new WeakReference<>(null);

    @Nonnull
    public static <J, X extends J> JavaFXSafeComponentBuilder<J, X> builderFor (final @Nonnull Class<X> componentClass)
      {
        final Class<J> interfaceClass = (Class<J>)componentClass.getInterfaces()[0]; // FIXME: guess
        return new JavaFXSafeComponentBuilder<>(componentClass, interfaceClass);
      }

    @Nonnull
    public static <J, X extends J> JavaFXSafeComponentBuilder<J, X> builderFor (final @Nonnull Class<J> interfaceClass,
                                                                                final @Nonnull Class<X> componentClass)
      {
        return new JavaFXSafeComponentBuilder<>(componentClass, interfaceClass);
      }

    /*******************************************************************************************************************
     *
     * Creates an instance of a surrogate JavaFX delegate. JavaFX delegates (controllers in JavaFX jargon) are those
     * objects with fields annotated with {@link @FXML} that are created by the {@link FXMLLoader} starting from a
     * {@code .fxml} file. Sometimes a surrogate delegate is needed, that is a class that is not mapped to any
     * {@link @FXML} file, but whose fields are copied from another existing delegate.
     *
     * @param   componentClass      the class of the surrogate
     * @param   fxmlFieldsSource    the existing JavaFX delegate with {@code @FXML} annotated fields.
     * @return                      the new surrogate delegate
     *
     ******************************************************************************************************************/
    @Nonnull
    public static <J, X extends J> X createInstance (final @Nonnull Class<X> componentClass,
                                                     final @Nonnull Object fxmlFieldsSource)
      {
        final JavaFXSafeComponentBuilder<J, X> builder = builderFor(componentClass);
        return builder.createInstance(fxmlFieldsSource);
      }

    /*******************************************************************************************************************
     *
     * Creates an instance of a surrogate JavaFX delegate. JavaFX delegates (controllers in JavaFX jargon) are those
     * objects with fields annotated with {@link @FXML} that are created by the {@link FXMLLoader} starting from a
     * {@code .fxml} file. Sometimes a surrogate delegate is needed, that is a class that is not mapped to any
     * {@link @FXML} file, but whose fields are copied from another existing delegate.
     *
     * @param   fxmlFieldsSource    the existing JavaFX delegate with {@code @FXML} annotated fields.
     * @return                      the new surrogate delegate
     *
     ******************************************************************************************************************/
    @Nonnull
    public synchronized T createInstance (final @Nonnull Object fxmlFieldsSource)
      {
        log.trace("createInstance({})", fxmlFieldsSource);
        T presentation = presentationRef.get();

        if (presentation == null)
          {
            presentation = Platform.isFxApplicationThread() ? createComponentInstance() : createComponentInstanceInJAT();
            copyFxmlFields(presentation, fxmlFieldsSource); // FIXME: in JFX thread?

            try // FIXME // FIXME: in JFX thread?
              {
                presentation.getClass().getDeclaredMethod("initialize").invoke(presentation);
              }
            catch (NoSuchMethodException | SecurityException | IllegalAccessException
                 | InvocationTargetException e)
              {
                log.warn("No postconstruct in {}", presentation);
              }

            presentation = (T)Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                                                     new Class[] { interfaceClass },
                                                     new JavaFXSafeProxy<>(presentation));
            presentationRef = new WeakReference<>(presentation);
          }

        return presentation;
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    protected T createComponentInstance()
      {
        try
          {
            return componentClass.newInstance();
          }
        catch (InstantiationException | IllegalAccessException e)
          {
            log.error("", e);
            throw new RuntimeException(e);
          }
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private T createComponentInstanceInJAT()
      {
        final AtomicReference<T> reference = new AtomicReference<>();
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        Platform.runLater(new Runnable()
          {
            @Override
            public void run()
              {
                reference.set(createComponentInstance());
                countDownLatch.countDown();
              }
          });

        try
          {
            countDownLatch.await();
          }
        catch (InterruptedException e)
          {
            log.error("", e);
            throw new RuntimeException(e);
          }

        return reference.get();
      }

    /*******************************************************************************************************************
     *
     * Inject fields annotated with {@link FXML} in {@code source} to {@code target}.
     *
     * @param   target  the target object
     * @param   source  the source object
     *
     ******************************************************************************************************************/
    private void copyFxmlFields (final @Nonnull Object target, final @Nonnull Object source)
      {
        log.debug("injecting {} with fields from {}", target, source);
        final Map<String, Object> valuesMapByFieldName = new HashMap<>();

        for (final Field field : source.getClass().getDeclaredFields())
          {
            if (field.getAnnotation(FXML.class) != null)
              {
                final String name = field.getName();

                try
                  {
                    field.setAccessible(true);
                    final Object value = field.get(source);
                    valuesMapByFieldName.put(name, value);
                    log.trace(">>>> available field {}: {}", name, value);
                  }
                catch (IllegalArgumentException | IllegalAccessException e)
                  {
                    throw new RuntimeException("Cannot read field " + name + " from " + source, e);
                  }
              }
          }

        for (final Field field : target.getClass().getDeclaredFields())
          {
            final Widget widget = field.getAnnotation(Widget.class);
            final FXML fxml = field.getAnnotation(FXML.class);

            if ((widget != null) || (fxml != null))
              {
                final String name = (widget != null) ? widget.value() : field.getName();
                final Object value = valuesMapByFieldName.get(name);

                if (value == null)
                  {
                    throw new RuntimeException("Can't inject " + name + ": available: " + valuesMapByFieldName.keySet());
                  }

                field.setAccessible(true);

                try
                  {
                    field.set(target, value);
                  }
                catch (IllegalArgumentException | IllegalAccessException e)
                  {
                    throw new RuntimeException("Cannot inject field " + name + " to " + target, e);
                  }
              }
          }

          ReflectionUtils.injectDependencies(target, JavaFXSafeProxyCreator.BEANS);
      }
  }
