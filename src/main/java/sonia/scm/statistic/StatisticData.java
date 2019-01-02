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

//~--- non-JDK imports --------------------------------------------------------

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
import sonia.scm.repository.Person;
import sonia.scm.repository.Repository;
import sonia.scm.repository.api.RepositoryServiceFactory;
import sonia.scm.statistic.xml.XmlMultisetDayAdapter;
import sonia.scm.statistic.xml.XmlMultisetIntegerAdapter;
import sonia.scm.statistic.xml.XmlMultisetStringAdapter;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;
import java.util.Calendar;
import java.util.Set;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Sebastian Sdorra
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "statistic-data")
public class StatisticData
{

  /** Field description */
  public static final String MODIFICATION_ADDED = "added";

  /** Field description */
  public static final String MODIFICATION_MODIFIED = "modified";

  /** Field description */
  public static final String MODIFICATION_REMOVED = "removed";

  /** Field description */
  static final int VERSION = 2;

  /**
   * the logger for StatisticData
   */
  private static final Logger logger =
    LoggerFactory.getLogger(StatisticData.class);

  /** Field description */
  private static final Set<String> PUNCTATION_MARKS = ImmutableSet.of(".", ",",
                                                        "!", "?", "[", "]",
                                                        "(", ")", "{", "}");

  @Inject Repository repository;
  @Inject RepositoryServiceFactory repositoryServiceFactory;

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   */
  public StatisticData()
  {
    changesets = Sets.newHashSet();
    commitsPerAuthor = HashMultiset.create();
    commitsPerDay = HashMultiset.create();
    commitsPerHour = HashMultiset.create();
    commitsPerWeekday = HashMultiset.create();
    modifiedFiles = HashMultiset.create();
    fileModificationCount = HashMultiset.create();
    wordCount = HashMultiset.create();
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param c
   *
   * @return
   */
  public StatisticData add(Changeset c)
  {
    if (!changesets.contains(c.getId()))
    {
      append(c);
    }
    else if (logger.isDebugEnabled())
    {
      logger.debug("statistic already contains changeset {}", c.getId());
    }

    return this;
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @return
   */
  public Set<String> getChangesets()
  {
    return changesets;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public Multiset<String> getCommitsPerAuthor()
  {
    return commitsPerAuthor;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public Multiset<Day> getCommitsPerDay()
  {
    return commitsPerDay;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public Multiset<Integer> getCommitsPerHour()
  {
    return commitsPerHour;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public Multiset<Integer> getCommitsPerWeekday()
  {
    return commitsPerWeekday;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public Multiset<String> getFileModificationCount()
  {
    return fileModificationCount;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public Multiset<String> getModifiedFiles()
  {
    return modifiedFiles;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public int getVersion()
  {
    return version;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public Multiset<String> getWordCount()
  {
    return wordCount;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param c
   */
  private void append(Changeset c)
  {
    changesets.add(c.getId());

    Person author = c.getAuthor();

    if ((author != null) &&!Strings.isNullOrEmpty(author.getName()))
    {
      commitsPerAuthor.add(author.getName());
    }

    Calendar cal = Calendar.getInstance();

    cal.setTimeInMillis(c.getDate());
    commitsPerWeekday.add(cal.get(Calendar.DAY_OF_WEEK));

    commitsPerDay.add(Day.of(cal));
    commitsPerHour.add(cal.get(Calendar.HOUR_OF_DAY));

    try {
      Modifications mods = repositoryServiceFactory.create(repository).getModificationsCommand().getModifications();

      for (String file : mods.getModified()) {
        modifiedFiles.add(file);
      }

      fileModificationCount.add(MODIFICATION_ADDED, mods.getAdded().size());
      fileModificationCount.add(MODIFICATION_MODIFIED, mods.getModified().size());
      fileModificationCount.add(MODIFICATION_REMOVED, mods.getRemoved().size());
    }
    catch(IOException ex) {
      logger.error("could not get modifications", ex);
    }
    // data version 2
    String description = c.getDescription();

    if (!Strings.isNullOrEmpty(description))
    {
      Iterable<String> words = split(description);

      for (String word : words)
      {
        appendWord(word);
      }
    }
  }

  /**
   * Method description
   *
   *
   * @param word
   */
  private void appendWord(String word)
  {
    for (String punctationMark : PUNCTATION_MARKS)
    {
      if (word.endsWith(punctationMark))
      {
        word = word.substring(0, word.length() - 1);
      }

      if (word.startsWith(punctationMark))
      {
        word = word.substring(1);
      }
    }

    if (word.length() > 0)
    {
      wordCount.add(word);
    }
  }

  /**
   * Method description
   *
   *
   * @param desc
   *
   * @return
   */
  private Iterable<String> split(String desc)
  {
    return Splitter.on(" ").omitEmptyStrings().trimResults().split(desc);
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  @XmlElement(name = "id")
  @XmlElementWrapper(name = "changesets")
  private Set<String> changesets;

  /** Field description */
  @XmlElement(name = "commits-per-author")
  @XmlJavaTypeAdapter(XmlMultisetStringAdapter.class)
  private Multiset<String> commitsPerAuthor;

  /** Field description */
  @XmlElement(name = "commits-per-day")
  @XmlJavaTypeAdapter(XmlMultisetDayAdapter.class)
  private Multiset<Day> commitsPerDay;

  /** Field description */
  @XmlElement(name = "commits-per-hour")
  @XmlJavaTypeAdapter(XmlMultisetIntegerAdapter.class)
  private Multiset<Integer> commitsPerHour;

  /** Field description */
  @XmlElement(name = "commits-per-weekday")
  @XmlJavaTypeAdapter(XmlMultisetIntegerAdapter.class)
  private Multiset<Integer> commitsPerWeekday;

  /** Field description */
  @XmlElement(name = "file-modification-count")
  @XmlJavaTypeAdapter(XmlMultisetStringAdapter.class)
  private Multiset<String> fileModificationCount;

  /** Field description */
  @XmlElement(name = "modified-files")
  @XmlJavaTypeAdapter(XmlMultisetStringAdapter.class)
  private Multiset<String> modifiedFiles;

  /** Field description */
  private int version = VERSION;

  /** Field description */
  @XmlElement(name = "word-count")
  @XmlJavaTypeAdapter(XmlMultisetStringAdapter.class)
  private Multiset<String> wordCount;
}
