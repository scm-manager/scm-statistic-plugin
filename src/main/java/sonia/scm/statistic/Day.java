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
