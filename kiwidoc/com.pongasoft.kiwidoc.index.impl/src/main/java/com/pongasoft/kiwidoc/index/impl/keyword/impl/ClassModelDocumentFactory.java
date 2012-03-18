
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

package com.pongasoft.kiwidoc.index.impl.keyword.impl;

import com.pongasoft.kiwidoc.model.ClassModel;
import com.pongasoft.kiwidoc.model.AnnotationModel;
import com.pongasoft.kiwidoc.model.FieldModel;
import com.pongasoft.kiwidoc.model.MethodModel;
import com.pongasoft.kiwidoc.model.ParameterModel;
import com.pongasoft.kiwidoc.model.type.Type;
import com.pongasoft.kiwidoc.index.impl.ResourceEncoder;
import com.pongasoft.util.core.annotations.ObjectInitializer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

/**
 * @author yan@pongasoft.com
 */
public class ClassModelDocumentFactory extends AbstractDocumentFactory<ClassModel>
{
  /**
   * Constructor
   */
  @ObjectInitializer
  public ClassModelDocumentFactory()
  {
  }

  public ClassModelDocumentFactory(ResourceEncoder<String> resourceEncoder)
  {
    super(resourceEncoder);
  }

  /**
   * @param model the model to process
   * @return the document for the given model
   */
  public Document createDocument(ClassModel model)
  {
    Document doc = doCreateDocument(model);

    // library version to be able to unindex a library
    addFieldResource(doc, LIBRARY_VERSION_FIELD, model.getResource().getLibraryVersionResource());

    // class name
    addStoredUnanalyzed(doc, CLASS_NAME_FIELD, model.getSimpleName().toLowerCase());

    // package name
    addStoredUnanalyzed(doc, PACKAGE_NAME_FIELD, model.getPackageName());

    // content
    doc.add(new Field(BODY_FIELD,
                      buildBody(model),
                      Field.Store.NO,
                      Field.Index.ANALYZED));
    return doc;
  }

  private String buildBody(ClassModel classModel)
  {
    StringBuilder sb = new StringBuilder();

    // declaration
    sb.append(classModel.getClassKindString()).append(':')
      .append(classModel.getResource().getFqcnWithDot()).append(" (")
      .append(classModel.getResource().getLibraryVersionResource()).append(")");
    sb.append(FIELD_SEPARATOR);

    // package name
    sb.append(classModel.getPackageName());
    sb.append(FIELD_SEPARATOR);

    // annotations
    for(AnnotationModel annotationModel : classModel.getAnnotationsModel().getAnnotations())
    {
      sb.append(annotationModel);
      sb.append(FIELD_SEPARATOR);
    }

    // class documentation
    if(classModel.getDoc() != null && classModel.getDoc().hasDoc())
    {
      sb.append(filterDoc(classModel.getDoc()));
      sb.append(FIELD_SEPARATOR);
    }

    // generics
    sb.append(classModel.getGenericTypeVariables());
    sb.append(FIELD_SEPARATOR);

    // class declaration
    sb.append(classModel.getSimpleName());
    sb.append(FIELD_SEPARATOR);

    // superclass
    if(classModel.getSuperClass() != null)
    {
      sb.append(classModel.getSuperClass());
      sb.append(FIELD_SEPARATOR);
    }

    // interfaces
    if(!classModel.getInterfaces().isEmpty())
    {
      for(Type iface : classModel.getInterfaces())
      {
        sb.append(iface).append(", ");
      }
      sb.append(FIELD_SEPARATOR);
    }

    // fields
    for(FieldModel fieldModel : classModel.getAllFields())
    {
      if(fieldModel.getDoc() != null)
      {
        sb.append(filterDoc(fieldModel.getDoc()));
        sb.append(FIELD_SEPARATOR);
      }

      // annotations
      for(AnnotationModel annotationModel : fieldModel.getAnnotationsModel().getAnnotations())
      {
        sb.append(annotationModel);
        sb.append(FIELD_SEPARATOR);
      }

      sb.append(fieldModel.getType());
      sb.append(FIELD_SEPARATOR);

      sb.append(fieldModel.getName());
      sb.append(FIELD_SEPARATOR);
    }

    // methods
    for(MethodModel methodModel : classModel.getAllMethods())
    {
      if(methodModel.getDoc() != null)
      {
        sb.append(filterDoc(methodModel.getDoc()));
        sb.append(FIELD_SEPARATOR);
      }

      // annotations
      for(AnnotationModel annotationModel : methodModel.getAnnotationsModel().getAnnotations())
      {
        sb.append(annotationModel);
        sb.append(FIELD_SEPARATOR);
      }

      sb.append(methodModel.getGenericTypeVariables());
      sb.append(FIELD_SEPARATOR);

      if(methodModel.getReturnType() != null)
      {
        sb.append(methodModel.getReturnType());
        sb.append(FIELD_SEPARATOR);
      }

      if(!methodModel.isConstructor())
      {
        sb.append(methodModel.getName());
        sb.append(FIELD_SEPARATOR);
      }

      for(ParameterModel parameterModel : methodModel.getParameters())
      {
        sb.append(parameterModel.getType());
        sb.append(FIELD_SEPARATOR);

        sb.append(parameterModel.getName());
        sb.append(FIELD_SEPARATOR);
      }

      for(Type type : methodModel.getExceptions())
      {
        sb.append(type);
        sb.append(FIELD_SEPARATOR);
      }
    }

    return sb.toString();
  }
}
