
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

import com.pongasoft.kiwidoc.builder.model.ClassModelBuilder;
import com.pongasoft.kiwidoc.builder.serializer.type.TypeEncoder;
import com.pongasoft.kiwidoc.model.AnnotationModel;
import com.pongasoft.kiwidoc.model.AnnotationsModel;
import com.pongasoft.kiwidoc.model.BaseEntryModel;
import com.pongasoft.kiwidoc.model.ClassModel;
import com.pongasoft.kiwidoc.model.DocModel;
import com.pongasoft.kiwidoc.model.FieldModel;
import com.pongasoft.kiwidoc.model.MethodModel;
import com.pongasoft.kiwidoc.model.ParameterModel;
import com.pongasoft.kiwidoc.model.ParametersModel;
import com.pongasoft.kiwidoc.model.annotation.Annotation;
import com.pongasoft.kiwidoc.model.annotation.AnnotationAnnotationValue;
import com.pongasoft.kiwidoc.model.annotation.AnnotationElement;
import com.pongasoft.kiwidoc.model.annotation.AnnotationValue;
import com.pongasoft.kiwidoc.model.annotation.ArrayAnnotationValue;
import com.pongasoft.kiwidoc.model.annotation.ClassAnnotationValue;
import com.pongasoft.kiwidoc.model.annotation.EnumAnnotationValue;
import com.pongasoft.kiwidoc.model.annotation.PrimitiveAnnotationValue;
import com.pongasoft.kiwidoc.model.annotation.StringAnnotationValue;
import com.pongasoft.kiwidoc.model.resource.ClassResource;
import com.pongasoft.kiwidoc.model.resource.MethodResource;
import com.pongasoft.kiwidoc.model.tag.InlineLinkTag;
import com.pongasoft.kiwidoc.model.tag.InlineOrdinaryTag;
import com.pongasoft.kiwidoc.model.tag.InlineTag;
import com.pongasoft.kiwidoc.model.tag.InlineTextTag;
import com.pongasoft.kiwidoc.model.tag.LinkReference;
import com.pongasoft.kiwidoc.model.tag.MainTag;
import com.pongasoft.kiwidoc.model.tag.OrdinaryTag;
import com.pongasoft.kiwidoc.model.tag.ParamTag;
import com.pongasoft.kiwidoc.model.tag.SeeTag;
import com.pongasoft.kiwidoc.model.tag.Tag;
import com.pongasoft.kiwidoc.model.tag.ThrowsTag;
import com.pongasoft.kiwidoc.model.type.ArrayType;
import com.pongasoft.kiwidoc.model.type.GenericBoundedWildcardType;
import com.pongasoft.kiwidoc.model.type.GenericType;
import com.pongasoft.kiwidoc.model.type.GenericTypeVariable;
import com.pongasoft.kiwidoc.model.type.GenericTypeVariables;
import com.pongasoft.kiwidoc.model.type.GenericUnboundedWildcardType;
import com.pongasoft.kiwidoc.model.type.GenericVariable;
import com.pongasoft.kiwidoc.model.type.GenericWildcardType;
import com.pongasoft.kiwidoc.model.type.PrimitiveType;
import com.pongasoft.kiwidoc.model.type.Type;
import com.pongasoft.kiwidoc.model.type.TypePart;
import com.pongasoft.kiwidoc.model.type.UnresolvedType;
import com.pongasoft.util.core.text.TextUtils;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.AnnotationTypeElementDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.ParameterizedType;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.TypeVariable;
import com.sun.javadoc.WildcardType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is <em>not</em> thread safe, but it should not be used by multiple threads at a
 * time...
 *
 * @author yan@pongasoft.com
 */
public class KiwidocDoclet
{
  private final RootDoc _rootDoc;
  private final List<ClassModelBuilder> _classDescs = new ArrayList<ClassModelBuilder>();
  private final Map<String, UnresolvedType> _unresolvedTypes = new HashMap<String, UnresolvedType>();
  private final Map<String, DocModel> _packageInfos = new HashMap<String, DocModel>();

  private DocModel _overview;

  /**
   * Constructor
   */
  public KiwidocDoclet(RootDoc rootDoc)
  {
    _rootDoc = rootDoc;
  }

