/**
 * Copyright (c) 2010, Sebastian Sdorra
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of SCM-Manager; nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://bitbucket.org/sdorra/scm-manager
 *
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
