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
import com.google.common.collect.TreeMultiset;

import sonia.scm.statistic.dto.CommitsPerAuthor;
import sonia.scm.statistic.dto.CommitsPerHour;
import sonia.scm.statistic.dto.CommitsPerMonth;
import sonia.scm.statistic.dto.TopModifiedFiles;

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
    Multiset<String> authors = HashMultiset.create();
    int others = collectTopEntries(data.getCommitsPerAuthor(), authors, limit,
                   true);

    if (others > 0)
    {
      authors.add("others", others);
    }

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
  public static CommitsPerHour collectCommitsPerHour(StatisticData data)
  {
    Multiset<Integer> commitsPerHour = TreeMultiset.create();

    for (Entry<Integer> e : data.getCommitsPerHour().entrySet())
    {
      commitsPerHour.add(e.getElement(), e.getCount());
    }

    return new CommitsPerHour(commitsPerHour);
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

  /**
   * Method description
   *
   *
   * @param data
   *
   * @return
   */
  public static CommitsPerMonth collectCommitsPerYear(StatisticData data)
  {
    Multiset<String> commitsPerMonth = TreeMultiset.create();

    for (Entry<Day> e : data.getCommitsPerDay().entrySet())
    {
      commitsPerMonth.add(e.getElement().getYearString(), e.getCount());
    }

    return new CommitsPerMonth(commitsPerMonth);
  }

  /**
   * Method description
   *
   *
   * @param data
   * @param limit
   *
   * @return
   */
  public static TopModifiedFiles collectTopModifiedFiles(StatisticData data,
    int limit)
  {
    Multiset<String> files = HashMultiset.create();

    collectTopEntries(data.getModifiedFiles(), files, limit, false);

    return new TopModifiedFiles(files);
  }

  /**
   * Method description
   *
   *
   * @param source
   * @param target
   * @param limit
   * @param countOthers
   * @param <T>
   *
   * @return
   */
  private static <T> int collectTopEntries(Multiset<T> source,
    Multiset<T> target, int limit, boolean countOthers)
  {
    Multiset<T> ordered = Multisets.copyHighestCountFirst(source);

    int i = 0;
    int others = 0;

    for (Entry<T> e : ordered.entrySet())
    {
      if (i < limit)
      {
        target.add(e.getElement(), e.getCount());
        i++;
      }
      else if (countOthers)
      {
        others += e.getCount();
      }
      else
      {
        break;
      }
    }

    return others;
  }
}