  public void generateKiwidoc()
  {
    _overview = extractDoc(_rootDoc);

    for(ClassDoc classDoc : _rootDoc.classes())
    {
      processClass(classDoc);
    }
  }

  public List<ClassModelBuilder> getClassModels()
  {
    return _classDescs;
  }

  public Map<String, DocModel> getPackageInfos()
  {
    return _packageInfos;
  }

  public DocModel getOverview()
  {
    return _overview;
  }

  public Map<String, UnresolvedType> getUnresolvedTypes()
  {
    return _unresolvedTypes;
  }

  private ClassModelBuilder processClass(ClassDoc classDoc)
  {
    // create the class
    ClassModel.ClassKind kind = computeKind(classDoc);

    ClassModelBuilder classDesc = new ClassModelBuilder(adjustAccess(classDoc.modifierSpecifier(), kind),
                                                        extractDoc(classDoc),
                                                        extractAnnotations(classDoc.annotations()),
                                                        kind,
                                                        computeFQCN(classDoc),
                                                        extractGenericTypeVariables(classDoc.typeParameters()),
                                                        (GenericType) extractType(classDoc.superclassType()),
                                                        this.<GenericType>extractTypes(classDoc.interfaceTypes()));

    _classDescs.add(classDesc);

    // package documentation
    if(!_packageInfos.containsKey(classDesc.getPackageName()))
    {
      _packageInfos.put(classDesc.getPackageName(), extractDoc(classDoc.containingPackage()));
    }

    // methods
    extractMethods(classDesc, classDoc.methods(false));

    // constructors
    extractMethods(classDesc, classDoc.constructors(false));

    // annotation elements
    if(classDoc instanceof AnnotationTypeDoc)
    {
      AnnotationTypeDoc annotationTypeDoc = (AnnotationTypeDoc) classDoc;
      extractMethods(classDesc, annotationTypeDoc.elements());
    }

    // fields
    extractFields(classDesc, classDoc.fields(false), false);

    if(classDoc.isEnum())
    {
      extractFields(classDesc, classDoc.enumConstants(), true);
    }

    ClassDoc[] innerClasses = classDoc.innerClasses(false);
    for(ClassDoc innerClass : innerClasses)
    {
      ClassModelBuilder innerClassDesc = processClass(innerClass);
      classDesc.addInnerClass(innerClassDesc);
    }

    return classDesc;
  }

  private int adjustAccess(int access, ClassModel.ClassKind kind)
  {
    BaseEntryModel.Access adjustingAccess = null;
    switch(kind)
    {
      case ANNOTATION:
        adjustingAccess =  BaseEntryModel.Access.ANNOTATION;
        break;

      case ENUM:
        adjustingAccess = BaseEntryModel.Access.ENUM;
        break;

      case INTERFACE:
        adjustingAccess = BaseEntryModel.Access.INTERFACE;
        break;

      default:
        break;
    }

    if(adjustingAccess != null)
      access |= adjustingAccess.getOpcode();

    return access;
  }

  private void extractFields(ClassModelBuilder classDesc,
                             FieldDoc[] fields,
                             boolean enumConstants)
  {
    for(FieldDoc fieldDoc : fields)
    {
      int access = fieldDoc.modifierSpecifier();
      if(enumConstants)
        access |= BaseEntryModel.Access.ENUM.getOpcode();
      
      classDesc.addField(new FieldModel(access,
                                        fieldDoc.name(),
                                        extractDoc(fieldDoc),
                                        extractAnnotations(fieldDoc.annotations()),
                                        extractType(fieldDoc.type()),
                                        fieldDoc.constantValue()));
    }
  }

