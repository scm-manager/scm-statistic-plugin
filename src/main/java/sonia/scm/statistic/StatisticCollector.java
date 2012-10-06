/**
 * Copyright (c) 2010, Sebastian Sdorra All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. Neither the name of SCM-Manager;
 * nor the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://bitbucket.org/sdorra/scm-manager
 *
 */



package sonia.scm.statistic;

//~--- non-JDK imports --------------------------------------------------------

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;
import com.google.common.collect.Multisets;
import com.google.common.collect.SortedMultiset;
import com.google.common.collect.TreeMultiset;

import sonia.scm.statistic.dto.CommitsPerAuthor;
import sonia.scm.statistic.dto.CommitsPerMonth;

/**
 *
 * @author Sebastian Sdorra
 */
public class StatisticCollector
{

  /**
   * Method description
   *
   *
   * @param data
   * @param limit
   *
   * @return
   */
  public static CommitsPerAuthor collectCommitsPerAuthor(StatisticData data,
    int limit)
  {
    Multiset<String> ordered =
      Multisets.copyHighestCountFirst(data.getCommitsPerAuthor());
    Multiset<String> authors = HashMultiset.create();

    int i = 0;
    int others = 0;

    for (Entry<String> e : ordered.entrySet())
    {
      if (i < limit)
      {
        authors.add(e.getElement(), e.getCount());
        i++;
      }
      else
      {
        others += e.getCount();
      }
    }

    authors.add("others", others);

    return new CommitsPerAuthor(authors);
  }

  /**
   * Method description
   *
   *
   * @param data
   *
   * @return
   */
  public static CommitsPerMonth collectCommitsPerMonth(StatisticData data)
  {
    Multiset<String> commitsPerMonth = TreeMultiset.create();

    for (Entry<Day> e : data.getCommitsPerDay().entrySet())
    {
      commitsPerMonth.add(e.getElement().getMonthString(), e.getCount());
    }

    return new CommitsPerMonth(commitsPerMonth);
  }
}
