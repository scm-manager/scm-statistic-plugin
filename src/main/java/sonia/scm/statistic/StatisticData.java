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

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.repository.Changeset;
import sonia.scm.repository.Modifications;
import sonia.scm.repository.Modified;
import sonia.scm.repository.Person;
import sonia.scm.statistic.xml.XmlMultisetDayAdapter;
import sonia.scm.statistic.xml.XmlMultisetIntegerAdapter;
import sonia.scm.statistic.xml.XmlMultisetStringAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Calendar;
import java.util.Set;

/**
 * @author Sebastian Sdorra
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "statistic-data")
public class StatisticData {

  public static final String MODIFICATION_ADDED = "added";
  public static final String MODIFICATION_MODIFIED = "modified";
  public static final String MODIFICATION_REMOVED = "removed";
  static final int VERSION = 2;
  private static final Logger LOG = LoggerFactory.getLogger(StatisticData.class);
  private static final Set<String> PUNCTATION_MARKS = ImmutableSet.of(".", ",",
    "!", "?", "[", "]",
    "(", ")", "{", "}");

  public StatisticData() {
    changesets = Sets.newHashSet();
    commitsPerAuthor = HashMultiset.create();
    commitsPerDay = HashMultiset.create();
    commitsPerHour = HashMultiset.create();
    commitsPerWeekday = HashMultiset.create();
    modifiedFiles = HashMultiset.create();
    fileModificationCount = HashMultiset.create();
    wordCount = HashMultiset.create();
  }

  public StatisticData add(Changeset c, Modifications mods) {
    if (!changesets.contains(c.getId())) {
      append(c, mods);
    } else if (LOG.isDebugEnabled()) {
      LOG.debug("statistic already contains changeset {}", c.getId());
    }

    return this;
  }

  public Set<String> getChangesets() {
    return changesets;
  }

  public Multiset<String> getCommitsPerAuthor() {
    return commitsPerAuthor;
  }

  public Multiset<Day> getCommitsPerDay() {
    return commitsPerDay;
  }

  public Multiset<Integer> getCommitsPerHour() {
    return commitsPerHour;
  }

  public Multiset<Integer> getCommitsPerWeekday() {
    return commitsPerWeekday;
  }

  public Multiset<String> getFileModificationCount() {
    return fileModificationCount;
  }

  public Multiset<String> getModifiedFiles() {
    return modifiedFiles;
  }

  public int getVersion() {
    return version;
  }

  public Multiset<String> getWordCount() {
    return wordCount;
  }

  private void append(Changeset c, Modifications mods) {
    changesets.add(c.getId());

    Person author = c.getAuthor();

    if ((author != null) && !Strings.isNullOrEmpty(author.getName())) {
      commitsPerAuthor.add(author.getName());
    }

    Calendar cal = Calendar.getInstance();

    cal.setTimeInMillis(c.getDate());
    commitsPerWeekday.add(cal.get(Calendar.DAY_OF_WEEK));

    commitsPerDay.add(Day.of(cal));
    commitsPerHour.add(cal.get(Calendar.HOUR_OF_DAY));

    mods.getModified().stream().map(Modified::getPath).forEach(modifiedFiles::add);

    fileModificationCount.add(MODIFICATION_ADDED, mods.getAdded().size());
    fileModificationCount.add(MODIFICATION_MODIFIED, mods.getModified().size());
    fileModificationCount.add(MODIFICATION_REMOVED, mods.getRemoved().size());

    // data version 2
    String description = c.getDescription();

    if (!Strings.isNullOrEmpty(description)) {
      Iterable<String> words = split(description);

      for (String word : words) {
        appendWord(word);
      }
    }
  }

  private void appendWord(String word) {
    for (String punctationMark : PUNCTATION_MARKS) {
      if (word.endsWith(punctationMark)) {
        word = word.substring(0, word.length() - 1);
      }

      if (word.startsWith(punctationMark)) {
        word = word.substring(1);
      }
    }

    if (word.length() > 0) {
      wordCount.add(word);
    }
  }

  private Iterable<String> split(String desc) {
    return Splitter.on(" ").omitEmptyStrings().trimResults().split(desc);
  }

  @XmlElement(name = "id")
  @XmlElementWrapper(name = "changesets")
  private Set<String> changesets;

  @XmlElement(name = "commits-per-author")
  @XmlJavaTypeAdapter(XmlMultisetStringAdapter.class)
  private Multiset<String> commitsPerAuthor;

  @XmlElement(name = "commits-per-day")
  @XmlJavaTypeAdapter(XmlMultisetDayAdapter.class)
  private Multiset<Day> commitsPerDay;

  @XmlElement(name = "commits-per-hour")
  @XmlJavaTypeAdapter(XmlMultisetIntegerAdapter.class)
  private Multiset<Integer> commitsPerHour;

  @XmlElement(name = "commits-per-weekday")
  @XmlJavaTypeAdapter(XmlMultisetIntegerAdapter.class)
  private Multiset<Integer> commitsPerWeekday;

  @XmlElement(name = "file-modification-count")
  @XmlJavaTypeAdapter(XmlMultisetStringAdapter.class)
  private Multiset<String> fileModificationCount;

  @XmlElement(name = "modified-files")
  @XmlJavaTypeAdapter(XmlMultisetStringAdapter.class)
  private Multiset<String> modifiedFiles;

  private int version = VERSION;

  @XmlElement(name = "word-count")
  @XmlJavaTypeAdapter(XmlMultisetStringAdapter.class)
  private Multiset<String> wordCount;
}
