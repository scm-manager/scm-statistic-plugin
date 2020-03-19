/**
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.repository.Repository;

import java.io.IOException;

/**
 * @author Sebastian Sdorra
 */
public class StatisticBootstrapWorker implements Runnable {

  static final String THREADNAME = "StatisticWorker";
  static final String THREADNAME_PATTERN = THREADNAME.concat("-%s");
  private static final Logger LOG = LoggerFactory.getLogger(StatisticBootstrapWorker.class);

  public StatisticBootstrapWorker(StatisticManager manager,
                                  Repository repository) {
    this.manager = manager;
    this.repository = repository;
  }

  @Override
  public void run() {
    try {

      manager.createStatistic(repository);
    } catch (IOException ex) {
      LOG.error(
        "could not create statistic for repository ".concat(
          repository.getName()), ex);
    }
  }

  private StatisticManager manager;
  private Repository repository;
}
