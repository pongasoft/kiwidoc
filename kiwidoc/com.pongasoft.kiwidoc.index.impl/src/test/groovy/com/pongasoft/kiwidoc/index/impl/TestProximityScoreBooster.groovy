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

package com.pongasoft.kiwidoc.index.impl

import com.pongasoft.kiwidoc.index.impl.result.impl.ProximityScoreBooster
import com.pongasoft.kiwidoc.model.resource.RepositoryResource
import com.pongasoft.kiwidoc.model.resource.OrganisationResource
import com.pongasoft.kiwidoc.model.resource.LibraryResource
import com.pongasoft.kiwidoc.model.resource.LibraryVersionResource
import com.pongasoft.kiwidoc.model.resource.PackageResource
import com.pongasoft.kiwidoc.model.resource.ClassResource
import com.pongasoft.kiwidoc.model.resource.ManifestResource

public class TestProximityScoreBooster extends GroovyTestCase
{
  public void testProximityScoreBooster()
  {
    def resources = [RepositoryResource.INSTANCE,
                     new OrganisationResource("org"),
                     new LibraryResource("org", "name"),
                     new LibraryVersionResource("org", "name", "version"),
                     new ManifestResource(new LibraryVersionResource("org", "name", "version")),
                     new PackageResource(new LibraryVersionResource("org", "name", "version"), "p"),
                     new ClassResource(new LibraryVersionResource("org", "name", "version"), "p.c")]

    // with the repo... identity
    check(new ProximityScoreBooster(RepositoryResource.INSTANCE),
          resources, [0.5F, 0.5F, 0.5F, 0.5F, 0.5F, 0.5F, 0.5F])

    // with a class resource (same class)
    check(new ProximityScoreBooster(new ClassResource(new LibraryVersionResource("org", "name", "version"), "p.c")),
          resources, [0.5F, 1.5F, 2.5F, 3.5F, 3.5F, 4.5F, 5.5F])

    // with a class resource (name differs)
    check(new ProximityScoreBooster(new ClassResource(new LibraryVersionResource("org", "name2", "version"), "p.c")),
          resources, [0.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F])
  }

  public void testMultipleProximityScoreBoosters()
  {
    def class1Resource = new ClassResource(new LibraryVersionResource("org", "name", "version"), "p.c")
    def class2Resource = new ClassResource(new LibraryVersionResource("org", "name", "version"), "p.c2")

    def booster = new ProximityScoreBooster(new ClassResource(new LibraryVersionResource("org", "name", "version"), "p.c"))

    assertEquals(5.5F, booster.boostScore(class1Resource, 0.5F))
    assertEquals(4.5F, booster.boostScore(class2Resource, 0.5F))

    booster = ProximityScoreBooster.create(new ClassResource(new LibraryVersionResource("org2", "name", "version"), "p.c"))

    // no boost here
    assertEquals(0.5F, booster.boostScore(class1Resource, 0.5F))
    assertEquals(0.5F, booster.boostScore(class2Resource, 0.5F))

    booster = ProximityScoreBooster.create(new ClassResource(new LibraryVersionResource("org2", "name", "version"), "p.c"),
                                           new ClassResource(new LibraryVersionResource("org", "name", "version"), "p.c"))

    assertEquals(5.5F, booster.boostScore(class1Resource, 0.5F))
    assertEquals(4.5F, booster.boostScore(class2Resource, 0.5F))
  }

  private def check(psb, resources, expected)
  {
    resources.eachWithIndex { resource, i ->
      assertEquals(expected[i], psb.boostScore(resource, 0.5F))
    }
  }
}
