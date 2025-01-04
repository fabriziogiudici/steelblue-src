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
package it.tidalwave.ui.core.role.spi;

import jakarta.annotation.Nonnull;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.Icon;
import it.tidalwave.ui.core.role.MutableIconProvider;

/***************************************************************************************************************************************************************
 *
 * A convenient support for implementing a {@link it.tidalwave.ui.core.role.MutableIconProvider}.
 *
 * @since   2.0-ALPHA-1
 * @author  Fabrizio Giudici
 * @it.tidalwave.javadoc.draft
 *
 **************************************************************************************************************************************************************/
public abstract class MutableIconProviderSupport implements MutableIconProvider
  {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    @Override
    public void addPropertyChangeListener (@Nonnull final PropertyChangeListener listener)
      {
        pcs.addPropertyChangeListener(listener);
      }

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    @Override
    public void removePropertyChangeListener (@Nonnull final PropertyChangeListener listener)
      {
        pcs.removePropertyChangeListener(listener);
      }

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     *
     * FIXME: this method does nothing. Probably this is inconsistent with DefaultMutableDisplayable? But that is
     * a Default*, we're just a *Support...
     **********************************************************************************************************************************************************/
    @Override
    public void setIcon (@Nonnull final Icon icon)
      {
      }

    /***********************************************************************************************************************************************************
     * Fires the event notifying that {@link #PROP_ICON} has been changed.
     *
     * @param  oldIcon   the old value of the property
     * @param  newIcon   the new value of the property
     **********************************************************************************************************************************************************/
    protected void fireIconChange (@Nonnull final Icon oldIcon, @Nonnull final Icon newIcon)
      {
        pcs.firePropertyChange(PROP_ICON, oldIcon, newIcon); // FIXME: should be in the EDT?
      }
  }
