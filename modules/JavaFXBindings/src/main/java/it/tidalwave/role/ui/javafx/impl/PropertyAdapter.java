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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import javafx.beans.InvalidationListener;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.application.Platform;
import it.tidalwave.role.ui.BoundProperty;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class PropertyAdapter<T> implements Property<T>
  {
    @Nonnull
    private final Executor executor;

    @Nonnull
    private final BoundProperty<T> delegate;

    // FIXME: WEAK LISTENER!!
    private final List<ChangeListener<? super T>> listeners = new ArrayList<>();

    private T boundValue;

    // FIXME: WEAK LISTENER!!
    private final PropertyChangeListener propertyChangeListener = new PropertyChangeListener()
      {
        @Override
        public void propertyChange (final @Nonnull PropertyChangeEvent evt)
          {
            log.trace("propertyChange({}) - bound value: {}", evt, boundValue);

            if (!Objects.equals(boundValue, evt.getNewValue()))
              {
                boundValue = (T)evt.getNewValue();
                Platform.runLater(new Runnable()
                  {
                    @Override
                    public void run()
                      {
                        for (final ChangeListener<? super T> listener : new ArrayList<>(listeners))
                          {
                            log.trace("fire changed({}, {}) to {}", evt.getOldValue(), evt.getNewValue(), listener);
                            listener.changed(PropertyAdapter.this, (T)evt.getOldValue(), (T)evt.getNewValue());
                          }
                      }
                  });
              }
          }
      };

    public PropertyAdapter (final @Nonnull Executor executor, final @Nonnull BoundProperty<T> delegate)
      {
        this.executor = executor;
        this.delegate = delegate;
        delegate.addPropertyChangeListener(propertyChangeListener);
      }

    @Override
    public T getValue()
      {
        return delegate.get();
      }

    @Override
    public void setValue (final T value)
      {
        log.trace("setValue({})", value);
        boundValue = value;

        if (!Objects.equals(value, delegate.get()))
          {
            executor.execute(new Runnable()
              {
                @Override
                public void run()
                  {
                    delegate.set(value);
                  }
              });
          }
      }

    @Override
    public void addListener (final @Nonnull ChangeListener<? super T> listener)
      {
        listeners.add(listener);
      }

    @Override
    public void removeListener (final @Nonnull ChangeListener<? super T> listener)
      {
        listeners.remove(listener);
      }

    @Override
    public void addListener(InvalidationListener listener)
      {
        log.warn("addListener({})", listener);
      }

    @Override
    public void removeListener(InvalidationListener listener)
      {
        log.warn("removeListener({})", listener);
      }

    @Override
    public void bind(ObservableValue<? extends T> observable)
      {
        log.warn("bind({})", observable);
        throw new UnsupportedOperationException("Not supported yet.");
      }

    @Override
    public void unbind()
      {
        log.warn("unbind()");
        throw new UnsupportedOperationException("Not supported yet.");
      }

    @Override
    public boolean isBound()
      {
        log.warn("isBound()");
        throw new UnsupportedOperationException("Not supported yet.");
      }

    @Override
    public void bindBidirectional(Property<T> other)
      {
        log.warn("bindBidirectional({})", other);
        throw new UnsupportedOperationException("Not supported yet.");
      }

    @Override
    public void unbindBidirectional(Property<T> other)
      {
        log.warn("unbindBidirectional({})", other);
        throw new UnsupportedOperationException("Not supported yet.");
      }

    @Override
    public Object getBean()
      {
        log.warn("getBean()");
        throw new UnsupportedOperationException("Not supported yet.");
      }

    @Override
    public String getName()
      {
        log.warn("getName()");
        throw new UnsupportedOperationException("Not supported yet.");
      }
  }
