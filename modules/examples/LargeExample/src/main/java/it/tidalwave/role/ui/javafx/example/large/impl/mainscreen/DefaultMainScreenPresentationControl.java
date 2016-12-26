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

import it.tidalwave.role.SimpleComposite;
import it.tidalwave.role.spi.ArrayListSimpleComposite;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import it.tidalwave.role.spi.DefaultDisplayable;
import it.tidalwave.role.ui.UserAction;
import it.tidalwave.role.ui.spi.UserActionSupport;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.role.ui.spi.DefaultPresentationModel;
import java.util.Arrays;
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
        presentation.bind(action, listPm);
      }

    @Override
    public void start()
      {
        presentation.showUp();
      }

    private void createData()
      {
        final SimpleComposite<Object> composite = new ArrayListSimpleComposite<>(
                Arrays.asList("1", "2", "3").stream().map(name ->
                        pmFor(new SampleEntity(new DefaultDisplayable(name)))).collect(toList()));
        listPm = new DefaultPresentationModel(null, composite);
      }

    @Nonnull
    private static PresentationModel pmFor (final @Nonnull Object owner)
      {
        return new DefaultPresentationModel(owner);
      }
  }
