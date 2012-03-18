
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

package com.pongasoft.kiwidoc.builder;

import com.pongasoft.kiwidoc.builder.bytecode.ByteCodeParser;
import com.pongasoft.kiwidoc.builder.doclet.SourceCodeParser;
import com.pongasoft.kiwidoc.builder.model.ClassModelBuilder;
import com.pongasoft.kiwidoc.builder.model.LibraryModelBuilder;
import com.pongasoft.kiwidoc.builder.model.PackageModelBuilder;
import com.pongasoft.kiwidoc.model.BuiltFrom;
import com.pongasoft.kiwidoc.model.DependenciesModel;
import com.pongasoft.kiwidoc.model.resource.LibraryVersionResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.NameScope;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.jar.Manifest;

/**
 * @author yan@pongasoft.com
 */
public class KiwidocBuilder
{
  public static final Log log = LogFactory.getLog(KiwidocBuilder.class);

  public static class Library
  {
    /**
     * The library itself
     */
    public LibraryVersionResource libraryVersionResource;

    /**
     * The dependencies (expressed in terms of other library version resource): this value
     * will be stored in the model.
     */
    public DependenciesModel dependencies = DependenciesModel.NO_DEPENDENCIES;

    /**
     * the (optional, <code>null</code> ok) resource containing the classes
     */
    public FileObject classes;

    /**
     * the (optional, <code>null</code> ok) resource containing javadoc
     */
    public FileObject javadoc;

    /**
     * the (optional, <code>null</code> ok) resource containing sources
     */
    public FileObject sources;

    /**
     * Artifact dependencies (used by javadoc to be able to 'parse' the code)
     */
    public Collection<FileObject> classpath = Collections.emptyList();

    /**
     * The version of the jdk (0 means unknown... otherwise 1 through 5)
     */
    public int jdkVersion = 0;

    /**
     * Set to <code>true</code> if you want to eliminate non api classes
     */
    public boolean publicOnly = false;

    /**
     * The name of the overview file
     */
    public String overviewFilename = "overview.html";
  }

  /**
   * Constructor
   */
  public KiwidocBuilder()
  {
  }

  /**
   * Builds the kiwidoc for the given library
   *
   * @param library (optional, <code>null</code> ok) dependencies
   * @return the model builder
   * @throws BuilderException when there is a problem indexing
   */
  public LibraryModelBuilder buildKiwidoc(Library library)
    throws IOException, BuilderException
  {
    Set<BuiltFrom> builtFrom = EnumSet.noneOf(BuiltFrom.class);
    Manifest manifest = null;

    LibraryModelBuilder libFromByteCode = null;

    // if we have the library itself (jar file)
    if(library.classes != null)
    {
      builtFrom.add(BuiltFrom.BYTECODE);
      
      // manifest
      FileObject manifestResource = library.classes.resolveFile("META-INF/MANIFEST.MF");
      if(manifestResource.exists())
      {
        manifest = new Manifest();
        InputStream is = manifestResource.getContent().getInputStream();
        try
        {
          manifest.read(new BufferedInputStream(is));
        }
        finally
        {
          is.close();
        }
      }

      // byte code
      libFromByteCode = new LibraryModelBuilder(library.libraryVersionResource);
      new ByteCodeParser().parseClasses(libFromByteCode, library.classes);
    }

    LibraryModelBuilder libFromSource = null;
    // if we have the source code
    if(library.sources != null)
    {
      try
      {
        libFromSource = new LibraryModelBuilder(library.libraryVersionResource);
        if(libFromByteCode != null)
          libFromSource.setJdkVersion(libFromByteCode.getJdkVersion());
        else
          libFromSource.setJdkVersion(library.jdkVersion);

        int sourceCount = new SourceCodeParser().parseSources(libFromSource,
                                                              library.sources,
                                                              library.overviewFilename,
                                                              library.publicOnly ?
                                                                library.javadoc :
                                                                null,
                                                              library.classpath);

        if(sourceCount == 0)
        {
          libFromSource = null;
          log.warn("No sources found in " + library.sources);
        }
        else
        {
          builtFrom.add(BuiltFrom.SOURCE);
        }
      }
      catch(IOException e)
      {
        throw e;
      }
      catch(Throwable th)
      {
        throw new BuilderException(th);
      }
    }

    // compute which version to use (source code wins if available)
    LibraryModelBuilder lib = new LibraryModelBuilder(library.libraryVersionResource);

    if(libFromByteCode != null)
    {
      lib = libFromByteCode;
    }

    if(libFromSource != null)
    {
      lib = libFromSource;
    }

    log.info("Processed source|bytecode: " +
             (libFromSource == null ? "N/A" : libFromSource.getClassCount()) + " | " +
             (libFromByteCode == null ? "N/A" : libFromByteCode.getClassCount()));

    // TODO MED YP: use byte code for resolving unresolved types during javadoc processing  

    // if we have the original javadoc (used for determining exported classes)
    if(library.javadoc != null)
    {
      builtFrom.add(BuiltFrom.JAVADOC);

      for(PackageModelBuilder packageModelBuilder : lib.getAllPackages())
      {
        for(ClassModelBuilder classModelBuilder : packageModelBuilder.getAllClasses())
        {
          String javadocFile = classModelBuilder.getFqcn();
          javadocFile = javadocFile.replace('.', '/');
          javadocFile = javadocFile.replace('$', '.'); // for inner classes
          javadocFile += ".html";

          try
          {
            classModelBuilder.setExportedClass(library.javadoc.resolveFile(javadocFile,
                                                                           NameScope.DESCENDENT).exists());
          }
          catch(FileSystemException e)
          {
            log.warn("Error while setting exported class on " + javadocFile + " [" + classModelBuilder.getFqcn() + "]", e);
          }
        }
      }
    }

    // dependencies
    lib.setDependencies(library.dependencies);

    // manifest
    lib.setManifest(manifest);

    // built from
    lib.addBuiltFrom(builtFrom);
    
    return lib;
  }
}
