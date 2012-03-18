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

<g:if test="${grails.util.GrailsUtil.isDevelopmentEnv()}"><html>
  <head>
	  <title>Grails Runtime Exception</title>
	  <style type="text/css">
	  		.message {
	  			border: 1px solid black;
	  			padding: 5px;
	  			background-color:#E9E9E9;
	  		}
	  		.stack {
	  			border: 1px solid black;
	  			padding: 5px;
	  			overflow:auto;
	  			height: 300px;
	  		}
	  		.snippet {
	  			padding: 5px;
	  			background-color:white;
	  			border:1px solid black;
	  			margin:3px;
	  			font-family:courier;
	  		}
	  </style>
  </head>

  <body>
    <h1>Grails Runtime Exception</h1>
    <h2>Error Details</h2>

  	<div class="message">
		<strong>Error ${request.'javax.servlet.error.status_code'}:</strong> ${request.'javax.servlet.error.message'.encodeAsHTML()}<br/>
		<strong>Servlet:</strong> ${request.'javax.servlet.error.servlet_name'}<br/>
		<strong>URI:</strong> ${request.'javax.servlet.error.request_uri'}<br/>
		<g:if test="${exception}">
	  		<strong>Exception Message:</strong> ${exception.message?.encodeAsHTML()} <br />
	  		<strong>Caused by:</strong> ${exception.cause?.message?.encodeAsHTML()} <br />
	  		<strong>Class:</strong> ${exception.className} <br />
	  		<strong>At Line:</strong> [${exception.lineNumber}] <br />
	  		<strong>Code Snippet:</strong><br />
	  		<div class="snippet">
	  			<g:each var="cs" in="${exception.codeSnippet}">
	  				${cs?.encodeAsHTML()}<br />
	  			</g:each>
	  		</div>
		</g:if>
  	</div>
	<g:if test="${exception}">
	    <h2>Stack Trace</h2>
	    <div class="stack">
	      <pre><g:each in="${exception.stackTraceLines}">${it.encodeAsHTML()}<br/></g:each></pre>
	    </div>
	</g:if>
  </body>
</html></g:if><g:else><html>
<head>
  <title>kiwidoc - Oops [Error: ${request.'javax.servlet.error.status_code'}]</title>
  <style type="text/css">
  body {
    background-color: #ffffff;
    color: #36702e;
    font-family: trebuchet ms, arial, sans-serif;
    font-size: 0.9em;
    text-align: center;
  }
.error {
  font-size: 5em;
  padding: 0;
  margin: 0;
}
  </style>
</head>
<body>
<div class="content">
<div class="logo"><img src="${resource(dir: 'images', file: 'kiwi_nurse.png')}" alt="kiwidoc"/></div>
  <p class="error">Error: ${request.'javax.servlet.error.status_code'}</p>
  <p>Sorry. It looks like an unexpected error has happened...</p>
  <p>Please report the problem to <a href="mailto:bugs@kiwidoc.com">bugs@kiwidoc.com</a>.</p>
  <p>Go back to the <a href="/java">home page</a>.</p>
</div>
</body>
</html></g:else>
