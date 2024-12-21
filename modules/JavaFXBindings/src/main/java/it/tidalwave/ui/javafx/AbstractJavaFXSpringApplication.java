/*
 * *********************************************************************************************************************
 *
 * SteelBlue: DCI User Interfaces
 * http://tidalwave.it/projects/steelblue
 *
 * Copyright (C) 2015 - 2024 by Tidalwave s.a.s. (http://tidalwave.it)
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
package it.tidalwave.ui.javafx;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.io.IOException;
import javafx.stage.Stage;
import javafx.application.Platform;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.tidalwave.role.ui.javafx.ApplicationPresentationAssembler;
import it.tidalwave.role.ui.javafx.PresentationAssembler;

/***********************************************************************************************************************
 *
 * A base class for all variants of JavaFX applications with Spring.
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
public abstract class AbstractJavaFXSpringApplication extends JavaFXApplicationWithSplash
  {
    // Don't use Slf4j and its static logger - give Main a chance to initialize things
    private final Logger log = LoggerFactory.getLogger(AbstractJavaFXSpringApplication.class);

    private ConfigurableApplicationContext applicationContext;

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    protected NodeAndDelegate<?> createParent()
      throws IOException
      {
        return NodeAndDelegate.load(getClass(), applicationFxml);
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Override
    protected void initializeInBackground()
      {
        log.info("initializeInBackground()");

        try
          {
            logProperties();
            // TODO: workaround for NWRCA-41
            System.setProperty("it.tidalwave.util.spring.ClassScanner.basePackages", "it");
            applicationContext = createApplicationContext();
            applicationContext.registerShutdownHook(); // this actually seems not working, onClosing() does
          }
        catch (Throwable t)
          {
            log.error("", t);
          }
      }

    /*******************************************************************************************************************
     *
     * Creates the application context.
     *
     * @return  the application context
     *
     ******************************************************************************************************************/
    @Nonnull
    protected abstract ConfigurableApplicationContext createApplicationContext();

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    protected final void onStageCreated (@Nonnull final Stage stage,
                                         @Nonnull final NodeAndDelegate<?> applicationNad)
      {
        assert Platform.isFxApplicationThread();
        JavaFXSafeProxyCreator.getJavaFxBinder().setMainWindow(stage);
        final var delegate = applicationNad.getDelegate();

        if (PresentationAssembler.class.isAssignableFrom(delegate.getClass()))
          {
            ((PresentationAssembler)delegate).assemble(applicationContext);
          }

        runApplicationAssemblers(applicationNad);
        Executors.newSingleThreadExecutor().execute(() -> onStageCreated(applicationContext));
      }

    /*******************************************************************************************************************
     *
     * Invoked when the {@link Stage} is created and the {@link ApplicationContext} has been initialized. Typically
     * the main class overrides this, retrieves a reference to the main controller and boots it.
     * This method is executed in a background thread.
     *
     * @param   applicationContext  the application context
     *
     ******************************************************************************************************************/
    protected void onStageCreated (@Nonnull final ApplicationContext applicationContext)
      {
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    protected void onClosing()
      {
        applicationContext.close();
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    private void runApplicationAssemblers (@Nonnull final NodeAndDelegate applicationNad)
      {
        Objects.requireNonNull(applicationContext, "applicationContext is null");
        applicationContext.getBeansOfType(ApplicationPresentationAssembler.class).values()
                .forEach(a -> a.assemble(applicationNad.getDelegate()));
      }

    /*******************************************************************************************************************
     *
     * Logs all the system properties.
     *
     ******************************************************************************************************************/
    private void logProperties()
      {
        for (final var e : new TreeMap<>(System.getProperties()).entrySet())
          {
            log.debug("{}: {}", e.getKey(), e.getValue());
          }
      }
  }