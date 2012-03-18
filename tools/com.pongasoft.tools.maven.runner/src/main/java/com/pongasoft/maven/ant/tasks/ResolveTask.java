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

package com.pongasoft.maven.ant.tasks;

import org.apache.maven.artifact.ant.AbstractArtifactWithRepositoryTask;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.model.Dependency;
import org.apache.maven.project.DefaultProjectBuilderConfiguration;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuilderConfiguration;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yan@pongasoft.com
 */
public class ResolveTask extends AbstractArtifactWithRepositoryTask
{
  public static class Artifact
  {
    private final String _groupId;
    private final String _artifactId;
    private final String _version;
    private final File _file;

    public Artifact(String groupId, String artifactId, String version, File file)
    {
      _groupId = groupId;
      _artifactId = artifactId;
      _version = version;
      _file = file;
    }

    public String getGroupId()
    {
      return _groupId;
    }

    public String getArtifactId()
    {
      return _artifactId;
    }

    public String getVersion()
    {
      return _version;
    }

    public File getFile()
    {
      return _file;
    }

    @Override
    public String toString()
    {
      StringBuilder sb = new StringBuilder();
      sb.append("c=");
      sb.append(_groupId).append(':').append(_artifactId).append(':').append(_version);
      sb.append(";f=").append(_file);
      return sb.toString();
    }
  }

  private Map<String, Object> _result = new HashMap<String, Object>();

  private Dependency _dependency;
  private boolean _artifactOnly = false;
  private boolean _sources = false;
  private boolean _javadoc = false;
  private boolean _verbose = false;
  private boolean _optional = true;

  private ArtifactResolver _resolver;
  private ArtifactRepository _localRepo;
  private ArtifactFactory _artifactFactory;
  private List _remoteArtifactRepositories;

  public Map<String, Object> getResult()
  {
    return _result;
  }

  @Override
  protected void doExecute()
  {
    if(_dependency == null)
      throw new BuildException("mising dependency");

    _localRepo = createLocalArtifactRepository();
    log("Using local repository: " + _localRepo.getBasedir(), Project.MSG_VERBOSE);

    _resolver = (ArtifactResolver) lookup(ArtifactResolver.ROLE);
    _artifactFactory = (ArtifactFactory) lookup(ArtifactFactory.ROLE);

    MavenProject mavenProject = createMavenProject();
    _remoteArtifactRepositories = createRemoteArtifactRepositories(mavenProject.getRepositories());

    log("Resolving " + _dependency, Project.MSG_VERBOSE);

    Artifact mainArtifact;

    try
    {
      mainArtifact = resolveJarArtifact(null);
      _result.put("artifact", mainArtifact);
    }
    catch(ArtifactResolutionException e)
    {
      throw new BuildException("Unable to resolve artifact: " + e.getMessage(), e);
    }
    catch(ArtifactNotFoundException e)
    {
      throw new BuildException("Unable to resolve artifact: " + e.getMessage(), e);
    }

    try
    {
      if(_sources)
        _result.put("sources", resolveJarArtifact("sources"));
      if(_javadoc)
        _result.put("javadoc", resolveJarArtifact("javadoc"));
    }
    catch(ArtifactResolutionException e)
    {
      throw new BuildException("Unable to resolve artifact: " + e.getMessage(), e);
    }
    catch(ArtifactNotFoundException e)
    {
      // ok... sources and javadocs are optional
    }

    if(!_artifactOnly)
    {
      resolveTransitively(mainArtifact);
    }
  }

  private MavenProject createMavenProject()
  {
    MavenProjectBuilder projectBuilder = (MavenProjectBuilder) lookup(MavenProjectBuilder.ROLE);
    try
    {
      ProjectBuilderConfiguration config = new DefaultProjectBuilderConfiguration();
      config.setLocalRepository(_localRepo).setGlobalProfileManager(getProfileManager());
      return projectBuilder.buildStandaloneSuperProject(config);
    }
    catch(ProjectBuildingException e)
    {
      throw new BuildException(e);
    }
  }

  private Artifact resolveJarArtifact(String classifier)
    throws ArtifactResolutionException, ArtifactNotFoundException
  {
    return resolveArtifact("jar", classifier);
  }

