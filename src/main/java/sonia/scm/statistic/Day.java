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
 *mvn
 */

package sonia.scm.statistic;

import com.google.common.base.Objects;
import com.google.common.base.MoreObjects;

import java.util.Calendar;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Sebastian Sdorra
 */
@XmlRootElement(name = "day")
@XmlAccessorType(XmlAccessType.FIELD)
public class Day {

  public Day() {
  }

  public Day(int year, int month, int day) {
    this.year = year;
    this.month = month;
    this.day = day;
  }

  public static Day of(long time) {
    Calendar c = Calendar.getInstance();

    c.setTimeInMillis(time);

    return of(c);
  }

  public static Day of(Calendar c) {
    return new Day(
      c.get(Calendar.YEAR),
      c.get(Calendar.MONTH) + 1,
      c.get(Calendar.DAY_OF_MONTH)
    );
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }

    if (getClass() != obj.getClass()) {
      return false;
    }

    final Day other = (Day) obj;

    return Objects.equal(year, other.year)
      && Objects.equal(month, other.month)
      && Objects.equal(day, other.day);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(year, month, day);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("year", year)
      .add("month", month)
      .add("day", day)
      .toString();
  }

  public int getDay() {
    return day;
  }

  public String getDayString() {
    StringBuilder value = new StringBuilder();

    value.append(getMonthString()).append("-");
    append(value, day);

    return value.toString();
  }

  public int getMonth() {
    return month;
  }

  public String getMonthString() {
    StringBuilder value = new StringBuilder();

    value.append(year).append("-");
    append(value, month);

    return value.toString();
  }

  public int getYear() {
    return year;
  }

  public String getYearString() {
    return String.valueOf(year);
  }

  private void append(StringBuilder value, int nr) {
    if (nr < 10) {
      value.append("0");
    }

    value.append(nr);
  }

  private int day;
  private int month;
  private int year;
}