  private void extractMethods(ClassModelBuilder classDesc, ExecutableMemberDoc[] memberDocs)
  {
    for(ExecutableMemberDoc methodDoc : memberDocs)
    {
      AnnotationValue annotationDefaultValue = null;
      Type returnType = null;

      if(methodDoc instanceof MethodDoc)
      {
        MethodDoc doc = (MethodDoc) methodDoc;
        returnType = extractType(doc.returnType());
      }

      int access = methodDoc.modifierSpecifier();

      if(methodDoc instanceof AnnotationTypeElementDoc)
      {
        AnnotationTypeElementDoc doc = (AnnotationTypeElementDoc) methodDoc;
        annotationDefaultValue = extractAnnotationValue(doc.defaultValue());
        access |= BaseEntryModel.Access.ANNOTATION.getOpcode();
      }

      if(methodDoc.isVarArgs())
        access |= BaseEntryModel.Access.VARARGS.getOpcode();

      ParametersModel parameters = extractParameters(methodDoc.parameters());

      String memberName = ClassModel.computeMemberName(methodDoc.name(),
                                                       parameters.getParameters());

      classDesc.addMethod(new MethodModel(access,
                                          new MethodResource(new ClassResource(classDesc.getFqcn()), memberName), 
                                          methodDoc.name(),
                                          extractDoc(methodDoc),
                                          extractAnnotations(methodDoc.annotations()),
                                          extractGenericTypeVariables(methodDoc.typeParameters()),
                                          parameters,
                                          returnType, 
                                          extractTypes(methodDoc.thrownExceptionTypes()),
                                          annotationDefaultValue));
    }
  }

  private AnnotationValue extractAnnotationValue(com.sun.javadoc.AnnotationValue annotationValue)
  {
    if(annotationValue == null)
      return null;

    Object value = annotationValue.value();

    if(value instanceof String)
    {
      return new StringAnnotationValue((String) value);
    }

    if(value instanceof com.sun.javadoc.Type)
    {
      return new ClassAnnotationValue(extractType((com.sun.javadoc.Type) value));
    }

    if(value instanceof FieldDoc)
    {
      FieldDoc fieldDoc = (FieldDoc) value;
      return new EnumAnnotationValue((GenericType) extractType(fieldDoc.type()),
                                     fieldDoc.name());
    }

    if(value instanceof AnnotationDesc)
    {
      AnnotationDesc annotationDesc = (AnnotationDesc) value;
      return new AnnotationAnnotationValue(new Annotation((GenericType) extractType(annotationDesc.annotationType()),
                                                           extractAnnotationValues(annotationDesc.elementValues())));
    }

    if(value instanceof com.sun.javadoc.AnnotationValue[])
    {
      com.sun.javadoc.AnnotationValue[] array = (com.sun.javadoc.AnnotationValue[]) value;
      Collection<AnnotationValue> values = new ArrayList<AnnotationValue>(array.length);
      for(com.sun.javadoc.AnnotationValue av : array)
      {
        values.add(extractAnnotationValue(av));
      }
      return new ArrayAnnotationValue(values);
    }

    return new PrimitiveAnnotationValue(new PrimitiveType(value.getClass()),
                                        value.toString());
  }

  private DocModel extractDoc(Doc doc)
  {
    List<Tag> tags = new ArrayList<Tag>();
    for(com.sun.javadoc.Tag tag : doc.tags())
    {
      tags.add(extractTag(tag));
    }

    return new DocModel(new MainTag(null, extractInlineTags(doc.inlineTags())),
                        tags);
  }

  private Tag extractTag(com.sun.javadoc.Tag tag)
  {
    if(tag instanceof com.sun.javadoc.ParamTag)
    {
      com.sun.javadoc.ParamTag paramTag = (com.sun.javadoc.ParamTag) tag;

      return new ParamTag(null,
                          paramTag.name(),
                          extractInlineTags(tag.inlineTags()),
                          paramTag.parameterName(),
                          paramTag.isTypeParameter());
    }

    if(tag instanceof com.sun.javadoc.SeeTag)
    {
      return extractSeeTag((com.sun.javadoc.SeeTag) tag);
    }

    if(tag instanceof com.sun.javadoc.ThrowsTag)
    {
      return extractThrowsTag((com.sun.javadoc.ThrowsTag) tag);
    }

    return new OrdinaryTag(null,
                           tag.name(),
                           extractInlineTags(tag.inlineTags()));
  }

  private ThrowsTag extractThrowsTag(com.sun.javadoc.ThrowsTag tag)
  {
    return new ThrowsTag(null,
                         tag.name(),
                         extractInlineTags(tag.inlineTags()),
                         tag.exceptionName(), 
                         extractType(tag.exceptionType()));
  }

