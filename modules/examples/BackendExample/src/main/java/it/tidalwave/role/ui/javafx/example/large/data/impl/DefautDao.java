/*
 * *********************************************************************************************************************
 *
 * SteelBlue: DCI User Interfaces
 * http://tidalwave.it/projects/steelblue
 *
 * Copyright (C) 2015 - 2023 by Tidalwave s.a.s. (http://tidalwave.it)
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
package it.tidalwave.role.ui.javafx.example.large.data.impl;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import it.tidalwave.role.ui.javafx.example.large.data.*;
import static java.util.stream.Collectors.toList;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 *
 **********************************************************************************************************************/
public class DefautDao implements Dao // FIXME: rename to Model
  {
    @Override @Nonnull
    public Collection<SimpleEntity> getSimpleEntities()
      {
        return IntStream.rangeClosed(1, 1000).mapToObj(Integer::toString).map(SimpleEntity::new).collect(toList());
      }

    @Override @Nonnull
    public Collection<SimpleDciEntity> getDciEntities()
      {
        final AtomicInteger sequence = new AtomicInteger(1); // I know it's bad, it's just an example
        return IntStream.rangeClosed(1, 1000).mapToObj(row ->
          {
            final int attr1 = sequence.getAndIncrement();
            final int attr2 = sequence.getAndIncrement();
            final String name = String.format("(%d ; %d)", attr1, attr2);
            return new SimpleDciEntity(name, attr1, attr2);
          }).collect(toList());
      }

    @Override @Nonnull
    public Collection<FileEntity> getFiles()
      {
        return List.of(FileEntity.of(Path.of("/")));
      }
  }
