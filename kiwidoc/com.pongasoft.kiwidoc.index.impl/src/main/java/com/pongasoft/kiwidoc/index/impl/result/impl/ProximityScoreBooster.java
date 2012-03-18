
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

package com.pongasoft.kiwidoc.index.impl.result.impl;

import com.pongasoft.kiwidoc.index.impl.result.api.ScoreBooster;
import com.pongasoft.kiwidoc.index.impl.result.api.NoOpScoreBooster;
import com.pongasoft.kiwidoc.model.resource.Resource;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yan@pongasoft.com
 */
public class ProximityScoreBooster implements ScoreBooster
{
  private final Resource _baseResource;

  /**
   * Constructor
   * @param baseResource
   */
  public ProximityScoreBooster(Resource baseResource)
  {
    _baseResource = baseResource;
  }

  /**
   * Boost the score for the given resource.
   *
   * @return the new score
   */
  public float boostScore(Resource resource, float score)
  {
    if(resource == null)
      return score;

    int depth = Math.min(_baseResource.getDepth(), resource.getDepth());

    Resource r1 = adjustDepth(_baseResource, depth);
    Resource r2 = adjustDepth(resource, depth);

    // now they are at the same depth
    while(r1 != null && !r1.equals(r2))
    {
      r1 = r1.getParent();
      r2 = r2.getParent();
      depth--;
    }

    if(depth > 0)
    {
      for(int i = 0; i < depth; i++)
        score += 1.0F;
    }

    return score;
  }

  private Resource adjustDepth(Resource resource, int depth)
  {
    while(resource != null && resource.getDepth() > depth)
    {
      resource = resource.getParent();
    }

    return resource;
  }

  /**
   * Creates the right score booster based on the base resource
   */
  public static ScoreBooster create(Resource baseResource)
  {
    if(baseResource == null || baseResource.isRoot())
      return NoOpScoreBooster.INSTANCE;

    return new ProximityScoreBooster(baseResource);
  }

  /**
   * Creates the right score booster based on the base resource
   */
  public static ScoreBooster create(Resource... baseResource)
  {
    List<ScoreBooster> boosters = new ArrayList<ScoreBooster>();

    for(Resource resource : baseResource)
    {
      ScoreBooster booster = create(resource);
      if(booster != NoOpScoreBooster.INSTANCE)
        boosters.add(booster);
    }

    return ChainScoreBooster.create(boosters);
  }
}
