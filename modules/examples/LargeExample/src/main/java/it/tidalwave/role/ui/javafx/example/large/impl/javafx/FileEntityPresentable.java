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
import java.util.Collection;
import it.tidalwave.role.ui.Displayable;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.role.ui.javafx.example.large.data.impl.FileEntity;
import it.tidalwave.dci.annotation.DciRole;
import it.tidalwave.role.ui.spi.SimpleCompositePresentable;
import static it.tidalwave.util.Parameters.r;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@DciRole(datumType = FileEntity.class)
public class FileEntityPresentable extends SimpleCompositePresentable<FileEntity>
  {
    @Nonnull
    private final FileEntity owner;

    public FileEntityPresentable (@Nonnull final FileEntity owner)
      {
        super(owner);
        this.owner = owner;
      }

    @Override @Nonnull
    public PresentationModel createPresentationModel (@Nonnull final Collection<Object> roles)
      {
        final Displayable displayable = Displayable.of(() -> "XX " + owner.getDisplayName());
        return super.createPresentationModel(r(displayable, roles));
      }
  }
