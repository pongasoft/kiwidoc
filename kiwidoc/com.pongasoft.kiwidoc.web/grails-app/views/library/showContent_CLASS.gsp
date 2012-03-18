%{--
  - Copyright (c) 2012 Yan Pujante
  -
  - Licensed under the Apache License, Version 2.0 (the "License"); you may not
  - use this file except in compliance with the License. You may obtain a copy of
  - the License at
  -
  - http://www.apache.org/licenses/LICENSE-2.0
  -
  - Unless required by applicable law or agreed to in writing, software
  - distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  - WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  - License for the specific language governing permissions and limitations under
  - the License.
  --}%

<%@ page import="com.pongasoft.kiwidoc.model.ClassModel" contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <meta name="layout" content="main"/>
  <meta name="Description" content="Type: Class, Name: ${content.resource.fqcn.encodeAsHTML()}, Location: ${content.resource.libraryVersionResource.encodeAsHTML()}, View: ${preferences.isViewModePrivate ? 'private': 'public'} javadoc API"/>
  <title><pk:renderModelResource model="${content}"/> javadoc api - kiwidoc</title>
  <link rel="stylesheet" href="${resource(dir:'css',file:'class.css')}" />
</head>
<body>
<g:set var="lvr" value="${content.resource.libraryVersionResource}"/>
<h1 class="heading"><pk:resourcePath resource="${content.resource}"/></h1>

<div id="content">
  <g:if test="${!content.publicAPI}">
    <div class="${ModelUtils.apiClass(content)} warning-api-x">This class is not part of the public API.</div>
  </g:if>
<h2 class="title">${content.classKindString}: ${content.resource.fqcnWithDot.encodeAsHTML()}<% /* generics */ %><pk:split in="${content.genericTypeVariables.genericTypeVariables}" separator=", " var="gtv" begin="&lt;" end="&gt;"><pt:renderType type="${gtv}"/></pk:split></h2>
<div class="classDefinition">
  <g:if test="${content.annotations}">
    <div class="annotations">
      <ul>
        <g:each var="annotation" in="${content.annotations}">
          <li>
            <pa:renderAnnotation annotation="${annotation.annotation}"/>
          </li>
        </g:each>
      </ul>
    </div>
  </g:if>
  <div id="declaration">
    <ul>
      <li>
      <% /* access modifier(s) */ %><pk:split in="${ModelUtils.splitAccessForClass(content.access)}" separator=" " var="modifier">${modifier}</pk:split>
      <% /* kind */ %><g:if test="${content.isAnnotation()}">@</g:if><g:else>${content.classKind.name().toLowerCase()} </g:else><span class="class">${content.resource.simpleClassNameWithDot}<% /* generics */ %><pk:split in="${content.genericTypeVariables.genericTypeVariables}" separator=", " var="gtv" begin="&lt;" end="&gt;"><pt:renderType type="${gtv}"/></pk:split></span>
      </li>
      <li class="extends">
      <% /* extends */ %><g:if test="${content.superClass && !ModelUtils.isObject(content.superClass)}">extends <pt:renderType type="${content.superClass}"/></g:if>
      </li>
      <li class="implements">
      <% /* implements */ %><pk:split in="${content.interfaces}" separator=", " var="iface" begin="${content.classKind == ClassModel.ClassKind.INTERFACE ? 'extends': 'implements'} "><pt:renderType type="${iface}"/></pk:split>
      </li>
    </ul>
  </div>
