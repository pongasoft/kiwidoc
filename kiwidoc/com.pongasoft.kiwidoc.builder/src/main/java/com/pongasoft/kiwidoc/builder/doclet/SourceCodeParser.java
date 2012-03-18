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

import com.pongasoft.kiwidoc.builder.model.LibraryModelBuilder;
import com.pongasoft.kiwidoc.model.DocModel;
import com.pongasoft.kiwidoc.model.type.UnresolvedType;
import com.pongasoft.util.core.exception.InternalException;
import com.pongasoft.util.core.io.IOUtils;
import com.sun.tools.javadoc.Main;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSelectInfo;
import org.apache.commons.vfs.FileSelector;
import org.apache.commons.vfs.NameScope;
import org.apache.commons.vfs.impl.DefaultFileReplicator;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;
import org.apache.commons.vfs.provider.local.DefaultLocalFileProvider;
import org.linkedin.util.reflect.ReflectUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Parse the source code using the doclet api.
 *  
 * @author yan@pongasoft.com
 */
public class SourceCodeParser
{
  public static final Log log = LogFactory.getLog(SourceCodeParser.class);
  public static final Log javadocLog = LogFactory.getLog(SourceCodeParser.class.getName() + ".javadoc");

  private final static String JAVA_EXTENSION = "java";
  private final static String HTML_EXTENSION = "html";

  private static final class SourcesSelector implements FileSelector
  {
    public static final SourcesSelector INSTANCE = new SourcesSelector();

    public boolean includeFile(FileSelectInfo fileSelectInfo) throws Exception
    {
      String extension = fileSelectInfo.getFile().getName().getExtension();
      return extension.equals(JAVA_EXTENSION) || extension.equals(HTML_EXTENSION);
    }

    public boolean traverseDescendents(FileSelectInfo fileSelectInfo) throws Exception
    {
      return true;
    }
  }

  private static final class JavaSelector implements FileSelector
  {
    public static final JavaSelector INSTANCE = new JavaSelector();

    public boolean includeFile(FileSelectInfo fileSelectInfo) throws Exception
    {
      return fileSelectInfo.getFile().getName().getExtension().equals(JAVA_EXTENSION);
    }

    public boolean traverseDescendents(FileSelectInfo fileSelectInfo) throws Exception
    {
      return true;
    }
  }

  private static final class DependenciesSelector implements FileSelector
  {
    public static final DependenciesSelector INSTANCE = new DependenciesSelector();

    public boolean includeFile(FileSelectInfo fileSelectInfo) throws Exception
    {
      return true;
    }

    public boolean traverseDescendents(FileSelectInfo fileSelectInfo) throws Exception
    {
      return false;
    }
  }

  public SourceCodeParser()
  {
  }

  /**
   * Parses the sources using a doclet. It is ok to provide a big number of files, since it will
   * automatically be split in chunks.
   *
   * @param library the library to store the results
   * @param sources the sources
   * @return the number of processed files
   * @throws IOException if there is an issue reading the files
   */
  public int parseSources(LibraryModelBuilder library,
                          FileObject sources,
                          String overviewFilename,
                          FileObject javadoc,
                          Collection<FileObject> dependencies) throws IOException, InternalException
  {
    DefaultFileSystemManager dfs = new DefaultFileSystemManager();
    dfs.addProvider("file", new DefaultLocalFileProvider());
    dfs.setReplicator(new DefaultFileReplicator(IOUtils.createTempDirectory("SourceCodeParser", "")));
    dfs.init();
    try
    {
      File localSources = dfs.getReplicator().replicateFile(sources, SourcesSelector.INSTANCE);

      sources = dfs.toFileObject(localSources);

      FileObject[] allSources = sources.findFiles(JavaSelector.INSTANCE);

      if(allSources == null || allSources.length == 0)
        return 0;

      List<String> sourcePath = new ArrayList<String>();

      for(FileObject sourceFile : allSources)
      {
//        if(javadoc != null)
//        {
//          String name = sources.getName().getRelativeName(sourceFile.getName());
//          name = name.substring(0, name.length() - JAVA_EXTENSION.length());
//          name += HTML_EXTENSION;
//          if(javadoc.resolveFile(name, NameScope.DESCENDENT).exists())
//            sourcePath.add(sourceFile.getName().getPath());
//        }
//        else
//        {
//          sourcePath.add(sourceFile.getName().getPath());
//        }
        sourcePath.add(sourceFile.getName().getPath());
      }

      List<String> deps = new ArrayList<String>();

      if(dependencies != null)
      {
        for(FileObject dependency : dependencies)
        {
          if(dependency.exists())
          {
            File localDependency = dfs.getReplicator().replicateFile(dependency,
                                                                     DependenciesSelector.INSTANCE);
            deps.add(localDependency.getCanonicalPath());
          }
        }
      }

      FileObject overviewFile = sources.resolveFile(overviewFilename, NameScope.CHILD);

      String overviewPath = null;
      if(overviewFile.exists())
        overviewPath = overviewFile.getName().getPath();

      parseSources(library, overviewPath, sourcePath, deps);

      return sourcePath.size();
    }
    finally
    {
      dfs.close();
    }
  }

