
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

import com.pongasoft.kiwidoc.builder.model.ClassModelBuilder;
import com.pongasoft.kiwidoc.builder.model.LibraryModelBuilder;
import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSelectInfo;
import org.apache.commons.vfs.FileSelector;
import org.objectweb.asm.ClassReader;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

/**
 * @author yan@pongasoft.com
 */
public class ByteCodeParser
{
  private final static String CLASS_EXTENSION = "class";
  public static final int ASM_SKIP_ALL = ClassReader.SKIP_FRAMES;

  private static final class ClassFileSelector implements FileSelector
  {
    public static final ClassFileSelector INSTANCE = new ClassFileSelector();
    public static final Pattern ANONYMOUS_CLASSES = Pattern.compile(".*\\$[0-9]+.*\\." + CLASS_EXTENSION + "$");  

    public boolean includeFile(FileSelectInfo fileSelectInfo) throws Exception
    {
      FileName name = fileSelectInfo.getFile().getName();
      return name.getExtension().equals(CLASS_EXTENSION) &&
             !ANONYMOUS_CLASSES.matcher(name.getBaseName()).matches();
    }

    public boolean traverseDescendents(FileSelectInfo fileSelectInfo) throws Exception
    {
      return true;
    }
  }

  /**
   * Constructor
   */
  public ByteCodeParser()
  {
  }

  public void parseClasses(LibraryModelBuilder library, FileObject classesResource) throws IOException
  {
    FileObject[] classes = classesResource.findFiles(ClassFileSelector.INSTANCE);

    // TODO MED YP: handle inner classes (classModelBuilder.addInnerClass...)  
    if(classes != null)
    {
      for (FileObject classResource : classes)
      {
        ClassModelBuilder classModelBuilder;

        InputStream is = classResource.getContent().getInputStream();
        try
        {
          ClassReader reader = new ClassReader(new BufferedInputStream(is));
          KiwidocClassVisitor classParser = new KiwidocClassVisitor();
          reader.accept(classParser, 0);
          classModelBuilder = classParser.getClassModel();
        }
        finally
        {
          is.close();
        }

        library.addClass(classModelBuilder);
      }
    }
  }
}
