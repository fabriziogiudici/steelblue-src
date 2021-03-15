/*
 * #%L
 * *********************************************************************************************************************
 *
 * SteelBlue
 * http://steelblue.tidalwave.it - git clone git@bitbucket.org:tidalwave/steelblue-src.git
 * %%
 * Copyright (C) 2015 - 2021 Tidalwave s.a.s. (http://tidalwave.it)
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
package it.tidalwave.util;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;
import it.tidalwave.util.impl.DefaultPreferencesHandler;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
public interface PreferencesHandler
  {
    public static final String PROP_APP_NAME = PreferencesHandler.class.getPackage().getName() + ".appName";

    // FIXME: make private as soon as the right Java version is required
    public static AtomicReference<PreferencesHandler> __INSTANCE = new AtomicReference<>();

    @Nonnull
    public Path getAppFolder();

    @Nonnull
    public Path getLogFolder();

    public static void setAppName (final @Nonnull String name)
      {
        System.setProperty(PROP_APP_NAME, name);
      }

    /*******************************************************************************************************************
     *
     * main() probably needs it and Spring has not booted yet, so this class can be accessed also by this factory
     * method. Note that Spring instantiates the bean by calling this method, so we really have a singleton.
     *
     ******************************************************************************************************************/
    @Nonnull
    public static PreferencesHandler getInstance()
      {
        synchronized (PreferencesHandler.class)
          {
            if (__INSTANCE.get() == null)
              {
                __INSTANCE.set(new DefaultPreferencesHandler());
              }

            return __INSTANCE.get();
          }
      }
  }
