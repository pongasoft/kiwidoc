
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
import com.pongasoft.kiwidoc.builder.serializer.SerializerException;
import com.pongasoft.kiwidoc.builder.serializer.type.GenericTypeVariablesSerializer;
import com.pongasoft.kiwidoc.builder.serializer.type.TypeDecoder;
import com.pongasoft.kiwidoc.builder.serializer.type.TypeEncoder;
import com.pongasoft.kiwidoc.model.type.GenericTypeVariables;
import com.pongasoft.kiwidoc.model.type.GenericTypeVariable;
import com.pongasoft.kiwidoc.model.type.GenericType;
import com.pongasoft.kiwidoc.model.type.GenericVariable;
import com.pongasoft.kiwidoc.model.type.GenericBoundedWildcardType;
import com.pongasoft.kiwidoc.model.type.TypePart;
import com.pongasoft.kiwidoc.model.type.Type;
import com.pongasoft.kiwidoc.model.resource.ClassResource;
import static junit.framework.Assert.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author yan@pongasoft.com
 */
public class TestSerializers
{
  /**
   * Constructor
   */
  public TestSerializers()
  {
  }

  @Test
  public void testGenericTypeVariablesSerializer() throws SerializerException
  {
    String sig="<E:Ljava/lang/Object;" +
               "F:Ljava/lang/Number;" +
               "G:Ljava/lang/Number;:Ljava/lang/Comparable<Ljava/lang/Number;>;" +
               "H:TE;" +
               "I:Ljava/util/List<TE;>;" +
               "J:Ljava/util/List<Ljava/lang/Number;>;" +
               "K:Ljava/util/List<+Ljava/lang/Number;>;" +
               "L:Ljava/util/List<-Ljava/lang/Number;>;" +
               "M:Ljava/util/List<+Ljava/util/List<Ljava/lang/Number;>;>;" +
               "N:Lcom/pongasoft/kiwidoc/testdata/pubdir1/C1<Ljava/lang/Number;>.Inner1<Ljava/lang/String;>;:Lcom/pongasoft/kiwidoc/testdata/pubdir1/I1.Inner1;:Ljava/util/List<+Lcom/pongasoft/kiwidoc/testdata/pubdir1/C1<Ljava/lang/Number;>.Inner1<Ljava/lang/String;>;>;>";

    GenericTypeVariablesSerializer gtvs = new GenericTypeVariablesSerializer();
    GenericTypeVariables typeVariables = gtvs.deserialize(null, sig);
    List<GenericTypeVariable> list = typeVariables.getGenericTypeVariables();
    assertEquals(10, list.size());
    assertEquals(sig, gtvs.serialize(typeVariables));
    checkGenericTypeVariables(list);
  }

  @Test
  public void testMethodGenericTypeVariables()
  {
    String sig="<E:Ljava/lang/Object;" +
               "F:Ljava/lang/Number;" +
               "G:Ljava/lang/Number;:Ljava/lang/Comparable<Ljava/lang/Number;>;" +
               "H:TE;" +
               "I:Ljava/util/List<TE;>;" +
               "J:Ljava/util/List<Ljava/lang/Number;>;" +
               "K:Ljava/util/List<+Ljava/lang/Number;>;" +
               "L:Ljava/util/List<-Ljava/lang/Number;>;" +
               "M:Ljava/util/List<+Ljava/util/List<Ljava/lang/Number;>;>;" +
               "N:Lcom/pongasoft/kiwidoc/testdata/pubdir1/C1<Ljava/lang/Number;>.Inner1<Ljava/lang/String;>;:Lcom/pongasoft/kiwidoc/testdata/pubdir1/I1.Inner1;:Ljava/util/List<+Lcom/pongasoft/kiwidoc/testdata/pubdir1/C1<Ljava/lang/Number;>.Inner1<Ljava/lang/String;>;>;>()V";

    TypeDecoder.MethodSignature methodSignature = new TypeDecoder().decodeMethodSignature(sig);
    checkGenericTypeVariables(methodSignature.getGenericTypeVariables().getGenericTypeVariables());
  }

