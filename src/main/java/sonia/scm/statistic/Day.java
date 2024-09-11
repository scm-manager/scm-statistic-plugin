/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

package sonia.scm.statistic;

import com.google.common.base.Objects;
import com.google.common.base.MoreObjects;

import java.util.Calendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

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
