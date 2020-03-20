/*
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
package sonia.scm.statistic.xml;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import java.util.Set;
import java.util.regex.Pattern;
import javax.xml.bind.annotation.adapters.XmlAdapter;

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
