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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.MenuItem;
import it.tidalwave.util.As;
import it.tidalwave.role.ContextManager;
import it.tidalwave.role.spi.DefaultContextManagerProvider;
import it.tidalwave.role.spi.DefaultDisplayable;
import it.tidalwave.role.ui.UserAction;
import it.tidalwave.role.ui.UserActionProvider;
import it.tidalwave.role.ui.spi.DefaultUserActionProvider;
import it.tidalwave.role.ui.spi.UserActionSupport;
import it.tidalwave.util.AsException;
import it.tidalwave.util.spi.AsDelegateProvider;
import it.tidalwave.util.spi.EmptyAsDelegateProvider;
import lombok.Delegate;
import lombok.RequiredArgsConstructor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class UserActionProviderContextMenuBuilderTest
  {
    @RequiredArgsConstructor
    static class TestExecutorService implements ExecutorService
      {
        public final List<AssertionError> assertionErrors = new ArrayList<>();

        static interface Exclusions
          {
            public Future<?> submit (Runnable runnable);
          }

        @Delegate(excludes = Exclusions.class)
        private final ExecutorService delegate;

        @Override @Nonnull
        public Future<?> submit (final Runnable task)
          {
            return delegate.submit(new Runnable()
              {
                @Override
                public void run()
                  {
                    try
                      {
                        task.run();
                      }
                    catch (AssertionError e)
                      {
                        assertionErrors.add(e);
                      }
                  }
            }  );
          }
      }

    private DefaultCellActionBinder fixture;

    private List<UserAction> actions;

    private As asObjectWithoutUserActionProvider;

    private As asObjectWithUserActionProvider;

    private TestExecutorService executorService;

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @BeforeMethod
    public void setupFixture()
      {
        AsDelegateProvider.Locator.set(new EmptyAsDelegateProvider());
        ContextManager.Locator.set(new DefaultContextManagerProvider()); // TODO: possibly drop this
        actions = new ArrayList<>();

        for (int i = 0; i < 10; i++)
          {
            actions.add(spy(new UserActionSupport(new DefaultDisplayable("Action #" + i))
              {
                @Override
                public void actionPerformed()
                  {
                    assertThat("Must not be in the FX thread!", Platform.isFxApplicationThread(), is(false));
                  }
              }));
          }

        final UserActionProvider userActionProvider = new DefaultUserActionProvider()
          {
            @Override @Nonnull
            public Collection<? extends UserAction> getActions()
              {
                return actions;
              }
          };

        // FIXME: use MockAs
        asObjectWithoutUserActionProvider = mock(As.class);
        asObjectWithUserActionProvider = mock(As.class);
        when(asObjectWithoutUserActionProvider.as(eq(UserActionProvider.class))).thenThrow(new AsException(UserActionProvider.class));
        when(asObjectWithoutUserActionProvider.asMany(eq(UserActionProvider.class))).thenReturn(new ArrayList<UserActionProvider>());
        when(asObjectWithUserActionProvider.as(eq(UserActionProvider.class))).thenReturn(userActionProvider);
        when(asObjectWithUserActionProvider.asMany(eq(UserActionProvider.class))).thenReturn(Arrays.asList(userActionProvider));

        executorService = new TestExecutorService(Executors.newSingleThreadExecutor());

        fixture = new DefaultCellActionBinder(executorService);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test
    public void must_return_null_when_UserActionProvider_is_not_present()
      {
        final List<MenuItem> menuItems = fixture.createMenuItems(asObjectWithoutUserActionProvider);

        assertThat(menuItems, is(nullValue()));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test
    public void must_set_the_MenuItem_text_from_UserAction_Displayable()
      throws InterruptedException
      {
        final List<MenuItem> menuItems = fixture.createMenuItems(asObjectWithUserActionProvider);

        assertThat(menuItems, is(not(nullValue())));
        assertThat(menuItems.size(), is(actions.size()));

        for (int i = 0; i < menuItems.size(); i++)
          {
            final MenuItem menuItem = menuItems.get(i);
            assertThat(menuItem, is(not(nullValue())));
            assertThat(menuItem.getText(), is("Action #" + i));
          }
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test
    public void must_invoke_callbacks_in_a_non_FX_thread()
      throws InterruptedException
      {
        final List<MenuItem> menuItems = fixture.createMenuItems(asObjectWithUserActionProvider);

        assertThat(menuItems, is(not(nullValue())));
        assertThat(menuItems.size(), is(actions.size()));

        for (int i = 0; i < menuItems.size(); i++)
          {
            final MenuItem menuItem = menuItems.get(i);
            menuItem.getOnAction().handle(new ActionEvent());
          }

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        for (int i = 0; i < menuItems.size(); i++)
          {
            verify(actions.get(i), times(1)).actionPerformed();
//            verifyNoMoreInteractions(actions.get(i));
          }

        if (!executorService.assertionErrors.isEmpty())
          {
            throw executorService.assertionErrors.get(0);
          }
      }
  }