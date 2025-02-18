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
package it.tidalwave.ui.core.role;

import javax.annotation.Nonnegative;
import jakarta.annotation.Nonnull;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/***************************************************************************************************************************************************************
 *
 * The role of an object that can provide an icon for rendering.
 *
 * @stereotype Role
 *
 * @since   2.0-ALPHA-1
 * @author  Fabrizio Giudici
 * @it.tidalwave.javadoc.draft
 *
 **************************************************************************************************************************************************************/
@FunctionalInterface
public interface IconProvider
  {
    public static final Class<IconProvider> _IconProvider_ = IconProvider.class;

    /***********************************************************************************************************************************************************
     * A default {@code IconProvider} with an empty icon.
     **********************************************************************************************************************************************************/
    public static final IconProvider DEFAULT = new IconProvider()
      {
        private final Icon EMPTY_ICON = new ImageIcon(new BufferedImage(16, 16, BufferedImage.TYPE_4BYTE_ABGR));

        @Override @Nonnull
        public Icon getIcon (@Nonnegative final int size)
          {
            return EMPTY_ICON;
          }
      };

    /***********************************************************************************************************************************************************
     * Returns the icon for this object. Note that the {@code size} parameter is just a hint to allow implementations
     * to pick the correctly sized icon in an optimized fashion. In particular, implementations should try to do their
     * best for providing an icon whose size is equal or greater than the requested one, but this is not guaranteed.
     * It's up to the client code to eventually resize the returned icon for its purposes.
     *
     * @param  requestedSize  the requested icon size
     * @return                the icon
     **********************************************************************************************************************************************************/
    @Nonnull
    public Icon getIcon (@Nonnegative int requestedSize);
  }
