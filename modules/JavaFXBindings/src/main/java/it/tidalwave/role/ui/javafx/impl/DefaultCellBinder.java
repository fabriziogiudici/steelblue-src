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
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Cell;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import it.tidalwave.util.As;
import it.tidalwave.util.AsException;
import it.tidalwave.util.NotFoundException;
import it.tidalwave.util.annotation.VisibleForTesting;
import it.tidalwave.role.ui.UserAction;
import it.tidalwave.role.ui.UserActionProvider;
import it.tidalwave.ui.role.javafx.CustomGraphicProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static java.util.stream.Collectors.toList;
import static it.tidalwave.role.ui.Displayable._Displayable_;
import static it.tidalwave.role.ui.Styleable._Styleable_;
import static it.tidalwave.role.ui.UserActionProvider._UserActionProvider_;
import static it.tidalwave.ui.role.javafx.CustomGraphicProvider._CustomGraphicProvider_;

/***********************************************************************************************************************
 *
 * An implementation of {@link CellBinder} that extracts information from a {@link UserActionProvider}.
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor @Slf4j
public class DefaultCellBinder implements CellBinder
  {
    private static final List<Class<?>> PRELOADING_ROLE_TYPES = Arrays.asList(
            _Displayable_, _UserActionProvider_, _Styleable_, _CustomGraphicProvider_);

    private static final String ROLE_STYLE_PREFIX = "-rs-";

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @RequiredArgsConstructor
    class EventHandlerUserActionAdapter implements EventHandler<ActionEvent>
      {
        @Nonnull
        private final UserAction action;

        @Override
        public void handle (final @Nonnull ActionEvent event)
          {
            executor.execute(action::actionPerformed);
          }
      }

    @Nonnull
    private final Executor executor;

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void bind (final @Nonnull Cell<?> cell, final @Nullable As item, final boolean empty)
      {
        log.trace("bind({}, {}, {})", cell, item, empty);

        clearBindings(cell);

        if (!empty && (item != null))
          {
            executor.execute(() ->
              {
                final RoleBag roles = new RoleBag(item, PRELOADING_ROLE_TYPES);

                Platform.runLater(() ->
                  {
                    bindTextAndGraphic(cell, roles);
                    bindDefaultAction(cell, roles);
                    bindContextMenu(cell, roles);
                    bindStyles(cell.getStyleClass(), roles);
                  });
              });
          }
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    private void clearBindings (final @Nonnull Cell<?> cell)
      {
        cell.setText("");
        cell.setGraphic(null);
        cell.setContextMenu(null);
        cell.setOnKeyPressed(null);
        cell.setOnMouseClicked(null);
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    private void bindTextAndGraphic (final @Nonnull Cell<?> cell, final @Nonnull RoleBag roles)
      {
        final Optional<CustomGraphicProvider> cgp = roles.get(_CustomGraphicProvider_);
        cell.setGraphic(cgp.map(role -> role.getGraphic()).orElse(null));
        cell.setText(cgp.map(c -> "").orElse(roles.get(_Displayable_).map(role -> role.getDisplayName()).orElse("")));
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    private void bindDefaultAction (final @Nonnull Cell<?> cell, final @Nonnull RoleBag roles)
      {
        try
          {
            final UserAction defaultAction = findDefaultUserAction(roles);

            // FIXME: doesn't work - keyevents are probably handled by ListView
            cell.setOnKeyPressed(event ->
              {
                log.debug("onKeyPressed: {}", event);
                if (event.getCode().equals(KeyCode.SPACE))
                  {
                    executor.execute(defaultAction::actionPerformed);
                  }
              });

            // FIXME: depends on mouse click, won't handle keyboard
            cell.setOnMouseClicked(event ->
              {
                if (event.getClickCount() == 2)
                  {
                    executor.execute(defaultAction::actionPerformed);
                  }
              });
          }
        catch (NotFoundException e)
          {
            log.trace("No default UserAction for {}", cell);
          }
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    private void bindContextMenu (final @Nonnull Cell<?> cell, final @Nonnull RoleBag roles)
      {
        final List<MenuItem> menuItems = createMenuItems(roles);
        cell.setContextMenu(menuItems.isEmpty() ? null : new ContextMenu(menuItems.toArray(new MenuItem[0])));
      }

    /*******************************************************************************************************************
     *
     * Don't directly return a ContextMenu otherwise it will be untestable.
     *
     ******************************************************************************************************************/
    @Nonnull
    @VisibleForTesting List<MenuItem> createMenuItems (final @Nonnull RoleBag roles)
      {
        try
          {
            final List<MenuItem> menuItems = new ArrayList<>();

            // FIXME: use flatMap() & collector as below - but doesn't work, I think it throws an uncaught Exception
//            return roles.getMany(UserActionProvider).stream()
//                                             .flatMap(uap -> uap.getActions().stream())
//                                             .map(action -> MenuItemBuilder.create()
//                                                                    .text(action.as(_Displayable_).getDisplayName())
//                                                                    .onAction(new EventHandlerUserActionAdapter(action))
//                                                                    .build())
//                                             .collect(toList());

            roles.getMany(_UserActionProvider_).stream().forEach(userActionProvider ->
              {
                userActionProvider.getActions().stream().forEach(action ->
                  {
                    final MenuItem menuItem = new MenuItem(action.as(_Displayable_).getDisplayName());
                    menuItem.setOnAction(new EventHandlerUserActionAdapter(action));
                    menuItems.add(menuItem);
                  });
              });

            return menuItems;
          }
        catch (AsException e)
          {
            return Collections.emptyList(); // ok, no context actions
          }
        catch (Exception e)
          {
            log.error("createMenuItems()", e);
            return Collections.emptyList();
          }
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public void bindStyles (final @Nonnull ObservableList<String> styleClasses, final @Nonnull RoleBag roles)
      {
        final List<String> styles = styleClasses.stream().filter(s -> !s.startsWith(ROLE_STYLE_PREFIX))
                                                         .collect(toList());
        // FIXME: shouldn't reset them? In case of cell reuse, they get accumulated
        styles.addAll(roles.getMany(_Styleable_).stream().flatMap(styleable -> styleable.getStyles().stream())
                                                         .map(s -> ROLE_STYLE_PREFIX + s)
                                                         .collect(toList()));
        styleClasses.setAll(styles);
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public static UserAction findDefaultUserAction (final @Nonnull RoleBag roles)
      throws NotFoundException
      {
        final Collection<UserActionProvider> userActionProviders = roles.getMany(_UserActionProvider_);
        log.trace(">>>> userActionProviders: {}", userActionProviders);

        for (final UserActionProvider userActionProvider : userActionProviders)
          {
           try
             {
               return userActionProvider.getDefaultAction();
             }
           catch (NotFoundException e)
             {
               // ok go on
             }
          }

        throw new NotFoundException();
      }
  }
