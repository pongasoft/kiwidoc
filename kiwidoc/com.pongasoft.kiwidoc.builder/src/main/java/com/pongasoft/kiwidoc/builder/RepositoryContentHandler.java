
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

import com.pongasoft.kiwidoc.model.RepositoryModel;
import com.pongasoft.kiwidoc.model.resource.LibraryResource;
import com.pongasoft.kiwidoc.model.resource.LibraryVersionResource;
import com.pongasoft.kiwidoc.model.resource.OrganisationResource;
import com.pongasoft.kiwidoc.model.resource.RepositoryResource;
import com.pongasoft.util.core.annotations.FieldInitializer;
import com.pongasoft.util.core.annotations.ObjectInitializer;
import org.apache.commons.vfs.FileDepthSelector;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSelector;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileType;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author yan@pongasoft.com
 */
public class RepositoryContentHandler extends AbstractContentHandler<RepositoryResource, RepositoryModel>
{
  private FileSelector _fileSelector = new FileDepthSelector(1, 1);

  @ObjectInitializer
  public RepositoryContentHandler()
  {
  }

  /**
   * Constructor
   */
  public RepositoryContentHandler(ResourceLocator resourceLocator,
                                 int depth)
  {
    super(resourceLocator);
    setDepth(depth);
  }

  /**
   * Constructor
   */
  public RepositoryContentHandler(ResourceLocator resourceLocator)
  {
    super(resourceLocator);
  }

  @FieldInitializer
  public void setDepth(int depth)
  {
    _fileSelector = new FileDepthSelector(1, depth);
  }

  /**
   * Logic to determine whether a resource exists (depend on implementation).
   *
   * @param resourceFile the file for the content
   * @return <code>true</code> if the content for the given resource exists
   * @throws StoreException
   */
  @Override
  protected boolean doExists(FileObject resourceFile) throws StoreException
  {
    return true;
  }


  /**
   * Loads the content of the given resource.
   *
   * @param resource the resource to load
   * @return the content as an object since it depends on which content is being read (ex: manifest,
   *         library, packages, class) (never <code>null</code>)
   * @throws NoSuchContentException if the content does not exist
   * @throws StoreException         if there is a problem reading the content.
   */
  public RepositoryModel loadContent(RepositoryResource resource)
    throws NoSuchContentException, StoreException
  {
    if(resource == null)
      throw new NoSuchContentException(resource);

    Collection<LibraryVersionResource> lvc = new ArrayList<LibraryVersionResource>();

    FileObject root = getResourceFile(resource);

    try
    {
      FileObject[] organisations = root.findFiles(_fileSelector);

      for(FileObject organisation : organisations)
      {
        if(organisation.getType() == FileType.FOLDER)
        {
          OrganisationResource organisationResource =
            new OrganisationResource(organisation.getName().getBaseName());

          FileObject[] libraries = organisation.getChildren();
          for(FileObject library : libraries)
          {
            LibraryResource libraryResource =
              new LibraryResource(organisationResource, library.getName().getBaseName());

            FileObject[] libraryVersions = library.getChildren();
            for(FileObject libraryVersion : libraryVersions)
            {
              lvc.add(new LibraryVersionResource(libraryResource,
                                                 libraryVersion.getName().getBaseName()));
            }
          }

        }
      }
    }
    catch(FileSystemException e)
    {
      throw new StoreException(e);
    }

    return new RepositoryModel(lvc);
  }

  /**
   * Saves the model.
   *
   * @param model the mode to store
   * @return the resource
   * @throws StoreException if there is a problem
   */
  public RepositoryResource saveContent(RepositoryModel model) throws StoreException
  {
    throw new UnsupportedOperationException("repo cannot be saved " + model.getClass().getName());
  }

  /**
   * Deletes the content pointed to by this resource.
   *
   * @param resource the resource to delete
   * @return <code>true</code> if the resource was deleted, <code>false</code> if it did not exist in
   *         the first place
   * @throws StoreException if there is a problem
   */
  @Override
  public boolean deleteContent(RepositoryResource resource) throws StoreException
  {
    throw new UnsupportedOperationException("repo cannot be deleted");
  }
}