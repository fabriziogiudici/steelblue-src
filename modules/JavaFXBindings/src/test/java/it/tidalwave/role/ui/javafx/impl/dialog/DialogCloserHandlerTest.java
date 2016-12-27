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
package it.tidalwave.role.ui.javafx.impl.dialog;

import it.tidalwave.util.Callback;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class DialogCloserHandlerTest
  {
    private Stage dialogStage;

    private DialogCloserHandler fixture;

    private ExecutorService executorService;

    private AssertionError error;

    private boolean doSomethingCalled;

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @BeforeMethod
    public void setupFixture()
      {
        executorService = Executors.newSingleThreadExecutor();
        dialogStage = mock(Stage.class);
        error = null;
        doSomethingCalled = false;

        final Callback callback = () ->
          {
            try
              {
                doSomethingCalled = true;
                assertThat(Platform.isFxApplicationThread(), is(false));
              }
            catch (AssertionError e)
              {
                error = e;
              }
          };

        fixture = new DialogCloserHandler(executorService, dialogStage, callback);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test
    public void must_close_the_dialog_Stage()
      {
        fixture.handle(new ActionEvent());

        verify(dialogStage).close();
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test
    public void must_call_doSomething_in_a_non_FX_thread()
      throws InterruptedException
      {
        fixture.handle(new ActionEvent());
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        assertThat(doSomethingCalled, is(true));

        if (error != null)
          {
            throw error;
          }
      }
  }