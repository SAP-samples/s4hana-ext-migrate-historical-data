<#--
  #%L
 License Maven Plugin
 %%
 Copyright (C) 2012 Codehaus, Tony Chemit
 %%
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
      http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 #L%
  -->
<#-- To render the third-party file.
 Available context :

 - dependencyMap a collection of Map.Entry with
   key are dependencies (as a MavenProject) (from the maven project)
   values are licenses of each dependency (array of string)

 - licenseMap a collection of Map.Entry with
   key are licenses of each dependency (array of string)
   values are all dependencies using this license
-->
<#function licenseFormat licenses>
    <#assign result = ""/>
    <#list licenses as license>
        <#assign result = result + "\nLicensed under " + license/>
    </#list>
    <#return result>
</#function>
<#function artifactFormat p>
    <#if p.name?index_of('Unnamed') &gt; -1 || p.name == 'null'>
        <#return p.artifactId + " (" + p.groupId + ":" + p.artifactId + ":" + p.version + " - " + (p.url!"no url defined") + ")">
    <#else>
        <#return p.name + " (" + (p.url!"no url defined") + ")">
    </#if>
</#function>
<#if dependencyMap?size == 0>
The project has no dependencies.
<#else>
This program references the following third party open source or other free download components.
The third party licensors of these components may provide additional license rights,
terms and conditions and/or require certain notices as described below.

    <#list dependencyMap as e>
        <#assign project = e.getKey()/>
        <#assign licenses = e.getValue()/>
${artifactFormat(project)}${licenseFormat(licenses)}

    </#list>
</#if>