  private SeeTag extractSeeTag(com.sun.javadoc.SeeTag seeTag)
  {
    List<InlineTag> inlineTags = extractInlineTags(seeTag.inlineTags());
    String rawReference = "";

    String packageName = null;
    String simpleClassName = null;
    String memberName = null;

    if(seeTag.referencedPackage() != null)
    {
      packageName = seeTag.referencedPackage().name();
    }

    if(seeTag.referencedClass() != null)
    {
      String fqcn = computeFQCN(seeTag.referencedClass());
      packageName = ClassModel.computePackageName(fqcn);
      simpleClassName = ClassModel.computeSimpleName(fqcn);
    }

    if(seeTag.referencedMember() != null)
    {
      MemberDoc memberDoc = seeTag.referencedMember();

      String fqcn = computeFQCN(memberDoc.containingClass());
      packageName = ClassModel.computePackageName(fqcn);
      simpleClassName = ClassModel.computeSimpleName(fqcn);

      if(memberDoc.isConstructor() || memberDoc.isMethod())
      {
        ExecutableMemberDoc emd = (ExecutableMemberDoc) memberDoc;

        memberName = ClassModel.computeMemberName(emd.name(),
                                                  extractParameters(emd.parameters()).getParameters());
      }
      else
      {
        memberName = memberDoc.name();
      }
    }

    LinkReference linkReference = LinkReference.NO_LINK_REFERENCE;

    if(!inlineTags.isEmpty() && packageName != null)
    {
      String text = seeTag.text();
      String label = seeTag.label();

      rawReference = text.substring(0, text.length() - label.length()).trim();
      text = inlineTags.get(0).getText();
      if(text.equals(rawReference))
        text = "";
      else
        text = TextUtils.ltrim(text.substring(rawReference.length() + 1));
      inlineTags.set(0, new InlineTextTag(text));

      linkReference = new LinkReference(rawReference,
                                        packageName,
                                        simpleClassName,
                                        memberName);
    }

    return new SeeTag(null,
                      seeTag.name(),
                      inlineTags,
                      linkReference);
  }

  private InlineTag extractInlineTag(com.sun.javadoc.Tag tag)
  {
    if(tag.kind().equals("Text"))
    {
      String text = tag.text();
      if(text.equals(""))
        return null;
      else
        return new InlineTextTag(tag.text());
    }

    if(tag instanceof com.sun.javadoc.SeeTag)
    {
      SeeTag seeTag = extractSeeTag((com.sun.javadoc.SeeTag) tag);

      return new InlineLinkTag(seeTag.getText(),
                               seeTag.getLinkReference());
    }
    else
    {
      return new InlineOrdinaryTag(tag.name(), tag.text());
    }

  }

  private List<InlineTag> extractInlineTags(com.sun.javadoc.Tag[] inlineTags)
  {
    if(inlineTags.length == 0)
      return InlineTag.NO_INLINE_TAGS;

    List<InlineTag> res = new ArrayList<InlineTag>(inlineTags.length);

    for(com.sun.javadoc.Tag inlineTag : inlineTags)
    {
      InlineTag tag = extractInlineTag(inlineTag);
      if(tag != null)
        res.add(tag);
    }

    return res;
  }

  private ClassModel.ClassKind computeKind(ClassDoc classDoc)
  {
    if(classDoc.isOrdinaryClass())
      return ClassModel.ClassKind.CLASS;

    if(classDoc.isInterface())
      return ClassModel.ClassKind.INTERFACE;

    if(classDoc.isEnum())
      return ClassModel.ClassKind.ENUM;

    if(classDoc.isAnnotationType())
      return ClassModel.ClassKind.ANNOTATION;

    if(classDoc.isError())
      return ClassModel.ClassKind.ERROR;

    if(classDoc.isException())
      return ClassModel.ClassKind.EXCEPTION;

    throw new RuntimeException("unknown type for " + classDoc);
  }

  private ParametersModel extractParameters(Parameter[] parameters)
  {
    List<ParameterModel> pds = new ArrayList<ParameterModel>(parameters.length);

    for(Parameter parameter : parameters)
    {
      pds.add(extractParameter(parameter));
    }

    return new ParametersModel(pds);
  }

