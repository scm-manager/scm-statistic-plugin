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

package sonia.scm.statistic.dto;

import com.google.common.collect.Multiset;
import sonia.scm.statistic.xml.XmlMultisetStringAdapter;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import sonia.scm.statistic.StatisticData;

/**
 * @author Sebastian Sdorra
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "commits-per-author")
public class CommitsPerAuthor {

  public CommitsPerAuthor() {
  }

  public CommitsPerAuthor(Multiset<String> commitsPerAuthor) {
    this.commitsPerAuthor = commitsPerAuthor;
  }

  public CommitsPerAuthor(StatisticData data) {
    this.commitsPerAuthor = data.getCommitsPerAuthor();
  }

  public Multiset<String> getCommitsPerAuthor() {
    return commitsPerAuthor;
  }

  @XmlElement(name = "author")
  @XmlJavaTypeAdapter(XmlMultisetStringAdapter.class)
  private Multiset<String> commitsPerAuthor;

}
