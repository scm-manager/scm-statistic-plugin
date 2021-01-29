/*
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
