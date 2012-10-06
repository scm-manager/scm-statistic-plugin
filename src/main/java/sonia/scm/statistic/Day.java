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

import com.google.common.base.Objects;

//~--- JDK imports ------------------------------------------------------------

import java.util.Calendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Sebastian Sdorra
 */
@XmlRootElement(name = "day")
@XmlAccessorType(XmlAccessType.FIELD)
public class Day
{

  /**
   * Constructs ...
   *
   */
  public Day() {}

  /**
   * Constructs ...
   *
   *
   * @param date
   */
  public Day(long date)
  {
    Calendar c = Calendar.getInstance();

    c.setTimeInMillis(date);
    year = c.get(Calendar.YEAR);
    month = c.get(Calendar.MONTH) + 1;
    day = c.get(Calendar.DAY_OF_MONTH);
  }

  /**
   * Constructs ...
   *
   *
   * @param year
   * @param month
   * @param day
   */
  public Day(int year, int month, int day)
  {
    this.year = year;
    this.month = month;
    this.day = day;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param obj
   *
   * @return
   */
  @Override
  public boolean equals(Object obj)
  {
    if (obj == null)
    {
      return false;
    }

    if (getClass() != obj.getClass())
    {
      return false;
    }

    final Day other = (Day) obj;

    //J-
    return Objects.equal(year, other.year)
        && Objects.equal(month, other.month)
        && Objects.equal(day, other.day);
    //J+
  }

  /**
   * Method description
   *
   *
   * @return
   */
  @Override
  public int hashCode()
  {
    return Objects.hashCode(year, month, day);
  }

  /**
   * Method description
   *
   *
   * @return
   */
  @Override
  public String toString()
  {
    //J-
    return Objects.toStringHelper(this)
                  .add("year", year)
                  .add("month", month)
                  .add("day", day)
                  .toString();
    //J+
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @return
   */
  public int getDay()
  {
    return day;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String getDayString()
  {
    StringBuilder value = new StringBuilder();

    value.append(getMonthString()).append("-");
    append(value, day);

    return value.toString();
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public int getMonth()
  {
    return month;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String getMonthString()
  {
    StringBuilder value = new StringBuilder();

    value.append(year).append("-");
    append(value, month);

    return value.toString();
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public int getYear()
  {
    return year;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String getYearString()
  {
    return String.valueOf(year);
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param value
   * @param nr
   */
  private void append(StringBuilder value, int nr)
  {
    if (nr < 10)
    {
      value.append("0");
    }

    value.append(nr);
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private int day;

  /** Field description */
  private int month;

  /** Field description */
  private int year;
}
