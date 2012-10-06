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

import sonia.scm.repository.Changeset;
import sonia.scm.statistic.xml.XmlMultisetDayAdapter;
import sonia.scm.statistic.xml.XmlMultisetStringAdapter;

//~--- JDK imports ------------------------------------------------------------

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
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

  /**
   * Constructs ...
   *
   */
  public StatisticData()
  {
    commitsPerAuthor = HashMultiset.create();
    commitsPerDay = HashMultiset.create();
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
    commitsPerAuthor.add(c.getAuthor().getName());
    commitsPerDay.add(new Day(c.getDate()));

    return this;
  }

  //~--- get methods ----------------------------------------------------------

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

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  @XmlElement(name = "commits-per-author")
  @XmlJavaTypeAdapter(XmlMultisetStringAdapter.class)
  private Multiset<String> commitsPerAuthor;

  /** Field description */
  @XmlElement(name = "commits-per-day")
  @XmlJavaTypeAdapter(XmlMultisetDayAdapter.class)
  private Multiset<Day> commitsPerDay;
}