  /**
   * In the bytecode when there is no generics, it uses $ instead of .
   */
  @Test
  public void testInnerClasses()
  {
    // with no generics
    String sig =
      "Lcom/pongasoft/kiwidoc/testdata/pubdir1/Pub1Class4$Inner2Pub1Class4$InnerInner2Pub1Class4;";
    GenericType type = (GenericType) new TypeDecoder().decodeType(sig);
    assertEquals(new ClassResource("com.pongasoft.kiwidoc.testdata.pubdir1.Pub1Class4$Inner2Pub1Class4$InnerInner2Pub1Class4"),
                 type.getClassResource());
    assertEquals(sig.replace('$', '.'), type.toErasureDescriptor());
    assertEquals(3, type.getTypeParts().size());

    // part 0
    TypePart typePart = type.getTypeParts().get(0);
    assertEquals(new ClassResource("com.pongasoft.kiwidoc.testdata.pubdir1.Pub1Class4"),
                 typePart.getClassResource());
    assertEquals(0, typePart.getGenerics().size());
    assertEquals("com.pongasoft.kiwidoc.testdata.pubdir1.Pub1Class4", typePart.getName());

    // part 1
    typePart = type.getTypeParts().get(1);
    assertEquals(new ClassResource("com.pongasoft.kiwidoc.testdata.pubdir1.Pub1Class4$Inner2Pub1Class4"),
                 typePart.getClassResource());
    assertEquals(0, typePart.getGenerics().size());
    assertEquals("Inner2Pub1Class4", typePart.getName());

    // part 2
    typePart = type.getTypeParts().get(2);
    assertEquals(new ClassResource("com.pongasoft.kiwidoc.testdata.pubdir1.Pub1Class4$Inner2Pub1Class4$InnerInner2Pub1Class4"),
                 typePart.getClassResource());
    assertEquals(0, typePart.getGenerics().size());
    assertEquals("InnerInner2Pub1Class4", typePart.getName());

    assertEquals(sig.replace('$', '.'), new TypeEncoder().encodeType(type));

    // with generics
    sig=
      "Lcom/pongasoft/kiwidoc/testdata/pubdir1/Pub1Class4<Ljava/lang/String;>.Inner3Pub1Class4.InnerInner3Pub1Class4<Ljava/lang/Double;>;";

    type = (GenericType) new TypeDecoder().decodeType(sig);
    assertEquals(new ClassResource("com.pongasoft.kiwidoc.testdata.pubdir1.Pub1Class4$Inner3Pub1Class4$InnerInner3Pub1Class4"),
                 type.getClassResource());
    assertEquals("Lcom/pongasoft/kiwidoc/testdata/pubdir1/Pub1Class4.Inner3Pub1Class4.InnerInner3Pub1Class4;", type.toErasureDescriptor());
    assertEquals(3, type.getTypeParts().size());

    // part 0
    typePart = type.getTypeParts().get(0);
    assertEquals(new ClassResource("com.pongasoft.kiwidoc.testdata.pubdir1.Pub1Class4"),
                 typePart.getClassResource());
    assertEquals(1, typePart.getGenerics().size());
    checkConcreteType("java.lang.String", typePart.getGenerics().get(0));
    assertEquals("com.pongasoft.kiwidoc.testdata.pubdir1.Pub1Class4", typePart.getName());

    // part 1
    typePart = type.getTypeParts().get(1);
    assertEquals(new ClassResource("com.pongasoft.kiwidoc.testdata.pubdir1.Pub1Class4$Inner3Pub1Class4"),
                 typePart.getClassResource());
    assertEquals(0, typePart.getGenerics().size());
    assertEquals("Inner3Pub1Class4", typePart.getName());

    // part 2
    typePart = type.getTypeParts().get(2);
    assertEquals(new ClassResource("com.pongasoft.kiwidoc.testdata.pubdir1.Pub1Class4$Inner3Pub1Class4$InnerInner3Pub1Class4"),
                 typePart.getClassResource());
    assertEquals(1, typePart.getGenerics().size());
    checkConcreteType("java.lang.Double", typePart.getGenerics().get(0));
    assertEquals("InnerInner3Pub1Class4", typePart.getName());

    assertEquals(sig, new TypeEncoder().encodeType(type));
  }

