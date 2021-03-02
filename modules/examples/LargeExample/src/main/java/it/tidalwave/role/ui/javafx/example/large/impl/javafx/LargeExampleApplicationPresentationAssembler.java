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
 *
 *
 * *********************************************************************************************************************
 * #L%
 */
package it.tidalwave.role.ui.javafx.example.large.impl.javafx;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import it.tidalwave.role.ui.javafx.ApplicationPresentationAssembler;
import it.tidalwave.role.ui.javafx.example.large.mainscreen.impl.javafx.JavaFXMainScreenPresentation;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * @version $Id: Class.java,v 631568052e17 2013/02/19 15:45:02 fabrizio $
 *
 **********************************************************************************************************************/
public class LargeExampleApplicationPresentationAssembler
        implements ApplicationPresentationAssembler<JavaFXApplicationPresentationDelegate>
  {
    @Inject
    private JavaFXMainScreenPresentation mainScreenPresentation;

    @Override
    public void assemble (final @Nonnull JavaFXApplicationPresentationDelegate delegate)
      {
        delegate.assemble(mainScreenPresentation.getNad().getNode());
      }
  }
