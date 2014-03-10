/**
 * Copyright (c) 2010, Sebastian Sdorra All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. Neither the name of SCM-Manager;
 * nor the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://bitbucket.org/sdorra/scm-manager
 *
 */



package sonia.scm.statistic.xml;

//~--- non-JDK imports --------------------------------------------------------

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.hamcrest.Matchers.*;

import static org.junit.Assert.*;

//~--- JDK imports ------------------------------------------------------------

import java.io.File;
import java.io.IOException;

import java.util.Arrays;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Sebastian Sdorra
 */
public class XmlMultisetStringAdapterTest
{

  /**
   * Method description
   *
   *
   * @throws Exception
   */
  @Test
  public void testMarshall() throws Exception
  {
    Multiset<String> counter = HashMultiset.create();

    counter.add("abc");
    counter.add("abc");
    counter.add("cba");

    XmlMultisetStringElement[] els = adapter.marshal(counter);

    //J-
    assertThat(
      Arrays.asList(els),
      containsInAnyOrder(
        new XmlMultisetStringElement("abc", 2),
        new XmlMultisetStringElement("cba", 1)
      )
    );
    //J+
  }

  /**
   * Method description
   *
   *
   * @throws IOException
   */
  @Test
  public void testMarshallUnmarshall() throws IOException
  {

    MarshallUnmarshall mu = new MarshallUnmarshall();
    Multiset<String> counter = HashMultiset.create();

    counter.add("abc");
    counter.add("abc");
    counter.add("cba");
    mu.multiset = counter;

    File file = tempFolder.newFile();

    JAXB.marshal(mu, file);

    MarshallUnmarshall umu = JAXB.unmarshal(file, MarshallUnmarshall.class);

    assertEquals(2, umu.multiset.count("abc"));
    assertEquals(1, umu.multiset.count("cba"));
  }

  /**
   * Method description
   *
   *
   * @throws IOException
   */
  @Test
  public void testMarshallUnmarshallWithInvalidChars() throws IOException
  {

    MarshallUnmarshall mu = new MarshallUnmarshall();
    Multiset<String> counter = HashMultiset.create();

    counter.add("\u0000abc\u0001");
    counter.add("abc");
    mu.multiset = counter;

    File file = tempFolder.newFile();

    JAXB.marshal(mu, file);

    MarshallUnmarshall umu = JAXB.unmarshal(file, MarshallUnmarshall.class);

    assertEquals(2, umu.multiset.count("abc"));
  }

  /**
   * Method description
   *
   */
  @Test
  public void testStripInvalidChars()
  {
    String withInvalidChars = "\u0000Abc\u0001";

    assertEquals("Abc", adapter.stripInvalidChars(withInvalidChars));

    String validChars = "Hello World!";

    assertEquals("Hello World!", adapter.stripInvalidChars(validChars));
  }

  /**
   * Method description
   *
   *
   * @throws Exception
   */
  @Test
  public void testUnmarshall() throws Exception
  {
    XmlMultisetStringElement[] els = new XmlMultisetStringElement[] {
                                       new XmlMultisetStringElement("abc", 4),
      new XmlMultisetStringElement("cba", 2), };

    Multiset<String> set = adapter.unmarshal(els);

    assertEquals(4, set.count("abc"));
    assertEquals(2, set.count("cba"));
  }

  //~--- inner classes --------------------------------------------------------

  /**
   * Class description
   *
   *
   * @version        Enter version here..., 14/03/10
   * @author         Enter your name here...    
   */
  @XmlRootElement
  @XmlAccessorType(XmlAccessType.FIELD)
  private static class MarshallUnmarshall
  {

    /** Field description */
    @XmlElement
    @XmlJavaTypeAdapter(XmlMultisetStringAdapter.class)
    private Multiset<String> multiset;
  }


  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private final XmlMultisetStringAdapter adapter =
    new XmlMultisetStringAdapter();

  /** Field description */
  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();
}
