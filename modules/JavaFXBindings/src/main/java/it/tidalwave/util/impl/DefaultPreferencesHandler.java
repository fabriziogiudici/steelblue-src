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
package it.tidalwave.util.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import it.tidalwave.util.PreferencesHandler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor
public class DefaultPreferencesHandler implements PreferencesHandler
  {
    @Getter
    private final Path appFolder;

    @Getter
    private final Path logFolder;

    public DefaultPreferencesHandler ()
      {
        try
          {
            final String appName = System.getProperty(PROP_APP_NAME);
            Objects.requireNonNull(appName,
                                   "You must call System.setProperty(PROP_APP_NAME, \"...\") before getting here");

            final String osName = System.getProperty("os.name").toLowerCase();
            String pattern = "";

            switch (osName)
              {
                case "linux":
                  pattern = "%s/.%s/";
                  break;

                case "mac os x":
                  pattern = "%s/Library/Application Support/%s/";
                  break;

                case "windows":
                  pattern = "%s/AppData/Local/%s/";
                  break;

                default:
                  throw new ExceptionInInitializerError("Unknown o.s.: " + osName);
              }

            final String home = System.getProperty("user.home", "/tmp");
            appFolder = Paths.get(String.format(pattern, home, appName)).toAbsolutePath();
            logFolder = appFolder.resolve("logs").toAbsolutePath();
            Files.createDirectories(logFolder);
            System.err.println("App folder: " + appFolder);
            System.err.println("Logging folder: " + logFolder);
          }
        catch (IOException e)
          {
            throw new RuntimeException(e);
          }
      }
  }
