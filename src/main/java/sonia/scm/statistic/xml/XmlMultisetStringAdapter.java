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

//~--- non-JDK imports --------------------------------------------------------

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

//~--- JDK imports ------------------------------------------------------------

import java.util.Set;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Sebastian Sdorra
 */
public class XmlMultisetStringAdapter
  extends XmlAdapter<XmlMultisetStringElement[], Multiset<String>>
{

  /**
   * Method description
   *
   *
   * @param set
   *
   * @return
   *
   * @throws Exception
   */
  @Override
  public XmlMultisetStringElement[] marshal(Multiset<String> set)
    throws Exception
  {
    Set<String> values = set.elementSet();
    XmlMultisetStringElement[] elements =
      new XmlMultisetStringElement[values.size()];
    int i = 0;

    for (String value : values)
    {
      elements[i] = new XmlMultisetStringElement(value, set.count(value));
      i++;
    }

    return elements;
  }

  /**
   * Method description
   *
   *
   * @param elements
   *
   * @return
   *
   * @throws Exception
   */
  @Override
  public Multiset<String> unmarshal(XmlMultisetStringElement[] elements)
    throws Exception
  {
    Multiset<String> multiset = HashMultiset.create();

    for (XmlMultisetStringElement e : elements)
    {
      multiset.add(e.getValue(), e.getCount());
    }

    return multiset;
  }
}
