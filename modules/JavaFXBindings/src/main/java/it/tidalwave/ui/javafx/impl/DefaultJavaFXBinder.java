/*
 * *************************************************************************************************************************************************************
 *
 * SteelBlue: DCI User Interfaces
 * http://tidalwave.it/projects/steelblue
 *
 * Copyright (C) 2015 - 2025 by Tidalwave s.a.s. (http://tidalwave.it)
 *
 * *************************************************************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 *
 * *************************************************************************************************************************************************************
 *
 * git clone https://bitbucket.org/tidalwave/steelblue-src
 * git clone https://github.com/tidalwave-it/steelblue-src
 *
 * *************************************************************************************************************************************************************
 */
package it.tidalwave.ui.javafx.impl;

import jakarta.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
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
import javafx.stage.Window;
import javafx.application.Platform;
import it.tidalwave.role.SimpleComposite;
import it.tidalwave.ui.core.BoundProperty;
import it.tidalwave.ui.core.role.Displayable;
import it.tidalwave.ui.core.role.PresentationModel;
import it.tidalwave.ui.core.role.Styleable;
import it.tidalwave.ui.core.role.UserAction;
import it.tidalwave.ui.core.role.UserActionProvider;
import it.tidalwave.ui.javafx.JavaFXBinder;
import it.tidalwave.ui.javafx.impl.combobox.ComboBoxBindings;
import it.tidalwave.ui.javafx.impl.common.CellBinder;
import it.tidalwave.ui.javafx.impl.common.DefaultCellBinder;
import it.tidalwave.ui.javafx.impl.common.PropertyAdapter;
import it.tidalwave.ui.javafx.impl.dialog.DialogBindings;
import it.tidalwave.ui.javafx.impl.filechooser.FileChooserBindings;
import it.tidalwave.ui.javafx.impl.list.ListViewBindings;
import it.tidalwave.ui.javafx.impl.tableview.TableViewBindings;
import it.tidalwave.ui.javafx.impl.tree.TreeViewBindings;
import it.tidalwave.ui.javafx.impl.treetable.TreeTableViewBindings;
import it.tidalwave.util.As;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.*;
import static it.tidalwave.ui.core.role.Displayable._Displayable_;
import static it.tidalwave.ui.core.role.Styleable._Styleable_;
import static it.tidalwave.ui.core.role.UserActionProvider._UserActionProvider_;

