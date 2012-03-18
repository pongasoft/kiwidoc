
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

package com.pongasoft.kiwidoc.index.impl.keyword.impl;

import com.pongasoft.kiwidoc.index.impl.lucene.api.UserDataExtractor;
import com.pongasoft.kiwidoc.index.impl.lucene.impl.StringFromCacheUserDataExtractor;
import com.pongasoft.kiwidoc.index.impl.ResourceEncoder;
import com.pongasoft.kiwidoc.index.impl.StringResourceEncoder;
import com.pongasoft.kiwidoc.index.impl.CanonicalResourceEncoder;
import com.pongasoft.kiwidoc.model.resource.Resource;
import com.pongasoft.util.core.annotations.FieldInitializer;
import org.apache.lucene.search.IndexSearcher;

import java.io.IOException;

/**
 * @author yan@pongasoft.com
 */
public class ResourceExtractor implements UserDataExtractor<Resource>
{
  private UserDataExtractor<String> _stringUserDataExtractor = new StringFromCacheUserDataExtractor();
  private ResourceEncoder<String> _resourceEncoder = null;

  public ResourceExtractor()
  {
  }

  /**
   * Constructor
   */
  public ResourceExtractor(UserDataExtractor<String> stringUserDataExtractor,
                           ResourceEncoder<String> resourceEncoder)
  {
    _stringUserDataExtractor = stringUserDataExtractor;
    _resourceEncoder = resourceEncoder;
  }

  public UserDataExtractor<String> getStringUserDataExtractor()
  {
    return _stringUserDataExtractor;
  }

  @FieldInitializer
  public void setStringUserDataExtractor(UserDataExtractor<String> stringUserDataExtractor)
  {
    _stringUserDataExtractor = stringUserDataExtractor;
  }

  public ResourceEncoder<String> getResourceEncoder()
  {
    return _resourceEncoder;
  }

  @FieldInitializer(optional = true)
  public void setResourceEncoder(ResourceEncoder<String> resourceEncoder)
  {
    _resourceEncoder = resourceEncoder;
  }

  /**
   * Extracts the user data from the searcher (could be store as payload or others...)
   */
  public Resource[] extractUserData(IndexSearcher indexSearcher) throws IOException
  {
    String[] array = _stringUserDataExtractor.extractUserData(indexSearcher);
    ResourceEncoder<String> resourceEncoder = _resourceEncoder;
    if(resourceEncoder == null)
    {
      resourceEncoder = new StringResourceEncoder(new CanonicalResourceEncoder()); 
    }
    return resourceEncoder.decodeResources(array);
  }
}
