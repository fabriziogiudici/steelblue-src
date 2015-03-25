/*
 * #%L
 * *********************************************************************************************************************
 *
 * SteelBlue
 * http://steelblue.tidalwave.it - hg clone https://bitbucket.org/tidalwave/steelblue-src
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
package it.tidalwave.role.ui.javafx;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import javafx.beans.property.Property;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.Node;
import javafx.stage.Window;
import it.tidalwave.util.ui.UserNotificationWithFeedback;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.role.ui.BoundProperty;
import it.tidalwave.role.ui.UserAction;
import javafx.scene.control.TreeTableView;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public interface JavaFXBinder
  {
    public void setMainWindow (@Nonnull Window window);

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public void bind (@Nonnull Button button, @Nonnull UserAction action);

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public void bind (@Nonnull MenuItem menuItem, @Nonnull UserAction action);

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public void bind (@Nonnull TableView<PresentationModel> tableView, @Nonnull PresentationModel pm);

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public void bind (@Nonnull TreeView<PresentationModel> treeView, @Nonnull PresentationModel pm);

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public void bind (@Nonnull TreeTableView<PresentationModel> treeTableView, @Nonnull PresentationModel pm);

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public void bind (@Nonnull ListView<PresentationModel> treeView, @Nonnull PresentationModel pm);

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public <T> void bindBidirectionally (@Nonnull Property<T> property1, @Nonnull BoundProperty<T> property2);

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public <T> void bindBidirectionally (@Nonnull TextField textField,
                                         @Nonnull BoundProperty<String> textProperty,
                                         @Nonnull BoundProperty<Boolean> validProperty);

    /*******************************************************************************************************************
     *
     * Shows a modal dialog with the given content and provides feedback by means of the given notification.
     *
     * @param  node          the dialog content
     * @param  notification  the object notifying whether the operation is confirmed or cancelled
     *
     ******************************************************************************************************************/
    public void showInModalDialog (@Nonnull Node node, @Nonnull UserNotificationWithFeedback notification);

    // FIXME: use a Builder, merge with the above
    public void showInModalDialog (@Nonnull Node node,
                                   @Nonnull UserNotificationWithFeedback notification,
                                   @Nonnull BoundProperty<Boolean> valid);

    /*******************************************************************************************************************
     *
     * Opens the FileChooser for selecting a file. The outcome of the operation (confirmed or cancelled) will be
     * notified to the given notification object. The selected file will be set to the given bound property, which can
     * be also used to set the default value rendered on the FileChooser.
     *
     * @param  notification  the object notifying whether the operation is confirmed or cancelled
     * @param  selectedFile  the property containing the selected file
     *
     ******************************************************************************************************************/
    public void openFileChooserFor (@Nonnull UserNotificationWithFeedback notification,
                                    @Nonnull BoundProperty<Path> selectedFile);

    /*******************************************************************************************************************
     *
     * Opens the FileChooser for selecting a folder. The outcome of the operation (confirmed or cancelled) will be
     * notified to the given notification object. The selected folder will be set to the given bound property, which can
     * be also used to set the default value rendered on the FileChooser.
     *
     * @param  notification    the object notifying whether the operation is confirmed or cancelled
     * @param  selectedFolder  the property containing the selected folder
     *
     ******************************************************************************************************************/
    public void openDirectoryChooserFor (@Nonnull UserNotificationWithFeedback notification,
                                         @Nonnull BoundProperty<Path> selectedFolder);
  }
