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

package sonia.scm.statistic.xml;

import com.google.common.base.Objects;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

/**
 * @author Sebastian Sdorra
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlMultisetStringElement {

  public XmlMultisetStringElement() {
  }

  public XmlMultisetStringElement(String value, int count) {
    this.value = value;
    this.count = count;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }

    if (getClass() != obj.getClass()) {
      return false;
    }

    final XmlMultisetStringElement other = (XmlMultisetStringElement) obj;

    return Objects.equal(value, other.value)
      && Objects.equal(count, other.count);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value, count);
  }

  public int getCount() {
    return count;
  }

  public String getValue() {
    return value;
  }

  private int count;
  private String value;
}
