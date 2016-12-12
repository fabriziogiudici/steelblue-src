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
package it.tidalwave.role.ui.javafx;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.nio.file.Path;
import javafx.beans.property.Property;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.control.TreeTableView;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Window;
import it.tidalwave.util.ui.UserNotificationWithFeedback;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.role.ui.BoundProperty;
import it.tidalwave.role.ui.UserAction;
import javafx.scene.control.ToggleButton;

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
    public void bind (@Nonnull ButtonBase button, @Nonnull UserAction action);

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
    public void bind (@Nonnull TableView<PresentationModel> tableView,
                      @Nonnull PresentationModel pm,
                      @Nonnull Runnable runnable);

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    default public void bind (final @Nonnull TableView<PresentationModel> tableView,
                              final @Nonnull PresentationModel pm)
      {
        bind(tableView, pm, () -> {});
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public void bind (@Nonnull TreeView<PresentationModel> treeView,
                      @Nonnull PresentationModel pm,
                      @Nonnull Runnable runnable);

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    default public void bind (final @Nonnull TreeView<PresentationModel> treeView,
                              final @Nonnull PresentationModel pm)
      {
        bind(treeView, pm, () -> {});
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public void bind (@Nonnull TreeTableView<PresentationModel> treeTableView,
                      @Nonnull PresentationModel pm,
                      @Nonnull Runnable runnable);

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    default public void bind (final @Nonnull TreeTableView<PresentationModel> treeTableView,
                              final @Nonnull PresentationModel pm)
      {
        bind(treeTableView, pm, () -> {});
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public void bind (@Nonnull ListView<PresentationModel> listView,
                      @Nonnull PresentationModel pm,
                      @Nonnull Runnable callback);

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    default public void bind (final @Nonnull ListView<PresentationModel> listView,
                              final @Nonnull PresentationModel pm)
      {
        bind(listView, pm, () -> {});
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public void bind (@Nonnull ComboBox<PresentationModel> comboBox,
                      @Nonnull PresentationModel pm,
                      @Nonnull Runnable callback);

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    default public void bind (final @Nonnull ComboBox<PresentationModel> comboBox,
                              final @Nonnull PresentationModel pm)
      {
        bind(comboBox, pm, () -> {});
      }

    /*******************************************************************************************************************
     *
     * Given a {@link PresentationModel} that contains a {@link Composite}, populate the pane with
     * {@link ToggleButton}s associated to the elements of the {@link Composite}. Each element is searched for the
     * following roles:
     *
     * <ul>
     * <li>{@link UserActionProvider} (mandatory) to provide a callback for the button</li>
     * <li>{@link Displayable} to provide a text for the button</li>
     * <li>{@link Styleable} to provide a CSS style for the button</li>
     * </ul>
     *
     * The pane must be pre-populated with at least one button, which will be queried for the CSS style.
     *
     ******************************************************************************************************************/
    public void bindToggleButtons (@Nonnull Pane pane, @Nonnull PresentationModel pm);

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public void bindButtonsInPane (@Nonnull GridPane gridPane, @Nonnull Collection<UserAction> actions);

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
