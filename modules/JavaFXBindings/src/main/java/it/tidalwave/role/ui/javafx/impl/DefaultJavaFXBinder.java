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
package it.tidalwave.role.ui.javafx.impl;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Objects;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.beans.property.Property;
import javafx.beans.binding.BooleanExpression;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.stage.Window;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.application.Platform;
import it.tidalwave.util.AsException;
import it.tidalwave.util.NotFoundException;
import it.tidalwave.role.SimpleComposite;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.role.ui.BoundProperty;
import it.tidalwave.role.ui.UserAction;
import it.tidalwave.role.ui.javafx.JavaFXBinder;
import it.tidalwave.role.ui.javafx.impl.dialog.DialogBindings;
import it.tidalwave.role.ui.javafx.impl.combobox.ComboBoxBindings;
import it.tidalwave.role.ui.javafx.impl.filechooser.FileChooserBindings;
import it.tidalwave.role.ui.javafx.impl.list.ListViewBindings;
import it.tidalwave.role.ui.javafx.impl.tableview.TableViewBindings;
import it.tidalwave.role.ui.javafx.impl.tree.TreeViewBindings;
import it.tidalwave.role.ui.javafx.impl.treetable.TreeTableViewBindings;
import it.tidalwave.role.ui.javafx.impl.util.PropertyAdapter;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import static java.util.Collections.*;
import static java.util.Objects.*;
import static java.util.stream.Collectors.toList;
import static it.tidalwave.role.SimpleComposite._SimpleComposite_;
import static it.tidalwave.role.ui.Displayable._Displayable_;
import static it.tidalwave.role.ui.Styleable._Styleable_;
import static it.tidalwave.role.ui.UserActionProvider._UserActionProvider_;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
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
    private final ComboBoxBindings comboBoxBindings;

    @Delegate(excludes = Exclusions.class)
    private final DialogBindings dialogBindings;

    @Delegate(excludes = Exclusions.class)
    private final FileChooserBindings fileChooserBindings;

    private final CellBinder cellBinder;

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public DefaultJavaFXBinder (final @Nonnull Executor executor)
      {
        this.executor = executor;
        cellBinder = new DefaultCellBinder(executor);
        comboBoxBindings = new ComboBoxBindings(executor, cellBinder);
        treeItemBindings = new TreeViewBindings(executor, cellBinder);
        tableViewBindings = new TableViewBindings(executor, cellBinder);
        treeTableViewBindings = new TreeTableViewBindings(executor, cellBinder);
        listViewBindings = new ListViewBindings(executor, cellBinder);
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
    public void bind (final @Nonnull ButtonBase button, final @Nonnull UserAction action)
      {
        Objects.requireNonNull(button, "button");
        assertIsFxApplicationThread();

        try
          {
            button.setText(action.as(_Displayable_).getDisplayName());
          }
        catch (AsException e)
          {
            // ok, no label
          }

        button.disableProperty().bind(adaptBoolean(action.enabled()).not());
        button.setOnAction((event) -> executor.execute(() -> action.actionPerformed()));
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
            menuItem.setText(action.as(_Displayable_).getDisplayName());
          }
        catch (AsException e)
          {
            // ok, no label
          }

        menuItem.disableProperty().bind(adaptBoolean(action.enabled()).not());
        menuItem.setOnAction(event -> executor.execute(action::actionPerformed));
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
        requireNonNull(textField, "textField");
        requireNonNull(textProperty, "textProperty");
        requireNonNull(validProperty, "validProperty");

        textField.textProperty().bindBidirectional(new PropertyAdapter<>(executor, textProperty));

        // FIXME: weak listener
        validProperty.addPropertyChangeListener(
                (event) -> textField.setStyle(validProperty.get() ? "" : invalidTextFieldStyle));
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void bindToggleButtons (final @Nonnull Pane pane, final @Nonnull PresentationModel pm)
      {
        assert Platform.isFxApplicationThread();

        final ToggleGroup group = new ToggleGroup();
        final ObservableList<Node> children = pane.getChildren();
        final ObservableList<String> prototypeStyleClass = children.get(0).getStyleClass();
        final SimpleComposite<PresentationModel> pmc = pm.as(_SimpleComposite_);
        children.setAll(pmc.findChildren().results().stream()
                                                    .map(cpm -> createToggleButton(cpm, prototypeStyleClass, group))
                                                    .collect(toList()));
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Override
    public void bindButtonsInPane (final @Nonnull GridPane gridPane,
                                   final @Nonnull Collection<UserAction> actions)
      {
        assert Platform.isFxApplicationThread();

        final ObservableList<ColumnConstraints> columnConstraints = gridPane.getColumnConstraints();
        final ObservableList<Node> children = gridPane.getChildren();

        columnConstraints.clear();
        children.clear();
        final AtomicInteger columnIndex = new AtomicInteger(0);

        actions.forEach(menuAction ->
          {
            final ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(100.0 / actions.size());
            columnConstraints.add(column);
            final Button button = createButton();
            GridPane.setConstraints(button, columnIndex.getAndIncrement(), 0);
            bind(button, menuAction);
            children.add(button);
          });
      }

    /*******************************************************************************************************************
     *
     * Create a {@code Button} for the menu bar.
     *
     * @param   text    the label of the button
     * @return          the button
     *
     ******************************************************************************************************************/
    @Nonnull
    private Button createButton()
      {
        final Button button = new Button();
        GridPane.setHgrow(button, Priority.ALWAYS);
        GridPane.setVgrow(button, Priority.ALWAYS);
        GridPane.setHalignment(button, HPos.CENTER);
        GridPane.setValignment(button, VPos.CENTER);
        button.setPrefSize(999, 999); // fill
        button.getStyleClass().add("mainMenuButton");

        return button;
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private ToggleButton createToggleButton (final @Nonnull PresentationModel pm,
                                             final @Nonnull List<String> baseStyleClass,
                                             final @Nonnull ToggleGroup group)
      {
        final ToggleButton button = new ToggleButton();
        button.setToggleGroup(group);
        button.setText(pm.maybeAs(_Displayable_).map(d -> d.getDisplayName()).orElse(""));
        button.getStyleClass().addAll(baseStyleClass);
        button.getStyleClass().addAll(pm.maybeAs(_Styleable_).map(s -> s.getStyles()).orElse(emptyList()));

        try
          {
            bind(button, pm.as(_UserActionProvider_).getDefaultAction());
          }
        catch (NotFoundException e)
          {
            // ok, no UserActionProvider
          }

        if (group.getSelectedToggle() == null)
          {
            group.selectToggle(button);
          }

        return button;
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
    private BooleanExpression adaptBoolean (final @Nonnull BoundProperty<Boolean> property)
      {
        return BooleanExpression.booleanExpression(new PropertyAdapter<>(executor, property));
      }
  }
