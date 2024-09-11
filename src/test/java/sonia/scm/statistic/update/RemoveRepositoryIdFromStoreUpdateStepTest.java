/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

package sonia.scm.statistic.update;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.statistic.StatisticData;
import sonia.scm.store.DataStore;
import sonia.scm.store.DataStoreFactory;
import sonia.scm.update.RepositoryUpdateIterator;

import java.util.function.Consumer;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoveRepositoryIdFromStoreUpdateStepTest {

  private static final String REPOSITORY_ID = "42";

  @Mock
  private RepositoryUpdateIterator repositoryUpdateIterator;
  @Mock(answer = Answers.CALLS_REAL_METHODS)
  private DataStoreFactory storeFactory;
  @Mock
  private DataStore store;

  @InjectMocks
  private RemoveRepositoryIdFromStoreUpdateStep updateStep;

  @BeforeEach
  void mockStore() {
    doAnswer(invocation -> {
      invocation.getArgument(0, Consumer.class).accept(REPOSITORY_ID);
      return null;
    }).when(repositoryUpdateIterator).forEachRepository(any());
    when(storeFactory.getStore(argThat(parameter ->
      parameter.getRepositoryId().equals(REPOSITORY_ID)
        && parameter.getName().equals("statistic")
        && parameter.getType().equals(StatisticData.class)))).thenReturn(store);
  }

  @Test
  void shouldMoveStoreEntry() {
    StatisticData statisticData = new StatisticData();
    when(store.getOptional(REPOSITORY_ID)).thenReturn(of(statisticData));

    updateStep.doUpdate();

    verify(store).remove(REPOSITORY_ID);
    verify(store).put("statistic", statisticData);
  }

  @Test
  void shouldNotFailForRepositoryWithoutStatistics() {
    when(store.getOptional(REPOSITORY_ID)).thenReturn(empty());

    updateStep.doUpdate();

    verify(store, never()).remove(any());
    verify(store, never()).put(any(), any());
  }
}
