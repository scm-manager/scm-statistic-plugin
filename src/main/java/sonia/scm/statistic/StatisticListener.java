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
package sonia.scm.statistic;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.EagerSingleton;
import sonia.scm.HandlerEventType;
import com.github.legman.Subscribe;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.Changeset;
import sonia.scm.repository.PostReceiveRepositoryHookEvent;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryEvent;

import java.io.IOException;

/**
 * @author Sebastian Sdorra
 */
@Extension
@EagerSingleton
public class StatisticListener {
  private static final Logger LOG = LoggerFactory.getLogger(StatisticListener.class);

  @Inject
  public StatisticListener(StatisticManager statisticManager) {
    this.statisticManager = statisticManager;
  }

  @Subscribe
  public void onHookEvent(PostReceiveRepositoryHookEvent event) {
    LOG.debug("update statistic of repository {}", event.getRepository().getName());

    try (Statistics statistics = statisticManager.get(event.getRepository())) {
      for (Changeset c : event.getContext().getChangesetProvider().getChangesets()) {
        statistics.add(c);
      }
      statistics.commit();
    } catch (IOException ex) {
      LOG.error("could not update statistic", ex);
    }
  }

  @Subscribe
  public void onRepositoryEvent(RepositoryEvent event) {
    if (event.getEventType() == HandlerEventType.DELETE) {
      Repository repository = event.getItem();

      LOG.trace("receive delete event for repository {}",
        repository.getId());
      statisticManager.remove(repository);
    }

  }

  private StatisticManager statisticManager;
}