</div>
<pd:renderDoc doc="${content.doc}" resource="${content.resource}" groupByStyle="class"/>
<g:if test="${inheritance}">
<div id="inheritance">
  <a name="refInheritance" id="refInheritance"></a>
  <h2 class="heading">Inheritance</h2>
  <g:if test="${inheritance?.superClassHierarchy}">
    Superclass tree:
    <ul class="extends">
      <pk:split var="entry" in="${inheritance.superClassHierarchy.reverse()}">
        <li class="extends">
          <g:if test="${entry.baseClass}">
            <pk:inheritanceSpacer size="${idx}"/><pt:renderType type="${entry.baseClass.type}" style="[fqcnOnly: true]"/>
          </g:if>
          <g:else>
            <pk:inheritanceSpacer size="${idx}"/>${entry.baseResource.fqcn.encodeAsHTML()}
          </g:else>
        </li>
      </pk:split>
      <li><pk:inheritanceSpacer size="${inheritance.superClassHierarchy.size()}"/><pt:renderType type="${content.type}" style="[fqcnOnly: true, dontRenderGenerics: true, dontRenderLink: true]"/></li>
    </ul>
  </g:if>
  <g:if test="${inheritance?.interfaceHierarchy}">
    Implements:
    <ul class="implements">
      <pk:split var="entry" in="${inheritance.interfaceHierarchy}">
        <li class="implements Interface i${idx}">
          <g:if test="${entry.baseClass}">
            <pt:renderType type="${entry.baseClass.type}" style="[simpleNameOnly: true]"/>
          </g:if>
          <g:else>
            ${entry.baseResource.fqcn.encodeAsHTML()}
          </g:else>
        </li>
      </pk:split>
    </ul>
  </g:if>
</div>
</g:if>  
<g:if test="${content.allMethods}">
<a name="methods" id="methods"></a>
<div class="entries methods">
  <h2 class="heading"><g:if test="${content.isAnnotation()}">Elements</g:if><g:else>Methods</g:else></h2>
  <ul>
  <g:each in="${ModelUtils.sortByName(content.allMethods)}" var="method">
    <a name="${method.memberName.encodeAsHTML()}"></a>
    <li class="entry entry${pk.publicAPI(test: method.publicAPI)}">
    <h3 class="entryName entry${pk.publicAPI(test: method.publicAPI)}">${method.name}<span class="method-links"><a href="#top">top</a></span></h3>
    <div class="entrySpec">
      <g:if test="${method.annotations}">
        <div class="annotations">
          <ul>
            <g:each var="annotation" in="${method.annotations}">
              <li>
                <pa:renderAnnotation annotation="${annotation.annotation}"/>
              </li>
            </g:each>
          </ul>
        </div>
      </g:if>
      <pk:renderMethod method="${method}" style="full"/>
      <% /* annotation default value*/ %><g:if test="${method.annotationDefaultValue}">default <pa:renderAnnotation annotation="${method.annotationDefaultValue}"/></g:if>
    </div>

      <pd:renderDoc doc="${method.doc}" resource="${method.getResource(content.resource)}" groupByStyle="methods"/>

      <g:if test="${inheritance?.model}">
        <div class="methodInheritance">
        <pk:dl title="Specified by:" in="${inheritance.model.findInterfacesSpecifying(method)}" var="c">
          <pk:contentLink resource="${c.findMethodResource(method.memberName)}">${method.name.encodeAsHTML()}</pk:contentLink> from <pt:renderType type="${c.type}"/>
        </pk:dl>
        <pk:dl title="Override hierarchy:" in="${inheritance.model.findSuperClassesImplementing(method)}" var="c">
          <pk:contentLink resource="${c.findMethodResource(method.memberName)}">${method.name.encodeAsHTML()}</pk:contentLink> from <pt:renderType type="${c.type}"/>
        </pk:dl>
        </div>
      </g:if>

      <g:set var="searchQueryString" value="${method.resource.packageName + ' ' + method.resource.classResource.simpleClassNameWithDot + ' ' + method.name}"/>
      <dl class="extras">
        <dt class="relatedLinks">Related Links:</dt>
        <dd class="googleCodeSearch"><a target="relatedLinks" href="http://www.google.com/codesearch?hl=en&lr=&q=${searchQueryString.encodeAsURL()}">Google Code Search</a></dd>
        <dd class="stackOverflow"><a target="relatedLinks" href="http://stackoverflow.com/search?q=${searchQueryString.encodeAsURL()}">Stack Overflow</a></dd>
      </dl>

    </li>
  </g:each>
  </ul>
