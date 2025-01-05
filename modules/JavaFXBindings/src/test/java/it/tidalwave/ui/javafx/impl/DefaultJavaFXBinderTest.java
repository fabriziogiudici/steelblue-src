/*
 * *************************************************************************************************************************************************************
 *
 * SteelBlue: DCI User Interfaces
 * http://tidalwave.it/projects/steelblue
 *
 * Copyright (C) 2015 - 2025 by Tidalwave s.a.s. (http://tidalwave.it)
 *
 * *************************************************************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 *
 * *************************************************************************************************************************************************************
 *
 * git clone https://bitbucket.org/tidalwave/steelblue-src
 * git clone https://github.com/tidalwave-it/steelblue-src
 *
 * *************************************************************************************************************************************************************
 */
package it.tidalwave.ui.javafx.impl;

import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.application.Platform;
import it.tidalwave.ui.core.role.Displayable;
import it.tidalwave.ui.core.role.UserAction;
import it.tidalwave.util.Callback;
import it.tidalwave.role.spi.SystemRoleFactory;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.mockito.stubbing.Answer;
import static org.testfx.assertions.api.Assertions.assertThat;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.mockito.Mockito.*;

/***************************************************************************************************************************************************************
 *
 * WARNING: for this to work on macOS, the application running the test (i.e. the Terminal or IntelliJ IDEA) must have the proper permissions. See
 *    <a href="https://github.com/TestFX/TestFX/issues/641">here</a>
 *
 * On Sonoma, go under System Settings / Privacy & Security / Accessibility.
 *
 * @author Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@Test(groups = "no-ci") @Slf4j
public class DefaultJavaFXBinderTest extends TestNGApplicationTest
  {
    private Button button;

    private DefaultJavaFXBinder underTest;

    private UserAction userAction;

    private UserAction userActionWithoutDisplayable;

    private Callback callback;

    private ExecutorService executorService;

    private final Displayable displayable = Displayable.of("new label");

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    @BeforeMethod
    public void setup()
            throws Throwable
      {
        SystemRoleFactory.reset();
        executorService = Executors.newSingleThreadExecutor();
        underTest = new DefaultJavaFXBinder(executorService);
        callback = mock(Callback.class);
        doAnswer((Answer<Void>)invocation ->
          {
            assertTrue(!Platform.isFxApplicationThread(), "Erroneously in JavaFX thread.");
            return null;
          }).when(callback).call();
        userAction = UserAction.of(callback, List.of(displayable));
        userActionWithoutDisplayable = UserAction.of(callback);
      }

    /***********************************************************************************************************************************************************
     * Shut down the executor service to prevent the thing from hanging at the end of the process.
     **********************************************************************************************************************************************************/
    @AfterMethod
    public void shutdown()
      {
        log.info("shutting down {}... ", executorService);
        executorService.shutdownNow();
      }

    /***********************************************************************************************************************************************************
     * Prepares the {@link Stage for the test}.
     **********************************************************************************************************************************************************/
    @Override
    public void start (@Nonnull final Stage stage)
      {
        button = new Button("original label");
        stage.setScene(new Scene(new StackPane(button), 100, 100));
        stage.show();
        stage.setAlwaysOnTop(true);
      }

    /**********************************************************************************************************************************************************/
    @Test(expectedExceptions = IllegalStateException.class)
    public void bind_button_must_throw_exception_when_invoked_in_the_wrong_thread()
      {
        underTest.bind(button, userAction);
      }

    /**********************************************************************************************************************************************************/
    @Test
    public void bind_button_to_Displayable_action_must_assign_label()
            throws Throwable
      {
        // when
        runLaterAndWait(() -> underTest.bind(button, userAction));
        // then
        assertThat(button).hasText("new label");
      }

    /**********************************************************************************************************************************************************/
    @Test
    public void bind_button_to_not_Displayable_action_must_not_assign_label()
            throws Throwable
      {
        // when
        runLaterAndWait(() -> underTest.bind(button, userActionWithoutDisplayable));
        // then
        assertThat(button).hasText("original label");
      }

    /**********************************************************************************************************************************************************/
    @Test(dataProvider = "falseTrue")
    public void bind_button_must_assign_correct_initial_enablement (final boolean enabled)
            throws Throwable
      {
        // given
        userAction.enabled().set(enabled);
        // when
        runLaterAndWait(() -> underTest.bind(button, userAction));
        // then
        if (enabled)
          {
            assertThat(button).isEnabled();
          }
        else
          {
            assertThat(button).isDisabled();
          }
      }

    /**********************************************************************************************************************************************************/
    @Test
    public void button_enablement_must_be_bound_to_UserAction_enablement()
            throws Throwable
      {
        // when
        runLaterAndWait(() -> underTest.bind(button, userAction));
        final var enabled1 = !button.isDisabled();
        userAction.enabled().set(false);
        Thread.sleep(100);
        final var enabled2 = !button.isDisabled();
        userAction.enabled().set(true);
        Thread.sleep(100);
        final var enabled3 = !button.isDisabled();
        // then
        assertTrue(enabled1, "Wrong initial enablement");
        assertFalse(enabled2, "Wrong second enablement");
        assertTrue(enabled3, "Wrong third enablement");
      }

    /**********************************************************************************************************************************************************/
    @Test
    public void bound_button_must_invoke_callback()
            throws Throwable
      {
        // given
        runLaterAndWait(() -> underTest.bind(button, userAction));
        // when
        clickOn(button);
        // then
        waitForThread();
        verify(callback).call();
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    protected void runLaterAndWait (@Nonnull final Runnable runnable)
            throws InterruptedException
      {
        log.info("runLaterAndWait()...");
        final var latch = new CountDownLatch(1);
        Platform.runLater(() ->
          {
            runnable.run();
            latch.countDown();
          });

        log.info(">>>> waiting for latch...");
        latch.await();
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    protected void waitForThread()
            throws InterruptedException
      {
        log.info("waitForThread()...");
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.SECONDS);
      }

    /**********************************************************************************************************************************************************/
    @DataProvider
    public static Object[][] falseTrue()
      {
        return new Object[][] {{ false }, { true }};
      }
  }