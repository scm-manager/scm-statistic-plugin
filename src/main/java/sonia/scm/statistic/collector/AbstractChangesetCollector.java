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
package sonia.scm.statistic.collector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.repository.Changeset;
import sonia.scm.repository.ChangesetPagingResult;
import sonia.scm.repository.InternalRepositoryException;
import sonia.scm.repository.api.LogCommandBuilder;
import sonia.scm.statistic.Statistics;

import java.io.IOException;

/**
 * @author Sebastian Sdorra
 */
public abstract class AbstractChangesetCollector implements ChangesetCollector {

  private static final Logger LOG = LoggerFactory.getLogger(AbstractChangesetCollector.class);
  private final Statistics statistics;

  protected AbstractChangesetCollector(Statistics statistics) {
    this.statistics = statistics;
  }

  protected void append(LogCommandBuilder logCommand, Statistics statistics, int pageSize)
    throws IOException, InternalRepositoryException {

    // do not cache the whole changesets of a repository
    ChangesetPagingResult result = logCommand.setDisableCache(true)
      .setPagingStart(0)
      .setPagingLimit(pageSize)
      .getChangesets();

    append(statistics, result);

    int total = result.getTotal();

    for (int i = pageSize; i < total; i = i + pageSize) {
      result = logCommand.setPagingStart(i)
        .setPagingLimit(pageSize)
        .getChangesets();

      append(statistics, result);
    }
  }

  private void append(Statistics statistics, ChangesetPagingResult result) throws IOException {
    for (Changeset c : result) {
      statistics.add(c);
    }
  }
}
