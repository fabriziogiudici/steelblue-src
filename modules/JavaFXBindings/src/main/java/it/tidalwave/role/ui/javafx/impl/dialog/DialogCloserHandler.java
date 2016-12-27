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

import javax.annotation.Nonnull;
import java.util.concurrent.Executor;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * An {@link EventHandler} for {@link ActionEvent}s that closes a dialog {@link Stage} and performs a task in a
 * background thread. It's useful to be bound as the callback of buttons in a dialog that should close the dialog.
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor @Slf4j
abstract class DialogCloserHandler implements EventHandler<ActionEvent>
  {
    @Nonnull
    private final Executor executor;

    @Nonnull
    private final Stage dialogStage;

    @Override
    public void handle (final @Nonnull ActionEvent event)
      {
        dialogStage.close();
        executor.execute(() ->
          {
            try
              {
                doSomething();
              }
            catch (Exception e)
              {
                log.error("", e);
              }
          });
      }

    protected abstract void doSomething() throws Exception;
  }
