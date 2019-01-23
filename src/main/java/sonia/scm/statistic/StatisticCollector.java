/**
 * Copyright (c) 2010, Sebastian Sdorra
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of SCM-Manager; nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://bitbucket.org/sdorra/scm-manager
 *
 */

package sonia.scm.statistic;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;
import com.google.common.collect.Multisets;
import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultiset;
import com.google.common.primitives.Ints;
import sonia.scm.statistic.dto.CommitsPerAuthor;
import sonia.scm.statistic.dto.CommitsPerHour;
import sonia.scm.statistic.dto.CommitsPerMonth;
import sonia.scm.statistic.dto.CommitsPerWeekday;
import sonia.scm.statistic.dto.CommitsPerYear;
import sonia.scm.statistic.dto.FileModificationCount;
import sonia.scm.statistic.dto.TopModifiedFiles;
import sonia.scm.statistic.dto.TopWords;


import java.util.Calendar;

/**
 * @author Sebastian Sdorra
 */
public class StatisticCollector {

  private StatisticCollector() {}

  public static CommitsPerAuthor collectCommitsPerAuthor(StatisticData data,
                                                         int limit) {
    Multiset<String> authors = HashMultiset.create();
    int others = collectTopEntries(data.getCommitsPerAuthor(), authors, limit,
      true);

    if (others > 0) {
      authors.add("others", others);
    }

    return new CommitsPerAuthor(authors);
  }

  public static CommitsPerHour collectCommitsPerHour(StatisticData data) {
    Multiset<Integer> commitsPerHour = TreeMultiset.create();

    for (Entry<Integer> e : data.getCommitsPerHour().entrySet()) {
      commitsPerHour.add(e.getElement(), e.getCount());
    }

    return new CommitsPerHour(commitsPerHour);
  }

  public static CommitsPerMonth collectCommitsPerMonth(StatisticData data) {
    Multiset<String> commitsPerMonth = TreeMultiset.create();

    for (Entry<Day> e : data.getCommitsPerDay().entrySet()) {
      commitsPerMonth.add(e.getElement().getMonthString(), e.getCount());
    }

    return new CommitsPerMonth(commitsPerMonth);
  }

  public static CommitsPerWeekday collectCommitsPerWeekday(StatisticData data) {
    Multiset<String> weekdays = LinkedHashMultiset.create();
    Ordering<Entry<Integer>> intOrdering = new Ordering<Entry<Integer>>() {
      @Override
      public int compare(Entry<Integer> left, Entry<Integer> right) {
        return Ints.compare(left.getElement(), right.getElement());
      }
    };

    Multiset<Integer> weekdaysInt = data.getCommitsPerWeekday();

    for (Entry<Integer> e :
      intOrdering.immutableSortedCopy(weekdaysInt.entrySet())) {
      weekdays.add(getWeekdayAsString(e.getElement()), e.getCount());
    }

    return new CommitsPerWeekday(weekdays);
  }

  public static CommitsPerYear collectCommitsPerYear(StatisticData data) {
    Multiset<String> commitsPerYear = TreeMultiset.create();

    for (Entry<Day> e : data.getCommitsPerDay().entrySet()) {
      commitsPerYear.add(e.getElement().getYearString(), e.getCount());
    }

    return new CommitsPerYear(commitsPerYear);
  }

  public static FileModificationCount collectFileModificationCount(
    StatisticData data) {
    return new FileModificationCount(data.getFileModificationCount());
  }

  public static TopModifiedFiles collectTopModifiedFiles(StatisticData data,
                                                         int limit) {
    Multiset<String> files = HashMultiset.create();

    collectTopEntries(data.getModifiedFiles(), files, limit, false);

    return new TopModifiedFiles(files);
  }

  public static TopWords collectTopWords(StatisticData data, int limit) {
    Multiset<String> words = HashMultiset.create();

    collectTopEntries(data.getWordCount(), words, limit, false);

    return new TopWords(words);
  }

  public static TopWords collectTopWords(StatisticData data,
                                         Predicate<String> predicate, int limit) {
    Multiset<String> words = HashMultiset.create();

    collectTopEntries(data.getWordCount(), words, limit, false, predicate);

    return new TopWords(words);
  }

  private static <T> int collectTopEntries(Multiset<T> source,
                                           Multiset<T> target, int limit, boolean countOthers) {
    Predicate<T> predicate = Predicates.alwaysTrue();

    return collectTopEntries(source, target, limit, countOthers, predicate);
  }

  private static <T> int collectTopEntries(Multiset<T> source,
                                           Multiset<T> target, int limit, boolean countOthers, Predicate<T> predicate) {
    Multiset<T> ordered = Multisets.copyHighestCountFirst(source);

    int i = 0;
    int others = 0;

    for (Entry<T> e : ordered.entrySet()) {
      if (predicate.apply(e.getElement())) {
        if (i < limit) {
          target.add(e.getElement(), e.getCount());
          i++;
        } else if (countOthers) {
          others += e.getCount();
        } else {
          break;
        }
      }
    }

    return others;
  }

  private static String getWeekdayAsString(int weekday) {
    String label = "unknown";

    switch (weekday) {
      case Calendar.MONDAY:
        label = "Monday";
        break;

      case Calendar.TUESDAY:
        label = "Tuesday";
        break;

      case Calendar.WEDNESDAY:
        label = "Wednesday";
        break;

      case Calendar.THURSDAY:
        label = "Thursday";
        break;

      case Calendar.FRIDAY:
        label = "Friday";
        break;

      case Calendar.SATURDAY:
        label = "Saturday";
        break;

      case Calendar.SUNDAY:
        label = "Sunday";
        break;
      default: break;
    }

    return label;
  }
}
