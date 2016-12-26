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
package it.tidalwave.role.ui.javafx.example.large;

import javax.annotation.Nonnull;
import javafx.application.Platform;
import org.springframework.context.ApplicationContext;
import it.tidalwave.role.ui.javafx.example.large.impl.mainscreen.MainScreenPresentationControl;
import it.tidalwave.ui.javafx.JavaFXSpringApplication;

/***********************************************************************************************************************
 *
 * The main class initializes the logging facility and starts the JavaFX application.
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class Main extends JavaFXSpringApplication
  {
    public static void main (final @Nonnull String ... args)
      {
        try
          {
            Platform.setImplicitExit(true);
            launch(args);
          }
        catch (Throwable t)
          {
            // Don't use logging facilities here, they could be not initialized
            t.printStackTrace();
            System.exit(-1);
          }
      }

    @Override
    protected void onStageCreated (final @Nonnull ApplicationContext applicationContext)
      {
        applicationContext.getBean(MainScreenPresentationControl.class).start();
      }
  }