  private GenericTypeVariables extractGenericTypeVariables(TypeVariable[] typeVariables)
  {
    List<GenericTypeVariable> gtvs = new ArrayList<GenericTypeVariable>(typeVariables.length);

    for(TypeVariable typeVariable : typeVariables)
    {
      gtvs.add(extractGenericTypeVariable(typeVariable));
    }

    return new GenericTypeVariables(gtvs);
  }

  @SuppressWarnings("unchecked")
  private <K extends Type> List<K> extractTypes(com.sun.javadoc.Type[] javadocTypes)
  {
    List<K> types = new ArrayList<K>(javadocTypes.length);

    for(com.sun.javadoc.Type javadocType : javadocTypes)
    {
      types.add((K) extractType(javadocType));
    }

    return types;
  }

  private Type extractType(com.sun.javadoc.Type javadocType)
  {
    if(javadocType == null)
      return null;

    if(javadocType.isPrimitive())
      return adjustArrayType(javadocType, extractPrimitiveType(javadocType));

    TypeVariable tv = javadocType.asTypeVariable();
    if(tv != null)
      return adjustArrayType(javadocType, extractGenericVariable(tv));

    ParameterizedType pt = javadocType.asParameterizedType();
    if(pt != null)
      return adjustArrayType(javadocType, extractGenericType(pt));

    WildcardType wt = javadocType.asWildcardType();
    if(wt != null)
      return adjustArrayType(javadocType, extractGenericWildcardType(wt));

    ClassDoc classDoc = javadocType.asClassDoc();
    if(classDoc != null)
      return adjustArrayType(javadocType, extractGenericType(classDoc));

    throw new IllegalArgumentException("unknown javadoc type: " + javadocType.getClass().getName());
  }

  private Type extractPrimitiveType(com.sun.javadoc.Type javadocType)
  {
    try
    {
      if(javadocType.qualifiedTypeName().equals("<any>"))
        return TypeEncoder.OBJECT_TYPE;
      else
        return new PrimitiveType(javadocType.qualifiedTypeName());
    }
    catch(IllegalArgumentException e)
    {
      String name = javadocType.qualifiedTypeName();
      UnresolvedType unresolvedType = _unresolvedTypes.get(name);
      if(unresolvedType == null)
      {
        unresolvedType = new UnresolvedType(name);
        _unresolvedTypes.put(name, unresolvedType);
      }
      return unresolvedType;
    }
  }

  private Type adjustArrayType(com.sun.javadoc.Type javadocType, Type type)
  {
    int len = javadocType.dimension().length();
    if(len != 0)
      type = new ArrayType(len / 2, type);
    return type;
  }

  private GenericWildcardType extractGenericWildcardType(WildcardType wt)
  {
    com.sun.javadoc.Type[] superBounds = wt.superBounds();
    com.sun.javadoc.Type[] extendsBounds = wt.extendsBounds();

    if(superBounds.length == 0 && extendsBounds.length == 0)
      return GenericUnboundedWildcardType.INSTANCE;

    if(superBounds.length == 1)
      return new GenericBoundedWildcardType(GenericBoundedWildcardType.Kind.SUPER,
                                            extractType(superBounds[0]));

    if(extendsBounds.length == 1)
      return new GenericBoundedWildcardType(GenericBoundedWildcardType.Kind.EXTENDS,
                                            extractType(extendsBounds[0]));

    throw new RuntimeException("wildcard type should have only 1 entry " + superBounds.length + 
                               " / " + extendsBounds.length);
  }

  private GenericType extractGenericType(ParameterizedType pt)
  {
    List<TypePart> typeParts = new ArrayList<TypePart>();

    Type containingType = extractType(pt.containingType());

    if(containingType != null)
    {
      GenericType gct = (GenericType) containingType;
      typeParts.addAll(gct.getTypeParts());
      typeParts.add(new TypePart(gct.getClassResource().createInnerClass(pt.simpleTypeName()),
                                 extractTypes(pt.typeArguments())));
    }
    else
    {
      typeParts.add(new TypePart(new ClassResource(computeFQCN(pt.asClassDoc())), 
                                 extractTypes(pt.typeArguments())));
    }

    return GenericType.create(typeParts);
  }

