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

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import sonia.scm.statistic.Day;

import java.util.Set;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author Sebastian Sdorra
 */
public class XmlMultisetDayAdapter
  extends XmlAdapter<XmlMultisetDayElement[], Multiset<Day>> {

  @Override
  public XmlMultisetDayElement[] marshal(Multiset<Day> set) throws Exception {
    Set<Day> days = set.elementSet();
    XmlMultisetDayElement[] elements = new XmlMultisetDayElement[days.size()];
    int i = 0;

    for (Day d : days) {
      elements[i] = new XmlMultisetDayElement(d, set.count(d));
      i++;
    }

    return elements;
  }

  @Override
  public Multiset<Day> unmarshal(XmlMultisetDayElement[] elements)
    throws Exception {
    Multiset<Day> multiset = HashMultiset.create();

    for (XmlMultisetDayElement e : elements) {
      multiset.add(e.getDay(), e.getCount());
    }

    return multiset;
  }
}
