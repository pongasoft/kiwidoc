
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

import com.pongasoft.kiwidoc.builder.serializer.type.TypeDecoder;
import com.pongasoft.kiwidoc.model.AnnotationModel;
import com.pongasoft.kiwidoc.model.AnnotationsModel;
import com.pongasoft.kiwidoc.model.ClassModel;
import com.pongasoft.kiwidoc.model.MethodModel;
import com.pongasoft.kiwidoc.model.ParameterModel;
import com.pongasoft.kiwidoc.model.ParametersModel;
import com.pongasoft.kiwidoc.model.annotation.Annotation;
import com.pongasoft.kiwidoc.model.annotation.AnnotationElement;
import com.pongasoft.kiwidoc.model.annotation.AnnotationValue;
import com.pongasoft.kiwidoc.model.resource.ClassResource;
import com.pongasoft.kiwidoc.model.resource.MethodResource;
import com.pongasoft.kiwidoc.model.type.SimpleType;
import com.pongasoft.kiwidoc.model.type.Type;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * <ul>
 * <li>For static methods: visitParameterAnnotation.index starts at 0</li>
 * <li>For non static methods: visitParameterAnnotation.index starts at 0 and visitLocalVariable.index
 * starts at 1 (0 is this)</li>
 * <li>For constructors of inner non static class visitParameterAnnotation.index starts at 1
 * (0 is outer type), and visitLocalVariable.index starts at 2 (0 is 'this' and 1 is outer type)</li>
 * </ul>
 *
 *
 * @author yan@pongasoft.com
 */
public class KiwidocMethodVisitor extends KiwidocClassEntryVisitor implements MethodVisitor
{
  private final TypeDecoder.MethodSignature _methodSignature;
  private final List<Type> _parameters;
  private final List<Collection<AnnotationModel>> _parametersAnnotations;
  private final Map<String, Type> _parameterTypes = new LinkedHashMap<String, Type>();
  private final Map<Integer, String> _parameterNames = new TreeMap<Integer, String>();

  private MethodModel _methodModel;
  private AnnotationValue _annotationDefaultValue;
  private final ClassResource _classResource;
  private final boolean _isInnerNonStaticClass;

  /**
   * Constructor
   * @param access
   * @param innerNonStaticClass
   * @param name
   * @param genericMethodSignature
   */
  public KiwidocMethodVisitor(int access,
                              ClassResource classResource,
                              boolean innerNonStaticClass, String name,
                              TypeDecoder.MethodSignature methodSignature,
                              TypeDecoder.MethodSignature genericMethodSignature)
  {
    super(access, name);
    _classResource = classResource;
    _isInnerNonStaticClass = innerNonStaticClass;
    _methodSignature = genericMethodSignature == null ? methodSignature : genericMethodSignature;

    if(genericMethodSignature != null)
    {
      _parameters = genericMethodSignature.getParameters();
    }
    else
    {
      _parameters = new ArrayList<Type>(methodSignature.getParameters());
      if(isInnerNonStaticClassConstructor())
        _parameters.remove(0);
    }

    _parametersAnnotations = new ArrayList<Collection<AnnotationModel>>(_parameters.size());
    for(int i = 0; i < _parameters.size(); i++)
    {
      _parametersAnnotations.add(new ArrayList<AnnotationModel>());
    }
  }

  public MethodModel getMethodModel()
  {
    return _methodModel;
  }

  public boolean isConstructor()
  {
    return getName().equals("<init>");
  }

  public boolean isInnerNonStaticClassConstructor()
  {
    return isConstructor() && _isInnerNonStaticClass;
  }

  public AnnotationVisitor visitAnnotationDefault()
  {
    return new KiwidocAnnotationVisitor(new KiwidocAnnotationVisitor.Setter()
    {
      public void setElements(Collection<AnnotationElement> elements)
      {
        // we know (cf javadoc) that there is one and only 1 element...
        _annotationDefaultValue = elements.iterator().next().getAnnotationValue();
      }
    });
  }

  public void visitAttribute(Attribute attribute)
  {
  }

  public void visitCode()
  {
  }

