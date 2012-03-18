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

import com.pongasoft.kiwidoc.index.impl.result.api.NoOpScoreBooster;
import com.pongasoft.kiwidoc.index.impl.result.api.ScoreBooster;
import com.pongasoft.kiwidoc.model.resource.Resource;

import java.util.Collection;
import java.util.List;

/**
 * @author yan@pongasoft.com
 */
public class ChainScoreBooster implements ScoreBooster
{
  private final Collection<ScoreBooster> _scoreBoosters;

  /**
   * Constructor
   */
  public ChainScoreBooster(Collection<ScoreBooster> scoreBoosters)
  {
    _scoreBoosters = scoreBoosters;
  }

  /**
   * Boost the score for the given resource. This implementation iterates over all booster
   * until it finds one which boosts the score.
   *
   * @return the new score
   */
  public float boostScore(Resource resource, float score)
  {
    for(ScoreBooster scoreBooster : _scoreBoosters)
    {
      float boostedScore = scoreBooster.boostScore(resource, score);
      if(boostedScore != score)
        return boostedScore;
    }

    return score;
  }

  /**
   * Creates a chain from the list (optimize)
   */
  public static ScoreBooster create(List<ScoreBooster> boosters)
  {
    if(boosters.isEmpty())
      return NoOpScoreBooster.INSTANCE;

    if(boosters.size() == 1)
    {
      return boosters.get(0);
    }
    
    return new ChainScoreBooster(boosters);
  }
}
