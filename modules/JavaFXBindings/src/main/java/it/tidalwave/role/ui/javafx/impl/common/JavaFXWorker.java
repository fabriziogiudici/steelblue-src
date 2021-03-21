/*
 * #%L
 * *********************************************************************************************************************
 *
 * SteelBlue
 * http://steelblue.tidalwave.it - git clone git@bitbucket.org:tidalwave/steelblue-src.git
 * %%
 * Copyright (C) 2015 - 2021 Tidalwave s.a.s. (http://tidalwave.it)
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
package it.tidalwave.role.ui.javafx.impl.common;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Supplier;
import it.tidalwave.role.SimpleComposite;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.role.ui.javafx.impl.util.Logging;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import lombok.experimental.UtilityClass;
import static it.tidalwave.role.SimpleComposite._SimpleComposite_;
import static it.tidalwave.role.ui.javafx.impl.util.Logging.INDENT;
import static java.util.Collections.emptyList;
import static javafx.collections.FXCollections.observableArrayList;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@UtilityClass
public class JavaFXWorker
  {
    public static <T> void run (@Nonnull final Executor executor,
                                @Nonnull Supplier<T> backgroundSupplier,
                                @Nonnull Consumer<T> javaFxFinalizer)
      {
        executor.execute(() ->
                         {
                           final T value = backgroundSupplier.get();
                           Platform.runLater(() -> javaFxFinalizer.accept(value));
                         });
      }

    @Nonnull
    public static ObservableList<PresentationModel> childrenPm (@Nonnull final PresentationModel pm)
      {
        return childrenPm(pm, 0);
      }

    @Nonnull
    public static ObservableList<PresentationModel> childrenPm (@Nonnull final PresentationModel pm,
                                                                @Nonnegative int recursion)
      {
        final String indent = INDENT.substring(0, recursion * 8);
        final Optional<SimpleComposite> composite = pm.maybeAs(_SimpleComposite_);
        composite.ifPresent(c -> Logging.logObject(indent, composite));
        final List items = composite.map(c -> c.findChildren().results()).orElse(emptyList());
        Logging.logObjects(indent, items);
        return observableArrayList(items);
      }
  }

