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
package sonia.scm.statistic.xml;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import java.util.Set;
import javax.xml.bind.annotation.adapters.XmlAdapter;

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