</div>
</g:if><% /* End methods */ %>
<g:if test="${content.allFields}">
<a name="fields" id="fields"></a>
<div class="entries fields">
  <h2 class="heading">Fields</h2>
  <ul>
  <g:each in="${ModelUtils.sortByName(content.allFields)}" var="field">
    <a name="${field.memberName.encodeAsHTML()}"></a>
    <li class="entry entry${pk.publicAPI(test: field.publicAPI)}">
    <h3 class="entryName entry${pk.publicAPI(test: field.publicAPI)}">${field.name}</h3>
    <div class="entrySpec">
    <g:if test="${field.annotations}">
      <div class="annotations">
        <ul>
          <g:each var="annotation" in="${field.annotations}">
            <li>
              <pa:renderAnnotation annotation="${annotation.annotation}"/>
            </li>
          </g:each>
        </ul>
      </div>
    </g:if>
    <pk:renderField field="${field}" style="full"/>
    </div>
    <pd:renderDoc doc="${field.doc}" resource="${field.getResource(content.resource)}"/>
    </li>
  </g:each>
  </ul>
</div>
</g:if><% /* End fields */ %>
</div> <% /* end content */ %>
<div id="sidebar">
  <div id="sidebar-content">
    <% /* history */ %>
    <g:render template="/block/block" model="[action: 'h', args: [r: pk.resourceURI(resource: content.resource), viewMode: params.viewMode]]"/>
    <g:if test="${content.isEnum() && content.allEnumConstants}">
      <div id="sidebar-enumConstants" class="entry">
        <a name="enumConstantsSummary" id="enumConstantsSummary"></a>
        <h2><a href="#fields" title="Shortcut to Fields">Enum constants</a></h2>
        <ul>
          <g:each var="field" in="${content.allEnumConstants}">
            <li><a href="#${field.memberName.encodeAsHTML()}" title="${pk.renderField(field: field, style: 'summaryTitle')}">${field.name}</a></li>
          </g:each>
        </ul>
      </div>
    </g:if>
    <g:if test="${methods}">
      <div id="sidebar-methods" class="entry">
        <a name="methodsSummary" id="methodsSummary"></a>
        <h2><a href="#methods" title="Shortcut to Methods"><g:if test="${content.isAnnotation()}">Elements</g:if><g:else>Methods</g:else></a></h2>
        <ul>
        <g:each in="[constructors: 'Constructors', staticMethods: 'Static', otherMethods: 'Members']" var="entry">
          <g:if test="${methods[entry.key]}">
            <h3>${entry.value}
              <g:if test="${methods.inheritedMethods && entry.value == 'Members'}">
                [<a href="#" class="js" onclick="toggleShowHide('sidebar-methods', 'inheritedMethod');return false;"> +/- </a>]
              </g:if>
            </h3>
            <div class="method-type">
              <g:each var="method" in="${ModelUtils.sortByName(methods[entry.key])}">
                <li class="${method.resource.parent == content.resource ? 'method' : 'inheritedMethod hidden'} ${ModelUtils.apiClass(method)}"><div class="${ModelUtils.typeClass(method)}"><div class="I-icon ${ModelUtils.inheritanceClass(method, inheritance?.model)}"><pk:contentLink resource="${method.resource}" title="${pk.renderMethod(method: method, style: 'summaryTitle')}"><pk:renderMethod method="${method}" style="summary"/></pk:contentLink></div></div></li>
              </g:each>
            </div>
          </g:if>
        </g:each>
      </ul>
      </div>
    </g:if>
    <g:if test="${content.allNonEnumConstants}">
      <div id="sidebar-fields" class="entry">
        <a name="fieldsSummary" id="fieldsSummary"></a>
        <h2><a href="#fields" title="Shortcut to Fields">Fields</a></h2>
        <ul>
          <g:each var="field" in="${content.allNonEnumConstants}">
            <li class="${ModelUtils.apiClass(field)}"><div class="${ModelUtils.typeClass(field)}"><a href="#${field.memberName.encodeAsHTML()}" title="${pk.renderField(field: field, style: 'summaryTitle')}"><pk:renderField field="${field}" style="summary"/></a></div></li>
          </g:each>
        </ul>
      </div>
    </g:if>
    <div id="sidebar-inheritance" class="entry">
      <h2><a href="#refInheritance" title="Shortcut to inheritance">Inheritance</a></h2>
      <g:if test="${inheritance}">
        <g:if test="${inheritance?.interfaceHierarchy}">
          <div class="implements">
            <h3>Implements</h3>
            <ul>
              <g:each var="entry" in="${inheritance.interfaceHierarchy.reverse()}">
                <li class="implements ${ModelUtils.apiClass(entry.baseClass)}"><div class="T-icon CIP">
                  <g:if test="${entry.baseClass}">
                    <pt:renderType type="${entry.baseClass.type}" style="[simpleNameOnly: true, oneLinkOnly: true]"/>
                  </g:if>
                  <g:else>
                    ${entry.baseResource.fqcn.encodeAsHTML()}
                  </g:else>
                </div></li>
              </g:each>
            </ul>
          </div>
        </g:if>
        <g:if test="${inheritance?.superClassHierarchy}">
          <div class="extends">
            <h3>Extends</h3>
            <ul>
              <pk:split var="entry" in="${inheritance.superClassHierarchy.reverse()}">
                <li class="extends ${ModelUtils.apiClass(entry.baseClass)}"><div class="T-icon CCP">
                  <g:if test="${entry.baseClass}">
                    <pt:renderType type="${entry.baseClass.type}" style="[oneLinkOnly: true]"/>
                  </g:if>
                  <g:else>
                    ${entry.baseResource.fqcn.encodeAsHTML()}
                  </g:else>
                </div></li>
              </pk:split>
            </ul>
          </div>
        </g:if>
      </g:if>
      <h3>Class</h3>
      <ul>
        <li class="${ModelUtils.apiClass(content)}"><div class="${ModelUtils.typeClass(content)}"><pt:renderType type="${content.type}" style="[dontRenderLink: true]"/></div></li>
      </ul>
    </div> <% /* sidebar-inheritance */ %>
    <g:if test="${content.innerClasses}">
      <div id="sidebar-innerClasses" class="entry">
        <h2>Inner Classes</h2>
        <ul>
          <g:each var="innerClass" in="${content.innerClasses.values()}">
            <li class="${ModelUtils.apiClass(innerClass)}"><div class="${ModelUtils.typeClass(innerClass)}"><pk:contentLink resource="${innerClass.classResource}"><span class="type ${pk.classKind(model: innerClass)}"><pt:renderType type="${innerClass.type}" style="[simpleNameOnly: true, dontRenderLink: true]" /></span></pk:contentLink></div></li>
          </g:each>
        </ul>
      </div>
    </g:if>
    <g:if test="${content.outerClass}">
      <div id="sidebar-outerClasses" class="entry">
        <h2>Outer Class</h2>
        <ul>
          <li class="${ModelUtils.apiClass(content.outerClass)}"><div class="${ModelUtils.typeClass(content.outerClass)}"><pk:contentLink resource="${content.outerClass.classResource}"><span class="type ${pk.classKind(model: content.outerClass)}"><pt:renderType type="${content.outerClass.type}" style="[simpleNameOnly: true, dontRenderLink: true]"/></span></pk:contentLink></div></li>
        </ul>
      </div>
    </g:if>
    <g:if test="${content.allLibrarySubclasses}">
      <div id="sidebar-subclasses" class="entry">
        <h2>Children</h2>
        <ul>
          <g:each var="subclass" in="${ModelUtils.sortByName(content.allLibrarySubclasses)}">
            <li class="${ModelUtils.apiClass(subclass)}"><div class="${ModelUtils.typeClass(subclass)}"><pk:contentLink resource="${subclass.classResource}"><span class="type ${pk.classKind(model: subclass)}"><pt:renderType type="${subclass.type}" style="[simpleNameOnly: true, dontRenderLink: true, dontRenderGenerics: true]"/></span></pk:contentLink></div></li>
          </g:each>
        </ul>
      </div>
    </g:if>
  </div>
</div>
</body>
</html>