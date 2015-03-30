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
package it.tidalwave.role.ui.javafx.impl;

import javax.annotation.Nonnull;
import java.util.concurrent.Executor;
import javafx.beans.property.Property;
import javafx.beans.binding.BooleanExpression;
import javafx.stage.Window;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.application.Platform;
import it.tidalwave.util.AsException;
import it.tidalwave.role.ui.BoundProperty;
import it.tidalwave.role.ui.UserAction;
import it.tidalwave.role.ui.javafx.JavaFXBinder;
import it.tidalwave.role.ui.javafx.impl.dialog.DialogBindings;
import it.tidalwave.role.ui.javafx.impl.filechooser.FileChooserBindings;
import it.tidalwave.role.ui.javafx.impl.list.ListViewBindings;
import it.tidalwave.role.ui.javafx.impl.tableview.TableViewBindings;
import it.tidalwave.role.ui.javafx.impl.tree.TreeViewBindings;
import it.tidalwave.role.ui.javafx.impl.treetable.TreeTableViewBindings;
import lombok.Delegate;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.role.Displayable.Displayable;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class DefaultJavaFXBinder implements JavaFXBinder
  {
    private final Executor executor;

    private String invalidTextFieldStyle = "-fx-background-color: pink";

    interface Exclusions
      {
        public void setMainWindow (Window window);
      }

    @Delegate(excludes = Exclusions.class)
    private final TreeViewBindings treeItemBindings;

    @Delegate(excludes = Exclusions.class)
    private final TableViewBindings tableViewBindings;

    @Delegate(excludes = Exclusions.class)
    private final TreeTableViewBindings treeTableViewBindings;

    @Delegate(excludes = Exclusions.class)
    private final ListViewBindings listViewBindings;

    @Delegate(excludes = Exclusions.class)
    private final DialogBindings dialogBindings;

    @Delegate(excludes = Exclusions.class)
    private final FileChooserBindings fileChooserBindings;

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public DefaultJavaFXBinder (final @Nonnull Executor executor)
      {
        this.executor = executor;
        treeItemBindings = new TreeViewBindings(executor);
        tableViewBindings = new TableViewBindings(executor);
        treeTableViewBindings = new TreeTableViewBindings(executor);
        listViewBindings = new ListViewBindings(executor);
        dialogBindings = new DialogBindings(executor);
        fileChooserBindings = new FileChooserBindings(executor);
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void setMainWindow (final @Nonnull Window mainWindow)
      {
        treeItemBindings.setMainWindow(mainWindow);
        tableViewBindings.setMainWindow(mainWindow);
        dialogBindings.setMainWindow(mainWindow);
        fileChooserBindings.setMainWindow(mainWindow);
      }
    
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void bind (final @Nonnull Button button, final @Nonnull UserAction action)
      {
        assertIsFxApplicationThread();

        try
          {
            button.setText(action.as(Displayable).getDisplayName());
          }
        catch (AsException e)
          {
            // ok, no label
          }

        button.disableProperty().bind(adaptBoolean(action.enabled()).not());                
        button.setOnAction((event) -> action.actionPerformed());
      }
    
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void bind (final @Nonnull MenuItem menuItem, final @Nonnull UserAction action)
      {
        assertIsFxApplicationThread();

        try
          {
            menuItem.setText(action.as(Displayable).getDisplayName());
          }
        catch (AsException e)
          {
            // ok, no label
          }
        
        menuItem.disableProperty().bind(adaptBoolean(action.enabled()).not());                
        menuItem.setOnAction((event) -> action.actionPerformed());
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public <T> void bindBidirectionally (final @Nonnull Property<T> property1,
                                         final @Nonnull BoundProperty<T> property2)
      {
        assertIsFxApplicationThread();

        property1.bindBidirectional(new PropertyAdapter<>(executor, property2));
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public <T> void bindBidirectionally (final @Nonnull TextField textField,
                                         final @Nonnull BoundProperty<String> textProperty,
                                         final @Nonnull BoundProperty<Boolean> validProperty)
      {
        assertIsFxApplicationThread();

        textField.textProperty().bindBidirectional(new PropertyAdapter<>(executor, textProperty));

        // FIXME: weak listener
        validProperty.addPropertyChangeListener(
                (event) -> textField.setStyle(validProperty.get() ? "" : invalidTextFieldStyle));
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    private void assertIsFxApplicationThread()
      {
        if (!Platform.isFxApplicationThread())
          {
            throw new AssertionError("Must run in the JavaFX Application Thread");
          }
      }
    
    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private BooleanExpression adaptBoolean (BoundProperty<Boolean> property)
      {
        return BooleanExpression.booleanExpression(new PropertyAdapter<>(executor, property)); 
      }
  }
