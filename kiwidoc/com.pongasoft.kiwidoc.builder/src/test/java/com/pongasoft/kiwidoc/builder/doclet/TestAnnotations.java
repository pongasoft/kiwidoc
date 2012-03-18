
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

package com.pongasoft.kiwidoc.builder.doclet;

import org.junit.Test;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import com.pongasoft.kiwidoc.testdata.pubdir1.I1G;

/**
 * @author yan@pongasoft.com
 */
public class TestAnnotations
{
  /**
   * Constructor
   */
  public TestAnnotations()
  {
  }

  @Test
  public void testAsm() throws IOException
  {
    InputStream is = readClass(I1G.class);
    try
    {
      ClassReader reader = new ClassReader(new BufferedInputStream(is));
      MyClassVisitor classParser = new MyClassVisitor();
      reader.accept(classParser,
                    ClassReader.SKIP_CODE + ClassReader.SKIP_DEBUG + ClassReader.SKIP_FRAMES);
    }
    finally
    {
      is.close();
    }
  }

  private static String computePath(Class<?> c)
  {
    return c.getName().replace(".", File.separator) + ".class";
  }

  private static InputStream readClass(Class<?> c) throws IOException
  {
    URL url = Thread.currentThread().getContextClassLoader().
      getResource(computePath(c));

    URLConnection urlConnection = url.openConnection();
    urlConnection.setUseCaches(false);
    urlConnection.connect();
    return urlConnection.getInputStream();
  }

  public class MyAnnotationVisitor implements AnnotationVisitor
  {
    private final String _indent;

    public MyAnnotationVisitor()
    {
      this("");
    }

    public MyAnnotationVisitor(String indent)
    {
      _indent = indent;
    }

    public void visit(String name, Object value)
    {
      System.out.println(_indent + "visit: " + name + " | " + value + " | " + value.getClass().getName());
    }

    public void visitEnum(String name, String desc, String value)
    {
      System.out.println(_indent + "visitEnum: " + name + " | " + desc + " | " + value);
    }

    public AnnotationVisitor visitAnnotation(String name, String desc)
    {
      System.out.println(_indent + "visitAnnotation: " + name + " | " + desc);
      return new MyAnnotationVisitor(_indent + "  ");
    }

    public AnnotationVisitor visitArray(String name)
    {
      System.out.println(_indent + "visitArray: " + name);
      return new MyAnnotationVisitor(_indent + "  ");
    }

    public void visitEnd()
    {
      //System.out.println("visitEnd.");
    }
  }

  /**
   * @author yan@pongasoft.com
     */
  public class MyClassVisitor implements ClassVisitor
  {
    /**
     * Constructor
     */
    public MyClassVisitor()
    {
    }

    public void visit(int version,
                      int access,
                      String name,
                      String signature,
                      String superName,
                      String[] interfaces)
    {
      System.out.println(name + " => " + signature);
    }

    public void visitSource(String source, String debug)
    {
    }

    public void visitOuterClass(String owner, String name, String desc)
    {
    }

    public AnnotationVisitor visitAnnotation(String desc, boolean visible)
    {
      System.out.println(desc + "/" + visible);
      return new MyAnnotationVisitor();
    }

    public void visitAttribute(Attribute attr)
    {
    }

    public void visitInnerClass(String name, String outerName, String innerName, int access)
    {
    }

    public FieldVisitor visitField(int access,
                                   String name,
                                   String desc,
                                   String signature,
                                   Object value)
    {
//    _classDesc.addField(new FieldDesc(access, name, desc, signature, value));
      return null;
    }

    public MethodVisitor visitMethod(int access,
                                     String name,
                                     String desc,
                                     String signature,
                                     String[] exceptions)
    {
      //System.out.println(name + " | " + desc + " | " + signature);
//    _classDesc.addMethod(new MethodDesc(access, name, desc, signature, exceptions));
      return null;
    }

    public void visitEnd()
    {
    }
  }
}