  private Artifact resolveArtifact(String type, String classifier)
    throws ArtifactResolutionException, ArtifactNotFoundException
  {
    org.apache.maven.artifact.Artifact artifact =
      _artifactFactory.createArtifactWithClassifier(_dependency.getGroupId(),
                                                    _dependency.getArtifactId(),
                                                    _dependency.getVersion(),
                                                    type,
                                                    classifier);

    _resolver.resolve(artifact, _remoteArtifactRepositories, _localRepo);

    return createArtifact(artifact);
  }

  private Artifact createArtifact(org.apache.maven.artifact.Artifact artifact)
  {
    Artifact result = new Artifact(artifact.getGroupId(),
                                   artifact.getArtifactId(),
                                   artifact.getVersion(),
                                   artifact.getFile());

    return result;
  }

  @SuppressWarnings("unchecked")
  private void resolveTransitively(Artifact mainArtifact)
  {
    try
    {
      Artifact pomArtifact = resolveArtifact("pom", null);
      MavenProjectBuilder projectBuilder = (MavenProjectBuilder) lookup(MavenProjectBuilder.ROLE);
      MavenProject mavenProject =
        projectBuilder.buildWithDependencies(pomArtifact.getFile(),
                                             _localRepo,
                                             getProfileManager());

      Set<String> dependencies = new HashSet<String>();

      for(Dependency d : (List<Dependency>) mavenProject.getDependencies())
      {
        dependencies.add(d.getGroupId() + ":" +
                         d.getArtifactId() + ":" +
                         d.getVersion() + ":" +
                         d.getClassifier() + ":" +
                         d.getType() + ":" +
                         d.getScope());
      }

      Collection<Artifact> directDependencies = new ArrayList<Artifact>();
      Collection<Artifact> transitiveDependencies = new ArrayList<Artifact>();
      Collection<Artifact> optionalDependencies = new ArrayList<Artifact>();
      Collection<Artifact> classpath = new ArrayList<Artifact>();
      classpath.add(mainArtifact);

      for(org.apache.maven.artifact.Artifact a : (Set<org.apache.maven.artifact.Artifact>) mavenProject.getArtifacts())
      {
        String scope = a.getScope();
        if("compile".equals(scope) || "runtime".equals(scope))
        {
          Artifact artifact = createArtifact(a);

          if(a.isOptional() && _optional)
          {
            classpath.add(artifact);
            optionalDependencies.add(artifact);
          }
          else
          {
            classpath.add(artifact);

          }
          if(dependencies.contains(a.getGroupId() + ":" +
                                   a.getArtifactId() + ":" +
                                   a.getVersion() + ":" +
                                   a.getClassifier() + ":" +
                                   a.getType() + ":" +
                                   a.getScope()))
          {
            directDependencies.add(artifact);
          }
          else
          {
            transitiveDependencies.add(artifact);
          }
        }
      }


      _result.put("classpath", classpath);
      _result.put("transitiveDependencies", transitiveDependencies);
      _result.put("directDependencies", directDependencies);
      _result.put("optionalDependencies", optionalDependencies);
    }
    catch(ProjectBuildingException e)
    {
      throw new BuildException(e);
    }
    catch(ArtifactNotFoundException e)
    {
      throw new BuildException(e);
    }
    catch(ArtifactResolutionException e)
    {
      throw new BuildException(e);
    }
  }

  public void addDependency(Dependency dependency)
  {
    _dependency = dependency;
  }

  public boolean isArtifactOnly()
  {
    return _artifactOnly;
  }

  public void setArtifactOnly(boolean artifactOnly)
  {
    _artifactOnly = artifactOnly;
  }

  public boolean isSources()
  {
    return _sources;
  }

  public void setSources(boolean sources)
  {
    _sources = sources;
  }

  public boolean isJavadoc()
  {
    return _javadoc;
  }

  public void setJavadoc(boolean javadoc)
  {
    _javadoc = javadoc;
  }

  public boolean isVerbose()
  {
    return _verbose;
  }

  public void setVerbose(boolean verbose)
  {
    _verbose = verbose;
  }

  public boolean isOptional()
  {
    return _optional;
  }

  public void setOptional(boolean optional)
  {
    _optional = optional;
  }
}