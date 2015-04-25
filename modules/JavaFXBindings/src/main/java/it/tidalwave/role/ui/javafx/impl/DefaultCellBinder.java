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
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Cell;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.MenuItemBuilder;
import javafx.scene.input.KeyCode;
import com.google.common.annotations.VisibleForTesting;
import it.tidalwave.util.As;
import it.tidalwave.util.AsException;
import it.tidalwave.util.NotFoundException;
import it.tidalwave.role.ui.UserAction;
import it.tidalwave.role.ui.UserActionProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static java.util.stream.Collectors.toList;
import static it.tidalwave.role.Displayable.Displayable;
import static it.tidalwave.role.ui.Styleable.Styleable;
import static it.tidalwave.role.ui.UserActionProvider.UserActionProvider;
import it.tidalwave.ui.role.javafx.CustomGraphicProvider;
import static it.tidalwave.ui.role.javafx.CustomGraphicProvider.CustomGraphicProvider;
import java.util.Optional;

/***********************************************************************************************************************
 *
 * An implementation of {@link CellBinder} that extracts information from a {@link UserActionProvider}.
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor @Slf4j
public class DefaultCellBinder implements CellBinder
  {
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
            executor.execute(() -> action.actionPerformed());
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
        log.debug("bind({}, {}, {})", cell, item, empty);

        clearBindings(cell);
            
        if (!empty && (item != null))
          {
            final RoleMap roleMap = new RoleMap(); // FIXME
            preLoadRoles(item, roleMap);
            bindTextAndGraphic(cell, roleMap);
            bindDefaultAction(cell, roleMap);
            bindContextMenu(cell, roleMap);
            bindStyles(cell.getStyleClass(), roleMap);
          }
      }
    
    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    private void preLoadRoles (final @Nonnull As item, final @Nonnull RoleMap roleMap)
      {
        roleMap.putMany(CustomGraphicProvider, item.asMany(CustomGraphicProvider));
        roleMap.putMany(Displayable, item.asMany(Displayable));
        roleMap.putMany(UserActionProvider, item.asMany(UserActionProvider));
        roleMap.putMany(Styleable, item.asMany(Styleable));
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
    private void bindTextAndGraphic (final @Nonnull Cell<?> cell, final @Nonnull RoleMap roleMap) 
      {
        final Optional<CustomGraphicProvider> cgp = roleMap.get(CustomGraphicProvider);
        cell.setGraphic(cgp.map(role -> role.getGraphic()).orElse(null));
        
        if (!cgp.isPresent())
          {
            cell.setText(roleMap.get(Displayable).map(role -> role.getDisplayName()).orElse(""));
          }
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    private void bindDefaultAction (final @Nonnull Cell<?> cell, final @Nonnull RoleMap roleMap) 
      {
        try
          {
            final UserAction defaultAction = findDefaultUserAction(roleMap);
            
            // FIXME: doesn't work - keyevents are probably handled by ListView
            cell.setOnKeyPressed(event ->
              {
                log.debug("onKeyPressed: {}", event);
                if (event.getCode().equals(KeyCode.SPACE))
                  {
                    executor.execute(() -> defaultAction.actionPerformed());
                  }
              });
            
            // FIXME: depends on mouse click, won't handle keyboard
            cell.setOnMouseClicked(event ->
              {
                if (event.getClickCount() == 2)
                  {
                    executor.execute(() -> defaultAction.actionPerformed());
                  }
              });
          }
        catch (NotFoundException e)
          {
            log.debug("No default UserAction for {}: {}", cell, e.getMessage());
          }
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    private void bindContextMenu (final @Nonnull Cell<?> cell, final @Nonnull RoleMap roleMap) 
      {
        final List<MenuItem> menuItems = createMenuItems(roleMap);
        cell.setContextMenu(menuItems.isEmpty() ? null : new ContextMenu(menuItems.toArray(new MenuItem[0])));
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    @VisibleForTesting List<MenuItem> createMenuItems (final @Nonnull RoleMap roleMap)
      {
        try
          {
            final List<MenuItem> menuItems = new ArrayList<>();

            // FIXME: use flatMap() & collector
            roleMap.getMany(UserActionProvider).stream().forEach(userActionProvider -> 
              {
                userActionProvider.getActions().stream().forEach(action -> 
                  {
                    menuItems.add(MenuItemBuilder.create().text(action.as(Displayable).getDisplayName())
                                                          .onAction(new EventHandlerUserActionAdapter(action))
                                                          .build());
                  });
              });

            return menuItems;
          }
        catch (AsException e)
          {
            return null; // ok, no context actions
          }
      }
    
    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public void bindStyles (final @Nonnull ObservableList<String> styleClasses, final @Nonnull RoleMap roleMap)
      {
        final List<String> styles = styleClasses.stream().filter(s -> !s.startsWith(ROLE_STYLE_PREFIX))
                                                         .collect(toList());
        // FIXME: shouldn't reset them? In case of cell reuse, they get accumulated
        styles.addAll(roleMap.getMany(Styleable).stream().flatMap(styleable -> styleable.getStyles().stream())
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
    public static UserAction findDefaultUserAction (final @Nonnull RoleMap roleMap)
      throws NotFoundException
      {
        final Collection<UserActionProvider> userActionProviders = roleMap.getMany(UserActionProvider);
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
