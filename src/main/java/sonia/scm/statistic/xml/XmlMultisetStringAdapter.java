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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import java.util.Set;
import java.util.regex.Pattern;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author Sebastian Sdorra
 */
public class XmlMultisetStringAdapter
  extends XmlAdapter<XmlMultisetStringElement[], Multiset<String>> {

  /**
   * valid char matcher for XML 1.0
   * #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]
   */
  private static final Pattern VALIDCHARS = Pattern.compile(
    "[^"
      + "\u0009\r\n"
      + "\u0020-\uD7FF"
      + "\uE000-\uFFFD"
      + "\ud800\udc00-\udbff\udfff"
      + "]"
  );

  @Override
  public XmlMultisetStringElement[] marshal(Multiset<String> set)
    throws Exception {
    Set<String> values = set.elementSet();
    XmlMultisetStringElement[] elements =
      new XmlMultisetStringElement[values.size()];
    int i = 0;

    for (String value : values) {
      elements[i] = new XmlMultisetStringElement(stripInvalidChars(value),
        set.count(value));
      i++;
    }

    return elements;
  }

  @Override
  public Multiset<String> unmarshal(XmlMultisetStringElement[] elements)
    throws Exception {
    Multiset<String> multiset = HashMultiset.create();

    for (XmlMultisetStringElement e : elements) {
      multiset.add(e.getValue(), e.getCount());
    }

    return multiset;
  }

  @VisibleForTesting
  String stripInvalidChars(String value) {
    return VALIDCHARS.matcher(Strings.nullToEmpty(value)).replaceAll("");
  }
}