  public void visitEnd()
  {
    List<ParameterModel> parameters = new ArrayList<ParameterModel>(_parameters.size());
    List<String> parameterNames = new ArrayList<String>(_parameterNames.values());

    for(int i = 0; i < _parameters.size(); i++)
    {
      AnnotationsModel annotations = new AnnotationsModel(_parametersAnnotations.get(i));

      Type parameterType = _parameters.get(i);

      String name = null;
      if(i < parameterNames.size())
      {
        name = parameterNames.get(i);
        Type type = _parameterTypes.get(name);
        // we check that the types match...
        if(type == null || !parameterType.toString().equals(type.toString()))
        {
            log.warn("No matching type found for " + _classResource + "." + getName() +
                     " index " + i + " (" + name + ") [" + parameterType + " !=" + type + "]");
          name = null;
        }
      }
      parameters.add(new ParameterModel(name,
                                        parameterType,
                                        annotations));
    }

    String memberName = ClassModel.computeMemberName(getName(), parameters);

    String methodName = getName();

    // constructor should be name of the class
    if(isConstructor())
      methodName = _classResource.getName();

    _methodModel = new MethodModel(getAccess(),
                                   new MethodResource(_classResource, memberName), 
                                   methodName,
                                   null,
                                   getAnnotations(),
                                   _methodSignature.getGenericTypeVariables(),
                                   new ParametersModel(parameters),
                                   _methodSignature.getReturnType(), 
                                   _methodSignature.getExceptions(),
                                   _annotationDefaultValue);
  }

  public void visitFieldInsn(int i, String s, String s1, String s2)
  {
  }

  public void visitFrame(int i, int i1, Object[] objects, int i2, Object[] objects1)
  {
  }

  public void visitIincInsn(int i, int i1)
  {
  }

  public void visitInsn(int i)
  {
  }

  public void visitIntInsn(int i, int i1)
  {
  }

  public void visitJumpInsn(int i, Label label)
  {
  }

  public void visitLabel(Label label)
  {
  }

  public void visitLdcInsn(Object o)
  {
  }

  public void visitLineNumber(int i, Label label)
  {
  }

  public void visitLocalVariable(String name,
                                 String desc,
                                 String signature,
                                 Label start,
                                 Label end,
                                 int index)
  {
    if(!name.startsWith("this"))
    {
      Type type;
      if(signature != null)
      {
        type = ByteCodeHelper.decodeType(signature);
      }
      else
      {
        type = ByteCodeHelper.decodeType(desc);
      }

      _parameterTypes.put(name, type);
      _parameterNames.put(index, name);
    }
  }

  public void visitLookupSwitchInsn(Label label, int[] ints, Label[] labels)
  {
  }

  public void visitMaxs(int i, int i1)
  {
  }

  public void visitMethodInsn(int i, String s, String s1, String s2)
  {
  }

  public void visitMultiANewArrayInsn(String s, int i)
  {
  }

  // YP note: in case of synthetic attribute, the generic method signature does not contain them
  // but the method visitParameterAnnotation(int parameter) gets called with an index that
  // spans the full size...
  public AnnotationVisitor visitParameterAnnotation(int parameter,
                                                    final String desc,
                                                    boolean visible)
  {
    final int p;

    if(isInnerNonStaticClassConstructor())
      p = parameter - 1;
    else
      p = parameter;

    if(p >= 0)
    {
      return new KiwidocAnnotationVisitor(new KiwidocAnnotationVisitor.Setter()
      {
        public void setElements(Collection<AnnotationElement> elements)
        {
          Collection<AnnotationModel> annotations = _parametersAnnotations.get(p);
          Annotation annotation =
            new Annotation((SimpleType) ByteCodeHelper.decodeType(desc), elements);
          annotations.add(new AnnotationModel(annotation));
        }
      });
    }
    else
    {
      return null;
    }
  }

  public void visitTableSwitchInsn(int i, int i1, Label label, Label[] labels)
  {
  }

  public void visitTryCatchBlock(Label label, Label label1, Label label2, String s)
  {
  }

  public void visitTypeInsn(int i, String s)
  {
  }

  public void visitVarInsn(int i, int i1)
  {
  }
}
