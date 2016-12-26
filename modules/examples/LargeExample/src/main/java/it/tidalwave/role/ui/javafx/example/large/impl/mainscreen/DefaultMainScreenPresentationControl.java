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
package it.tidalwave.role.ui.javafx.example.large.impl.mainscreen;

import it.tidalwave.role.Aggregate;
import it.tidalwave.role.SimpleComposite;
import it.tidalwave.role.spi.ArrayListSimpleComposite;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import it.tidalwave.role.spi.DefaultDisplayable;
import it.tidalwave.role.ui.UserAction;
import it.tidalwave.role.ui.spi.UserActionSupport;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.role.ui.Selectable;
import it.tidalwave.role.ui.UserActionProvider;
import it.tidalwave.role.ui.spi.DefaultPresentationModel;
import it.tidalwave.util.NotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: $
 *
 **********************************************************************************************************************/
@Slf4j
public class DefaultMainScreenPresentationControl implements MainScreenPresentationControl
  {
    @Inject
    private MainScreenPresentation presentation;

    private PresentationModel listPm;

    private PresentationModel arrayPm;

    private final UserAction action = new UserActionSupport(new DefaultDisplayable("Press me")) // FIXME: refactor with UserAction8
      {
        @Override
        public void actionPerformed()
          {
            log.info("BUTTON PRESSED");
          }
      };

    @PostConstruct
    private void initialize()
      {
        createData();
        presentation.bind(action, listPm, arrayPm);
      }

    @Override
    public void start()
      {
        presentation.showUp();
      }

    private void createData()
      {
        final SimpleComposite<PresentationModel> composite = new ArrayListSimpleComposite<>(
                Arrays.asList("1", "2", "3").stream()
                        .map(name -> pmFor(sampleEntity(name)))
                        .collect(toList()));
        listPm = new DefaultPresentationModel(composite);

        final List<PresentationModel> pmRows = new ArrayList<>();

        for (int row = 0; row < 10; row++)
          {
            final Map<String, PresentationModel> map = new HashMap<>();

            for (int column = 1; column <= 2; column++)
              {
                final SampleEntity entity = sampleEntity(String.format("(%d ; %d)", row, column));
                map.put("C" + column, new DefaultPresentationModel(entity, entity));
              }

            final Aggregate<PresentationModel> aRow = new Aggregate<PresentationModel>()
              {
                @Override
                public PresentationModel getByName(String name) throws NotFoundException
                  {
                    if (!map.containsKey(name))
                      {
                        throw new NotFoundException("column: " + name);
                      }

                    return map.get(name);
                  }
              };

            pmRows.add(new DefaultPresentationModel(aRow));
          }

        final SimpleComposite<PresentationModel> c2 = new ArrayListSimpleComposite<>(pmRows);
        arrayPm = new DefaultPresentationModel(c2);
      }

    @Nonnull
    private static SampleEntity sampleEntity (final @Nonnull String name)
      {
        return new SampleEntity(name, new DefaultDisplayable(name));
      }

    @Nonnull
    private static PresentationModel pmFor (final @Nonnull Object owner)
      {
        final Selectable selectable = new Selectable()
          {
            @Override
            public void select()
              {
                log.info("SELECTED {}", owner);
              }
          };

        final UserAction selectionAction = new UserActionSupport(new DefaultDisplayable("Default action")) // FIXME: refactor with UserAction8
          {
            @Override
            public void actionPerformed()
              {
                log.info("DEFAULT ACTION {}", owner);
              }
          };

        final UserAction contextAction1 = new UserActionSupport(new DefaultDisplayable("Context action 1")) // FIXME: refactor with UserAction8
          {
            @Override
            public void actionPerformed()
              {
                log.info("CONTEXT ACTION 1 {}", owner);
              }
          };

        final UserAction contextAction2 = new UserActionSupport(new DefaultDisplayable("Context action 2")) // FIXME: refactor with UserAction8
          {
            @Override
            public void actionPerformed()
              {
                log.info("CONTEXT ACTION 2 {}", owner);
              }
          };

        return new DefaultPresentationModel(owner, selectable, actionProvider(selectionAction, contextAction1, contextAction2));
      }

    @Nonnull
    private static UserActionProvider actionProvider (final @Nonnull UserAction ... actions)
      {
        return new UserActionProvider()
          {
            @Override @Nonnull
            public Collection<? extends UserAction> getActions()
              {
                return Arrays.asList(actions);
              }

            @Override @Nonnull
            public UserAction getDefaultAction()
              {
                return actions[0];
              }
          };
      }
  }