/***************************************************************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@Slf4j
public class DefaultJavaFXBinder implements JavaFXBinder
  {
    private static final As.Type<SimpleComposite<PresentationModel>>  _SimpleCompositePresentationModel_ = new As.Type<>(SimpleComposite.class);

    private final Executor executor;

    private final String invalidTextFieldStyle = "-fx-background-color: pink";

    @Delegate
    private final TreeViewBindings treeItemBindings;

    @Delegate
    private final TableViewBindings tableViewBindings;

    @Delegate
    private final TreeTableViewBindings treeTableViewBindings;

    @Delegate
    private final ListViewBindings listViewBindings;

    @Delegate
    private final ComboBoxBindings comboBoxBindings;

    @Delegate
    private final DialogBindings dialogBindings;

    @Delegate
    private final FileChooserBindings fileChooserBindings;

    private final CellBinder cellBinder;

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    public DefaultJavaFXBinder (@Nonnull final Executor executor)
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

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    @Override
    public void setMainWindow (@Nonnull final Window mainWindow)
      {
        treeItemBindings.setMainWindow(mainWindow);
        tableViewBindings.setMainWindow(mainWindow);
        dialogBindings.setMainWindow(mainWindow);
        fileChooserBindings.setMainWindow(mainWindow);
      }

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    @Override
    public void bind (@Nonnull final ButtonBase button, @Nonnull final UserAction action)
      {
        enforceFxApplicationThread();
        action.maybeAs(_Displayable_).ifPresent(d -> button.setText(d.getDisplayName()));
        button.setOnAction(__ -> executor.execute(action::actionPerformed));
        bindEnableProperty(button.disableProperty(), action.enabled());
      }

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    @Override
    public void bind (@Nonnull final MenuItem menuItem, @Nonnull final UserAction action)
      {
        enforceFxApplicationThread();
        menuItem.setText(action.maybeAs(_Displayable_).map(Displayable::getDisplayName).orElse(""));
        menuItem.setOnAction(__ -> executor.execute(action::actionPerformed));
        bindEnableProperty(menuItem.disableProperty(), action.enabled());
      }

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    @Override
    public <T, S> void bind (@Nonnull final BoundProperty<? super T> target,
                             @Nonnull final Property<? extends S> source,
                             @Nonnull final Function<S, T> adapter)
      {
        enforceFxApplicationThread();
        source.addListener((_1, _2, newValue) -> executor.execute(() -> target.set(adapter.apply(newValue))));
      }

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    @Override @SuppressWarnings("unchecked")
    public <T, S> void bindBidirectionally (@Nonnull final BoundProperty<? super T> property1,
                                            @Nonnull final Property<S> property2,
                                            @Nonnull final Function<? super S, T> adapter,
                                            @Nonnull final Function<? super T, ? extends S> reverseAdapter)
      {
        enforceFxApplicationThread();
        property2.addListener((_1, _2, newValue) -> executor.execute(() -> property1.set(adapter.apply(newValue))));
        property1.addPropertyChangeListener(evt -> Platform.runLater(() -> property2.setValue(reverseAdapter.apply((T)evt.getNewValue()))));
      }

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    @Override
    public void bindBidirectionally (@Nonnull final TextField textField,
                                     @Nonnull final BoundProperty<String> textProperty,
                                     @Nonnull final BoundProperty<Boolean> validProperty)
      {
        enforceFxApplicationThread();
        requireNonNull(textField, "textField");
        requireNonNull(textProperty, "textProperty");
        requireNonNull(validProperty, "validProperty");

        textField.textProperty().bindBidirectional(new PropertyAdapter<>(executor, textProperty));

        // FIXME: weak listener
        validProperty.addPropertyChangeListener(__ -> textField.setStyle(validProperty.get() ? "" : invalidTextFieldStyle));
      }

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    @Override
    public void bindToggleButtons (@Nonnull final Pane pane, @Nonnull final PresentationModel pm)
      {
        enforceFxApplicationThread();
        final var group = new ToggleGroup();
        final var children = pane.getChildren();
        final var prototypeStyleClass = children.get(0).getStyleClass();
        final SimpleComposite<PresentationModel> pmc = pm.as(_SimpleCompositePresentationModel_);
        children.setAll(pmc.findChildren().stream().map(cpm -> createToggleButton(cpm, prototypeStyleClass, group)).collect(toList()));
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    @Override
    public void bindButtonsInPane (@Nonnull final GridPane gridPane, @Nonnull final Collection<UserAction> actions)
      {
        enforceFxApplicationThread();
        final var columnConstraints = gridPane.getColumnConstraints();
        final var children = gridPane.getChildren();

        columnConstraints.clear();
        children.clear();
        final var columnIndex = new AtomicInteger(0);

        actions.forEach(menuAction ->
          {
            final var column = new ColumnConstraints();
            column.setPercentWidth(100.0 / actions.size());
            columnConstraints.add(column);
            final var button = createButton();
            GridPane.setConstraints(button, columnIndex.getAndIncrement(), 0);
            bind(button, menuAction);
            children.add(button);
          });
      }

    /***********************************************************************************************************************************************************
     * {@return a new {@code Button}} for the menu bar.
     **********************************************************************************************************************************************************/
    @Nonnull
    private Button createButton()
      {
        final var button = new Button();
        GridPane.setHgrow(button, Priority.ALWAYS);
        GridPane.setVgrow(button, Priority.ALWAYS);
        GridPane.setHalignment(button, HPos.CENTER);
        GridPane.setValignment(button, VPos.CENTER);
        button.setPrefSize(999, 999); // fill
        button.getStyleClass().add("mainMenuButton");

        return button;
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    @Nonnull
    private ToggleButton createToggleButton (@Nonnull final PresentationModel pm, @Nonnull final List<String> baseStyleClass, @Nonnull final ToggleGroup group)
      {
        final var button = new ToggleButton();
        button.setToggleGroup(group);
        button.setText(pm.maybeAs(_Displayable_).map(Displayable::getDisplayName).orElse(""));
        button.getStyleClass().addAll(baseStyleClass);
        button.getStyleClass().addAll(pm.maybeAs(_Styleable_).map(Styleable::getStyles).orElse(emptyList()));
        pm.maybeAs(_UserActionProvider_).flatMap(UserActionProvider::getOptionalDefaultAction)
                                        .ifPresent(action -> bind(button, action));

        if (group.getSelectedToggle() == null)
          {
            group.selectToggle(button);
          }

        return button;
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    public static void enforceFxApplicationThread()
      {
        if (!Platform.isFxApplicationThread())
          {
            throw new IllegalStateException("Must run in the JavaFX Application Thread");
          }
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    private void bindEnableProperty (@Nonnull final BooleanProperty property1, @Nonnull final BoundProperty<Boolean> property2)
      {
        property1.setValue(!property2.get());
        property1.addListener((_1, _2, newValue) -> executor.execute(() -> property2.set(!newValue)));
        property2.addPropertyChangeListener(evt -> Platform.runLater(() -> property1.setValue(!(boolean)evt.getNewValue())));
      }
  }