  private void parseSources(LibraryModelBuilder library,
                            String overviewPath,
                            List<String> sourcePath, 
                            List<String> dependencies)
    throws IOException, InternalException
  {
    StringWriter errors = new StringWriter();
    final PrintWriter errWriter = new PrintWriter(errors);
    StringWriter warnings = new StringWriter();
    final PrintWriter warnWriter = new PrintWriter(warnings);
    StringWriter notices = new StringWriter();
    final PrintWriter noticeWriter = new PrintWriter(notices);

    final List<String> args = new ArrayList<String>();
    if(!dependencies.isEmpty())
    {
      args.add("-classpath");
      StringBuilder sb = new StringBuilder();
      for(String dependency : dependencies)
      {
        if(sb.length() > 0)
          sb.append(File.pathSeparatorChar);
        sb.append(dependency);
      }
      args.add(sb.toString());
    }
    args.addAll(sourcePath);
    args.add("-private");

    args.add("-source");
    args.add("1." + computeJdkVersion(library.getJdkVersion()));

    if(overviewPath != null)
    {
      args.add("-overview");
      args.add(overviewPath);
    }

    int retCode = 0;
    try
    {
      retCode = ReflectUtils.executeWithClassLoader(KiwidocDoclet.class.getClassLoader(),
                                                    new Callable<Integer>()
      {
        public Integer call() throws Exception
        {
          return Main.execute("kiwidoc",
                              errWriter,
                              warnWriter,
                              noticeWriter,
                              KiwidocDoclet.class.getName(),
                              args.toArray(new String[args.size()]));
        }
      });
    }
    catch(IOException e)
    {
      throw e;
    }
    catch(Exception e)
    {
      throw new InternalException(e);
    }

    KiwidocDoclet doclet = KiwidocDoclet.getCurrentKiwidocDoclet();

    errWriter.close();
    warnWriter.close();
    noticeWriter.close();

    if(warnings.toString().length() > 0)
      javadocLog.warn(warnings);

    if(notices.toString().length() > 0 && log.isDebugEnabled())
    {
      javadocLog.debug(notices);
    }

    if(retCode != 0)
    {
      if(doclet == null || doclet.getClassModels().size() == 0)
        throw new IOException(errors.toString());
      else
        log.warn("There were some errors during processing (ignored): " + errors.toString());
    }

    Map<String, UnresolvedType> unresolvedTypes = doclet.getUnresolvedTypes();
    if(!unresolvedTypes.isEmpty())
      log.warn("Unresolved Types: " + unresolvedTypes.keySet());
    
    library.setOverview(doclet.getOverview());
    library.addClasses(doclet.getClassModels());

    for(Map.Entry<String, DocModel> entry : doclet.getPackageInfos().entrySet())
    {
      library.setPackageInfo(entry.getKey(), entry.getValue());
    }
  }

  // legal values are 3..5
  private int computeJdkVersion(int jdkVersion)
  {
    // unknown
    if(jdkVersion <= 0)
      return 5;

    if(jdkVersion < 3)
      return 3;

    return jdkVersion;
  }
}
