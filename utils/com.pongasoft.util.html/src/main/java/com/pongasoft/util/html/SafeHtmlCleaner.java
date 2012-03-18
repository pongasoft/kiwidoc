
/*
 * Copyright (c) 2012 Yan Pujante
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.pongasoft.util.html;

import org.ccil.cowan.tagsoup.XMLWriter;
import org.ccil.cowan.tagsoup.Parser;
import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.AttributesImpl;

import java.io.Writer;
import java.io.StringWriter;
import java.io.StringReader;
import java.io.IOException;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

/**
 * Makes html safe by removing unsafe tags and attributes and properly balancing the unclosed tags. 
 *
 * @author yan@pongasoft.com
 */
public class SafeHtmlCleaner extends XMLWriter
{
  public static final Set<String> FORBIDDEN_TAGS =
    new HashSet<String>(Arrays.asList("html", "body", "head", "title", "meta", "script"));

  public static final Set<String> FORBIDDEN_ATTRIBUTES =
    new HashSet<String>(Arrays.asList("onload", "onunload", "onclick", "ondblclick", "onmousedown",
                                      "onmouseup", "onmouseover", "onmousemove", "onmouseout",
                                      "onfocus", "onblur", "onkeypress", "onkeydown", "onkeyup",
                                      "onsubmit", "onreset", "onselect", "onchange"));

  public SafeHtmlCleaner()
  {
  }

  public SafeHtmlCleaner(Writer writer)
  {
    super(writer);
  }

  public SafeHtmlCleaner(XMLReader xmlReader)
  {
    super(xmlReader);
  }

  public SafeHtmlCleaner(XMLReader xmlReader, Writer writer)
  {
    super(xmlReader, writer);
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes atts)
    throws SAXException
  {
    if(!FORBIDDEN_TAGS.contains(qName.toLowerCase()))
      super.startElement(uri, localName, qName, filterAttributes(atts));
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException
  {
    if(!FORBIDDEN_TAGS.contains(qName.toLowerCase()))
      super.endElement(uri, localName, qName);
  }

  private Attributes filterAttributes(Attributes atts)
  {
    if(atts == null)
      return null;

    int len = atts.getLength();

    boolean hasForbiddenAttribute = false;

    for(int i = 0; i < len; i++)
    {
      String name = atts.getLocalName(i);
      if(FORBIDDEN_ATTRIBUTES.contains(name.toLowerCase()))
      {
        hasForbiddenAttribute = true;
        break;
      }
    }

    if(hasForbiddenAttribute)
    {
      AttributesImpl newAttributes = new AttributesImpl(atts);

      for(int i = 0; i < newAttributes.getLength(); i++)
      {
        String name = newAttributes.getLocalName(i);
        if(FORBIDDEN_ATTRIBUTES.contains(name.toLowerCase()))
        {
          newAttributes.removeAttribute(i);
          i--; // we just removed an attribute
        }
      }

      atts = newAttributes;
    }

    return atts;
  }

  public static String cleanHtml(String html)
    throws SAXException, SAXNotRecognizedException, IOException
  {
    Parser parser = new Parser();
    parser.setFeature(Parser.restartElementsFeature, false);
    parser.setFeature(Parser.defaultAttributesFeature, false);
    parser.setFeature(Parser.namespacesFeature, false);

    StringWriter sw = new StringWriter();

    SafeHtmlCleaner cleaner = new SafeHtmlCleaner(sw);

    cleaner.setOutputProperty(XMLWriter.METHOD, "html");
    cleaner.setOutputProperty(XMLWriter.OMIT_XML_DECLARATION, "yes");
    parser.setContentHandler(cleaner);
    parser.parse(new InputSource(new StringReader(html)));

    return sw.toString().trim();
  }
}
