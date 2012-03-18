
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

package com.pongasoft.kiwidoc.builder.bytecode;

import com.pongasoft.kiwidoc.builder.model.ClassModelBuilder;
import com.pongasoft.kiwidoc.builder.serializer.type.TypeDecoder;
import com.pongasoft.kiwidoc.model.BaseEntryModel;
import com.pongasoft.kiwidoc.model.ClassEntryModel;
import com.pongasoft.kiwidoc.model.ClassModel;
import com.pongasoft.kiwidoc.model.resource.ClassResource;
import com.pongasoft.kiwidoc.model.type.GenericType;
import com.pongasoft.kiwidoc.model.type.GenericTypeVariables;
import com.pongasoft.kiwidoc.model.type.Type;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yan@pongasoft.com
 */
public class KiwidocClassVisitor extends KiwidocClassEntryVisitor implements ClassVisitor
{
  private final static Map<Integer, Integer> JDK_VERSIONS;

  static
  {
    JDK_VERSIONS = new HashMap<Integer, Integer>();
    JDK_VERSIONS.put(Opcodes.V1_1, 1);
    JDK_VERSIONS.put(Opcodes.V1_2, 2);
    JDK_VERSIONS.put(Opcodes.V1_3, 3);
    JDK_VERSIONS.put(Opcodes.V1_4, 4);
    JDK_VERSIONS.put(Opcodes.V1_5, 5);
    JDK_VERSIONS.put(Opcodes.V1_6, 6);
  }

  private ClassModelBuilder _classModel;
  private int _version;
  private TypeDecoder.ClassSignature _classSignature;

  private Collection<KiwidocFieldVisitor> _fields = new ArrayList<KiwidocFieldVisitor>();
  private Collection<KiwidocMethodVisitor> _methods = new ArrayList<KiwidocMethodVisitor>();
  private ClassResource _classResource;

  private boolean _isInnerNonStaticClass = false;

  /**
   * Constructor
   */
  public KiwidocClassVisitor()
  {
  }

  public ClassModelBuilder getClassModel()
  {
    return _classModel;
  }

  public void visit(int version,
                    int access,
                    String name,
                    String signature,
                    String superName,
                    String[] interfaces)
  {
    _version = version;
    init(access, ClassResource.computeFQCNFromInternalName(name));
    _classResource = new ClassResource(getName());

    if(signature != null)
    {
      _classSignature =
        ByteCodeHelper.TYPE_DECODER.decodeClassSignature(signature);
    }
    else
    {
      Collection<GenericType> interfaceTypes  = new ArrayList<GenericType>();
      if(interfaces != null)
      {
        for(String anInterface : interfaces)
        {
          interfaceTypes.add(ByteCodeHelper.computeType(anInterface));
        }
      }

      _classSignature =
        new TypeDecoder.ClassSignatureImpl(GenericTypeVariables.NO_GENERIC_TYPE_VARIABLES,
                                           ByteCodeHelper.computeType(superName),
                                           interfaceTypes);
    }
  }

  public void visitSource(String source, String debug)
  {
  }

  public void visitOuterClass(String owner, String name, String desc)
  {
  }

  public void visitAttribute(Attribute attr)
  {
  }

  public void visitInnerClass(String name, String outerName, String innerName, int access)
  {
    if(_classResource.getFqcn().equals(ClassResource.computeFQCNFromInternalName(name)))
      _isInnerNonStaticClass = !BaseEntryModel.Access.STATIC.matches(access);
  }

  public FieldVisitor visitField(int access,
                                 String name,
                                 String desc,
                                 String signature,
                                 Object value)
  {
    Type fieldType;
    if(signature != null)
    {
      fieldType = ByteCodeHelper.decodeType(signature);
    }
    else
    {
      fieldType = ByteCodeHelper.decodeType(desc);
    }
    KiwidocFieldVisitor visitor = new KiwidocFieldVisitor(access, name, fieldType, value);
    _fields.add(visitor);
    return visitor;
  }

  public MethodVisitor visitMethod(int access,
                                   String name,
                                   String desc,
                                   String signature,
                                   String[] exceptions)
  {
    // skipping static initializers...
    if(name.equals("<clinit>"))
      return null;

    TypeDecoder.MethodSignature methodSignature =
      ByteCodeHelper.TYPE_DECODER.decodeMethodSignature(desc);

    TypeDecoder.MethodSignature genericMethodSignature = null;

    if(signature != null)
    {
      genericMethodSignature = ByteCodeHelper.TYPE_DECODER.decodeMethodSignature(signature);
    }

    KiwidocMethodVisitor methodVisitor =
      new KiwidocMethodVisitor(access,
                               _classResource,
                               _isInnerNonStaticClass,
                               name,
                               methodSignature, 
                               genericMethodSignature);
    _methods.add(methodVisitor);
    return methodVisitor;
  }

  public void visitEnd()
  {
      _classModel = new ClassModelBuilder(getAccess(),
                                          null,
                                          getAnnotations(),
                                          computeClassKind(),
                                          getName(),
                                          _classSignature.getGenericTypeVariables(),
                                          _classSignature.getSuperClass(),
                                          _classSignature.getInterfaces());

    _classModel.setJdkVersion(JDK_VERSIONS.get(_version));

    for(KiwidocFieldVisitor field : _fields)
    {
      _classModel.addField(field.getFieldModel());
    }

    for(KiwidocMethodVisitor method : _methods)
    {
      _classModel.addMethod(method.getMethodModel());
    }
  }

  private ClassModel.ClassKind computeClassKind()
  {
    if(ClassEntryModel.Access.INTERFACE.matches(getAccess()))
      return ClassModel.ClassKind.INTERFACE;

    if(ClassEntryModel.Access.ENUM.matches(getAccess()))
      return ClassModel.ClassKind.ENUM;

    if(ClassEntryModel.Access.ANNOTATION.matches(getAccess()))
      return ClassModel.ClassKind.ANNOTATION;

    return ClassModel.ClassKind.CLASS;
  }
}