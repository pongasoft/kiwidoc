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

class AutoCompleteTagLib
{
  static namespace = 'pa'

  def grailsUITagLibService

  def autoComplete = {attrs ->
    attrs = grailsUITagLibService.establishDefaultValues(
      [
      id: grailsUITagLibService.getUniqueId(),
      resultName: 'result',
      labelField: 'name',
      href: 'href',
      idField: 'id',
      'class': '',
      style: '',
      useShadow: false,
      minQueryLength: 0,
      queryDelay: 0.2,
      value: '',
      title: '',
      size: '',
      maxlength: '',
      connMethodPost: true,
      queryMatchContains: false
      ],
      attrs,
      ['action|url']
    )
    def resultName = attrs.remove('resultName')
    def idField = attrs.remove('idField')
    def labelField = attrs.remove('labelField')
    def href = attrs.remove('href')
    def id = attrs.remove('id')
    def containerId = "c_${id}"
    def jsid = grailsUITagLibService.toJS(id)
    def name = attrs.name ? attrs.name : id
    def value = attrs.remove('value')
    def queryAppend = attrs.remove('queryAppend')
    def connMethodPost = attrs.remove('connMethodPost')
    def size = attrs.remove('size')
    if(size)
    {
      size = "size=\"${size}\" "
    }
    def maxlength = attrs.remove('maxlength')
    if(maxlength)
    {
      maxlength = "maxlength=\"${maxlength}\""
    }

    // these apparently just need to be removed from the attrs before they are converted into a js config object
    def title = attrs.remove('title')
    def style = attrs.remove('style')
    def cssClass = attrs.remove('class')

    out << """
      <div class="yui-ac">
          <input type="text" class="yui-ac-input" id="${id}" name="${name}" value="${value}" ${size}${maxlength}/>
          <div class="yui-ac-container" id="${containerId}">
          </div>
      </div>
      """
    out << """
              <script type="text/javascript">
                  function init_${jsid}_ac() {
      """

    def dataURL = createLink(attrs)
    //id was removed for the call to createLink
    out << """
                  var dataSource = new YAHOO.widget.DS_XHR('${dataURL}', ['${resultName}','${labelField}','${idField}', '${href}']);
                  dataSource.connMethodPost=${connMethodPost};
                  GRAILSUI.${jsid} = new YAHOO.widget.AutoComplete('${id}', '${containerId}', dataSource);
                  GRAILSUI.${jsid}.dataSource = dataSource;
                  GRAILSUI.${jsid}.prehighlightClassName = 'yui-ac-prehighlight';
                  GRAILSUI.${jsid}.minQueryLength = 0;
                  GRAILSUI.${jsid}.autoHighlight = false;
	   	            // item select event
                  var itemSelectHandler = function(sType, aArgs) {
                      var aData = aArgs[2];
                      var href = aData[2];
                      YAHOO.util.Dom.get('${id}').value = '';
                      window.location.href = href;
                  };
                  GRAILSUI.${jsid}.itemSelectEvent.subscribe(itemSelectHandler);
"""
    // because the autoComplete doesn't access a config object like most YUI widgets, we'll need to
    // add each config value to the autoComplete afterwards

    attrs.each {key, val ->
      out << "GRAILSUI.${jsid}.${key} = ${grailsUITagLibService.valueToConfig(val)};\n"
    }
    out << """
              }
          YAHOO.util.Event.onDOMReady(init_${jsid}_ac);
          </script>"""
  }
}
