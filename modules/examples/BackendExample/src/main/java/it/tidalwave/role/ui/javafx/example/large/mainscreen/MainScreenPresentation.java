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
package it.tidalwave.role.ui.javafx.example.large.mainscreen;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import it.tidalwave.util.ui.UserNotificationWithFeedback;
import it.tidalwave.role.ui.BoundProperty;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.role.ui.UserAction;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: $
 *
 **********************************************************************************************************************/
public interface MainScreenPresentation
  {
    static class FormFields
      {
        public final BoundProperty<String> textProperty = new BoundProperty<>("1");
        public final BoundProperty<Boolean> booleanProperty = new BoundProperty<>(true);
      }

    public void bind (@Nonnull UserAction action,
                      @Nonnull UserAction actionDialogOk,
                      @Nonnull UserAction actionDialogCancelOk,
                      @Nonnull UserAction actionPickFile,
                      @Nonnull UserAction actionPickDirectory,
                      @Nonnull FormFields fields);

    public void showUp();

    public void populate (@Nonnull PresentationModel pm1, @Nonnull PresentationModel pm2);

    public void notify (@Nonnull UserNotificationWithFeedback notification);

    public void notify (@Nonnull String message);

    public void pickFile (@Nonnull BoundProperty<Path> selectedFile,
                          @Nonnull UserNotificationWithFeedback notification);

    public void pickDirectory (@Nonnull BoundProperty<Path> selectedFolder,
                               @Nonnull UserNotificationWithFeedback notification);
  }
