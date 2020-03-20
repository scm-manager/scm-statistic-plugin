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

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import sonia.scm.statistic.Day;

import java.util.Set;
import javax.xml.bind.annotation.adapters.XmlAdapter;

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
