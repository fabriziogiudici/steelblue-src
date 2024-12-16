/*
 * *********************************************************************************************************************
 *
 * SteelBlue: DCI User Interfaces
 * http://tidalwave.it/projects/steelblue
 *
 * Copyright (C) 2015 - 2024 by Tidalwave s.a.s. (http://tidalwave.it)
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
 * git clone https://bitbucket.org/tidalwave/steelblue-src
 * git clone https://github.com/tidalwave-it/steelblue-src
 *
 * *********************************************************************************************************************
 */
package it.tidalwave.ui.role.javafx;

import javax.annotation.Nonnull;
import javafx.scene.Node;

/***********************************************************************************************************************
 *
 * A Role for data that provides their own {@link Node} for rendering.
 * 
 * @stereotype  Role
 * 
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
public interface CustomGraphicProvider 
  {
    public static final Class<CustomGraphicProvider> _CustomGraphicProvider_ = CustomGraphicProvider.class;
    
    @Nonnull
    public Node getGraphic();
  }
