
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
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.VFS;
import org.objectweb.asm.signature.SignatureVisitor;
import org.objectweb.asm.signature.SignatureReader;

import java.io.IOException;
import java.io.File;
import java.net.URISyntaxException;
import java.util.Collections;

import com.pongasoft.kiwidoc.builder.model.LibraryModelBuilder;
import com.pongasoft.kiwidoc.builder.model.PackageModelBuilder;
import com.pongasoft.kiwidoc.builder.model.ClassModelBuilder;
import com.pongasoft.kiwidoc.model.resource.LibraryVersionResource;
import com.pongasoft.util.core.exception.InternalException;

/**
 * @author yan@pongasoft.com
 */
public class TestSourceCodeParser
{
  /**
   * Constructor
   */
  public TestSourceCodeParser()
  {
  }

  @Test
  public void testHappyPath() throws IOException, URISyntaxException, InternalException
  {
    LibraryModelBuilder libraryModelBuilder = new LibraryModelBuilder(new LibraryVersionResource("com.pongasoft", "com.pongasoft.kiwidoc.builder.doclet", "1.0"));

    SourceCodeParser sourceCodeParser = new SourceCodeParser();

    FileObject sources =
      VFS.getManager().toFileObject(new File("../com.pongasoft.kiwidoc.testdata/src/main/java"));

    sourceCodeParser.parseSources(libraryModelBuilder,
                                  sources,
                                  "overview.html",
                                  null,
                                  Collections.<FileObject>emptyList());

    for(PackageModelBuilder packageModelBuilder : libraryModelBuilder.getAllPackages())
    {
      for(ClassModelBuilder classModelBuilder : packageModelBuilder.getAllClasses())
      {
        System.out.println(classModelBuilder.buildModel().getGenericTypeVariables());
      }
    }

    //sourceCodeParser.parse(new File("../com.pongasoft.kiwidoc.testdata/src/main/java/com/pongasoft/kiwidoc/testdata/pubdir1/I1G.java"));
  }

  @Test
  public void testAsm()
  {
    //String sig = "<E:Ljava/lang/Number;F:Ljava/lang/Object;>Ljava/lang/Object;";
    //String sig = "<V:Ljava/lang/Object;>Lcom/pongasoft/kiwidoc/testdata/pubdir1/Pub1Class2<Ljava/lang/Integer;Ljava/lang/Float;>;Lcom/pongasoft/kiwidoc/testdata/pubdir1/Pub1Interface2<TV;>;";
    // String sig = "<K:Ljava/lang/Object;>(TK;Lorg/hamcrest/Matcher<TK;>;Ljava/util/Set<Ljava/lang/String;>;)V";
    //String sig ="<E:Ljava/lang/Object;F:Ljava/lang/Number;G:Ljava/lang/Number;:Ljava/lang/Comparable<Ljava/lang/Number;>;H:TE;I::Ljava/util/List<TE;>;J::Ljava/util/List<Ljava/lang/Number;>;K::Ljava/util/List<+Ljava/lang/Number;>;L::Ljava/util/List<-Ljava/lang/Number;>;M::Ljava/util/List<+Ljava/util/List<Ljava/lang/Number;>;>;>Ljava/lang/Object;";
    String sig ="<E:Ljava/lang/Object;F:Ljava/lang/Number;G:Ljava/lang/Number;:Ljava/lang/Comparable<Ljava/lang/Number;>;H:TE;I::Ljava/util/List<TE;>;J::Ljava/util/List<Ljava/lang/Number;>;K::Ljava/util/List<+Ljava/lang/Number;>;L::Ljava/util/List<-Ljava/lang/Number;>;M::Ljava/util/List<+Ljava/util/List<Ljava/lang/Number;>;>;N:Lcom/pongasoft/kiwidoc/testdata/pubdir1/C1$Inner1;:Lcom/pongasoft/kiwidoc/testdata/pubdir1/I1$Inner1;:Ljava/util/List<+Lcom/pongasoft/kiwidoc/testdata/pubdir1/C1$Inner1;>;>Ljava/lang/Object;";
    //String sig = "<K:Ljava/util/List<+Ljava/lang/Number;>;>Ljava/lang/Object;";
    //String sig = "Ljava/util/HashMap<TK;TV;>.HashIterator<TK;>;";
    SignatureReader reader = new SignatureReader(sig);

    reader.accept(new MySignatureVisitor(""));
  }

  private static class MySignatureVisitor implements SignatureVisitor
  {
    private final String _indent;

    private MySignatureVisitor(String indent)
    {
      _indent = indent;
    }

    private MySignatureVisitor(MySignatureVisitor visitor, String name)
    {
      _indent = visitor._indent + name + "$" + System.identityHashCode(this) + ".";
    }

    public void visitFormalTypeParameter(String s)
    {
      System.out.println(_indent + "visitFormalTypeParameter(" + s + ")");
    }

    public SignatureVisitor visitClassBound()
    {
      return new MySignatureVisitor(this, "visitClassBound");
    }

    public SignatureVisitor visitInterfaceBound()
    {
      return new MySignatureVisitor(this, "visitInterfaceBound");
    }

    public SignatureVisitor visitSuperclass()
    {
      return new MySignatureVisitor(this, "visitSuperclass");
    }

    public SignatureVisitor visitInterface()
    {
      return new MySignatureVisitor(this, "visitInterface");
    }

    public SignatureVisitor visitParameterType()
    {
      return new MySignatureVisitor(this, "visitParameterType");
    }

    public SignatureVisitor visitReturnType()
    {
      return new MySignatureVisitor(this, "visitReturnType");
    }

    public SignatureVisitor visitExceptionType()
    {
      return new MySignatureVisitor(this, "visitExceptionType");
    }

    public void visitBaseType(char c)
    {
      System.out.println(_indent + "visitBaseType(" + c + ")");
    }

    public void visitTypeVariable(String s)
    {
      System.out.println(_indent + "visitTypeVariable(" + s + ")");
    }

    public SignatureVisitor visitArrayType()
    {
      return new MySignatureVisitor(this, "visitClassBound");
    }

    public void visitClassType(String s)
    {
      System.out.println(_indent + "visitClassType(" + s + ")");
    }

    public void visitInnerClassType(String s)
    {
      System.out.println(_indent + "visitInnerClassType(" + s + ")");
    }

    public void visitTypeArgument()
    {
      System.out.println(_indent + "visitTypeArgument()");
    }

    public SignatureVisitor visitTypeArgument(char c)
    {
      return new MySignatureVisitor(this, "visitTypeArgument(" + c + ")");
    }

    public void visitEnd()
    {
      System.out.println(_indent + "end.");
    }
  }
}