  private static String computeFQCN(ClassDoc classDoc)
  {
    return ClassResource.computeFQCN(classDoc.containingPackage().name(),
                                     classDoc.typeName());
  }

  private GenericType extractGenericType(ClassDoc classDoc)
  {
    return GenericType.create(new ClassResource(computeFQCN(classDoc)));
  }

  private GenericVariable extractGenericVariable(TypeVariable typeVariable)
  {
    return new GenericVariable(typeVariable.qualifiedTypeName());
  }

  private GenericTypeVariable extractGenericTypeVariable(TypeVariable typeVariable)
  {
    return new GenericTypeVariable(typeVariable.qualifiedTypeName(),
                                   extractTypes(typeVariable.bounds()));
  }

  private ParameterModel extractParameter(Parameter parameter)
  {
    return new ParameterModel(parameter.name(),
                              extractType(parameter.type()),
                              extractAnnotations(parameter.annotations()));
  }

  private AnnotationsModel extractAnnotations(AnnotationDesc[] annotations)
  {
    List<AnnotationModel> list = new ArrayList<AnnotationModel>(annotations.length);

    for(AnnotationDesc annotation : annotations)
    {
      list.add(new AnnotationModel(new Annotation((GenericType) extractType(annotation.annotationType()),
                                                   extractAnnotationValues(annotation.elementValues()))));
    }

    return new AnnotationsModel(list);
  }

  private Collection<AnnotationElement> extractAnnotationValues(AnnotationDesc.ElementValuePair[] evps)
  {
    Collection<AnnotationElement> avs = new ArrayList<AnnotationElement>(evps.length);
    for(AnnotationDesc.ElementValuePair evp : evps)
    {
      avs.add(new AnnotationElement(evp.element().name(), extractAnnotationValue(evp.value())));
    }
    return avs;
  }

//  private static void dump(ClassDoc classDoc)
//  {
//    Method[] methods = ClassDoc.class.getMethods();
//    for(Method method : methods)
//    {
//      if(method.getParameterTypes().length == 0)
//      {
//        try
//        {
//          Object res = method.invoke(classDoc);
//          if(res != null && method.getReturnType().isArray())
//          {
//            StringBuilder array = new StringBuilder("[");
//            int len = Array.getLength(res);
//            for(int i = 0; i < len; i++)
//            {
//              if(i > 0)
//                array.append(",");
//              array.append(Array.get(res, i));
//            }
//            array.append("]");
//            System.out.println(method.getName() + " => " + array);
//          }
//          else
//            System.out.println(method.getName() + " => " + res);
//        }
//        catch(IllegalAccessException e)
//        {
//          throw new RuntimeException(e);
//        }
//        catch(InvocationTargetException e)
//        {
//          throw new RuntimeException(e);
//        }
//      }
//    }
//  }

  /**************************************************************************
   * The doclet API requires a static method :(
   * So exchanging data through thread local variable...
   **************************************************************************/
  private static final ThreadLocal<KiwidocDoclet> _threadLocalInstance =
    new ThreadLocal<KiwidocDoclet>();

  /**
   * The doclet API requires a static method :(
   */
  public static boolean start(RootDoc root)
  {
    _threadLocalInstance.set(null);
    KiwidocDoclet doclet = new KiwidocDoclet(root);
    doclet.generateKiwidoc();
    _threadLocalInstance.set(doclet);
    return true;
  }

  /**
   * This method is necessary for the javadoc process to know which version of the language is
   * supported. It took me many many hours to figure this out!
   */
  public static LanguageVersion languageVersion()
  {
    return LanguageVersion.JAVA_1_5;
  }

  /**
   * This call has a side effect and is meant to be used right after javadoc is finished. After
   * calling this method, the current doclet is discarded.
   *
   * @return the doclet used during the processing
   */
  public static KiwidocDoclet getCurrentKiwidocDoclet()
  {
    KiwidocDoclet kiwidocDoclet = _threadLocalInstance.get();
    _threadLocalInstance.set(null);
    return kiwidocDoclet;
  }

}
