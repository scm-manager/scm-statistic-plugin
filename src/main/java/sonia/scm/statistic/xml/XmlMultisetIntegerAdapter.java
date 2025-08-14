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

import java.util.Set;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author Sebastian Sdorra
 */
public class XmlMultisetIntegerAdapter
  extends XmlAdapter<XmlMultisetIntegerElement[], Multiset<Integer>> {

  @Override
  public XmlMultisetIntegerElement[] marshal(Multiset<Integer> set)
    throws Exception {
    Set<Integer> values = set.elementSet();
    XmlMultisetIntegerElement[] elements =
      new XmlMultisetIntegerElement[values.size()];
    int i = 0;

    for (Integer v : values) {
      elements[i] = new XmlMultisetIntegerElement(v, set.count(v));
      i++;
    }

    return elements;
  }

  @Override
  public Multiset<Integer> unmarshal(XmlMultisetIntegerElement[] elements)
    throws Exception {
    Multiset<Integer> multiset = HashMultiset.create();

    for (XmlMultisetIntegerElement e : elements) {
      multiset.add(e.getValue(), e.getCount());
    }

    return multiset;
  }
}