  @Test
  public void testGenericType()
  {
    List<TypePart> parts = new ArrayList<TypePart>();
    parts.add(new TypePart(new ClassResource("a.b.c"), Arrays.asList(new GenericVariable("V"))));
    GenericType genericType = GenericType.create(parts);
    String encoded = new TypeEncoder().encodeType(genericType);
    GenericType decodedType = (GenericType) new TypeDecoder().decodeType(encoded);
    assertEquals(genericType.getClassResource(), decodedType.getClassResource());
    assertEquals(1, decodedType.getTypeParts().size());
    assertEquals(1, decodedType.getFirstPart().getGenerics().size());
    GenericVariable gtv = (GenericVariable) decodedType.getFirstPart().getGenerics().get(0);
    assertEquals("V", gtv.getName());
  }

  private void checkGenericTypeVariables(List<GenericTypeVariable> list)
  {
    // E:Ljava/lang/Object;
    {
      GenericTypeVariable tv = list.get(0);

      assertEquals("E", tv.getName());
      assertEquals(1, tv.getBounds().size());
      checkConcreteType("java.lang.Object", tv.getBounds().get(0));
    }

    // F:Ljava/lang/Number;
    {
      GenericTypeVariable tv = list.get(1);

      assertEquals("F", tv.getName());
      assertEquals(1, tv.getBounds().size());
      checkConcreteType("java.lang.Number", tv.getBounds().get(0));
    }

    // G:Ljava/lang/Number;:Ljava/lang/Comparable<Ljava/lang/Number;>;
    {
      GenericTypeVariable tv = list.get(2);

      assertEquals("G", tv.getName());
      assertEquals(2, tv.getBounds().size());
      checkConcreteType("java.lang.Number", tv.getBounds().get(0));
      GenericType type1 = (GenericType) tv.getBounds().get(1);
      assertEquals(GenericType.class, type1.getClass());
      assertEquals(1, type1.getTypeParts().size());
      TypePart typePart = type1.getTypeParts().get(0);
      assertEquals("java.lang.Comparable", typePart.getName());
      assertEquals(1, typePart.getGenerics().size());
      checkConcreteType("java.lang.Number", typePart.getGenerics().get(0));
    }

    // H:TE;
    {
      GenericTypeVariable tv = list.get(3);

      assertEquals("H", tv.getName());
      assertEquals(1, tv.getBounds().size());
      GenericVariable type = (GenericVariable) tv.getBounds().get(0);
      assertEquals(GenericVariable.class, type.getClass());
      assertEquals("E", type.getName());
    }

    // "I:Ljava/util/List<TE;>;"
    {
      GenericTypeVariable tv = list.get(4);

      assertEquals("I", tv.getName());
      assertEquals(1, tv.getBounds().size());
      GenericType type = (GenericType) tv.getBounds().get(0);
      assertEquals(GenericType.class, type.getClass());
      assertEquals(1, type.getTypeParts().size());
      TypePart typePart = type.getTypeParts().get(0);
      assertEquals("java.util.List", typePart.getName());
      assertEquals(1, typePart.getGenerics().size());
      GenericVariable type1 = (GenericVariable) typePart.getGenerics().get(0);
      assertEquals(GenericVariable.class, type1.getClass());
      assertEquals("E", type1.getName());
    }

    // "J:Ljava/util/List<Ljava/lang/Number;>;"
    {
      GenericTypeVariable tv = list.get(5);

      assertEquals("J", tv.getName());
      assertEquals(1, tv.getBounds().size());
      GenericType type = (GenericType) tv.getBounds().get(0);
      assertEquals(GenericType.class, type.getClass());
      assertEquals(1, type.getTypeParts().size());
      TypePart typePart = type.getTypeParts().get(0);
      assertEquals("java.util.List", typePart.getName());
      assertEquals(1, typePart.getGenerics().size());
      checkConcreteType("java.lang.Number", typePart.getGenerics().get(0));
    }

    // "K:Ljava/util/List<+Ljava/lang/Number;>;"
    {
      GenericTypeVariable tv = list.get(6);

      assertEquals("K", tv.getName());
      assertEquals(1, tv.getBounds().size());
      GenericType type = (GenericType) tv.getBounds().get(0);
      assertEquals(GenericType.class, type.getClass());
      assertEquals(1, type.getTypeParts().size());
      TypePart typePart = type.getTypeParts().get(0);
      assertEquals("java.util.List", typePart.getName());
      assertEquals(1, typePart.getGenerics().size());
      GenericBoundedWildcardType type1 = (GenericBoundedWildcardType) typePart.getGenerics().get(0);
      assertEquals(GenericBoundedWildcardType.class, type1.getClass());
      assertEquals(GenericBoundedWildcardType.Kind.EXTENDS, type1.getKind());
      checkConcreteType("java.lang.Number", type1.getBound());
    }

    // "L:Ljava/util/List<-Ljava/lang/Number;>;"
    {
      GenericTypeVariable tv = list.get(7);

      assertEquals("L", tv.getName());
      assertEquals(1, tv.getBounds().size());
      GenericType type = (GenericType) tv.getBounds().get(0);
      assertEquals(GenericType.class, type.getClass());
      assertEquals(1, type.getTypeParts().size());
      TypePart typePart = type.getTypeParts().get(0);
      assertEquals("java.util.List", typePart.getName());
      assertEquals(1, typePart.getGenerics().size());
      GenericBoundedWildcardType type1 = (GenericBoundedWildcardType) typePart.getGenerics().get(0);
      assertEquals(GenericBoundedWildcardType.class, type1.getClass());
      assertEquals(GenericBoundedWildcardType.Kind.SUPER, type1.getKind());
      checkConcreteType("java.lang.Number", type1.getBound());
    }

    // "M:Ljava/util/List<+Ljava/util/List<Ljava/lang/Number;>;>;"
    {
      GenericTypeVariable tv = list.get(8);

      assertEquals("M", tv.getName());
      assertEquals(1, tv.getBounds().size());
      GenericType type = (GenericType) tv.getBounds().get(0);
      assertEquals(GenericType.class, type.getClass());
      assertEquals(1, type.getTypeParts().size());
      TypePart typePart = type.getTypeParts().get(0);
      assertEquals("java.util.List", typePart.getName());
      assertEquals(1, typePart.getGenerics().size());

      GenericBoundedWildcardType type1 = (GenericBoundedWildcardType) typePart.getGenerics().get(0);
      assertEquals(GenericBoundedWildcardType.class, type1.getClass());
      assertEquals(GenericBoundedWildcardType.Kind.EXTENDS, type1.getKind());

      GenericType type2 = (GenericType) type1.getBound();
      assertEquals(GenericType.class, type2.getClass());
      assertEquals(1, type2.getTypeParts().size());
      TypePart typePart2 = type2.getTypeParts().get(0);
      assertEquals("java.util.List", typePart2.getName());
      assertEquals(1, typePart2.getGenerics().size());

      checkConcreteType("java.lang.Number", typePart2.getGenerics().get(0));
    }

    // "N:Lcom/pongasoft/kiwidoc/testdata/pubdir1/C1<Ljava/lang/Number;>.Inner1<Ljava/lang/String;>;:Lcom/pongasoft/kiwidoc/testdata/pubdir1/I1.Inner1;:Ljava/util/List<+Lcom/pongasoft/kiwidoc/testdata/pubdir1/C1<Ljava/lang/Number;>.Inner1<Ljava/lang/String;>;>;>";
    {
      GenericTypeVariable tv = list.get(9);

      assertEquals("N", tv.getName());
      assertEquals(3, tv.getBounds().size());

      GenericType type = (GenericType) tv.getBounds().get(0);
      assertEquals(GenericType.class, type.getClass());
      assertEquals(2, type.getTypeParts().size());
      TypePart tp1 = type.getTypeParts().get(0);
      assertEquals("com.pongasoft.kiwidoc.testdata.pubdir1.C1", tp1.getName());
      assertEquals(1, tp1.getGenerics().size());
      checkConcreteType("java.lang.Number", tp1.getGenerics().get(0));
      TypePart tp2 = type.getTypeParts().get(1);
      assertEquals("Inner1", tp2.getName());
      assertEquals(1, tp2.getGenerics().size());
      checkConcreteType("java.lang.String", tp2.getGenerics().get(0));
      assertEquals("com.pongasoft.kiwidoc.testdata.pubdir1.C1$Inner1", type.getClassResource().getFqcn());

      type = (GenericType) tv.getBounds().get(1);
      assertEquals(GenericType.class, type.getClass());
      assertEquals(2, type.getTypeParts().size());
      checkConcreteType("com.pongasoft.kiwidoc.testdata.pubdir1.I1", type.getTypeParts().get(0));
      checkConcreteType("Inner1", type.getTypeParts().get(1));
      assertEquals("com.pongasoft.kiwidoc.testdata.pubdir1.I1$Inner1", type.getClassResource().getFqcn());

      GenericType type1 = (GenericType) tv.getBounds().get(2);
      assertEquals(GenericType.class, type1.getClass());
      assertEquals(1, type1.getTypeParts().size());
      TypePart typePart = type1.getTypeParts().get(0);
      assertEquals("java.util.List", typePart.getName());
      assertEquals(1, typePart.getGenerics().size());

      GenericBoundedWildcardType type2 = (GenericBoundedWildcardType) typePart.getGenerics().get(0);
      assertEquals(GenericBoundedWildcardType.class, type2.getClass());
      assertEquals(GenericBoundedWildcardType.Kind.EXTENDS, type2.getKind());

      GenericType type3 = (GenericType) type2.getBound();
      assertEquals(GenericType.class, type3.getClass());
      assertEquals(2, type3.getTypeParts().size());
      tp1 = type3.getTypeParts().get(0);
      assertEquals("com.pongasoft.kiwidoc.testdata.pubdir1.C1", tp1.getName());
      assertEquals(1, tp1.getGenerics().size());
      checkConcreteType("java.lang.Number", tp1.getGenerics().get(0));
      tp2 = type3.getTypeParts().get(1);
      assertEquals("Inner1", tp2.getName());
      assertEquals(1, tp2.getGenerics().size());
      checkConcreteType("java.lang.String", tp2.getGenerics().get(0));
      assertEquals("com.pongasoft.kiwidoc.testdata.pubdir1.C1$Inner1", type3.getClassResource().getFqcn());
    }
  }

  private void checkConcreteType(String expectedType, Type type)
  {
    GenericType genericType = (GenericType) type;
    assertEquals(1, genericType.getTypeParts().size());
    checkConcreteType(expectedType, genericType.getTypeParts().get(0));
  }

  private void checkConcreteType(String expectedType, TypePart typePart)
  {
    assertTrue(typePart.getGenerics() == null || typePart.getGenerics().size() == 0);
    assertEquals(expectedType, typePart.getName());
  }
}
