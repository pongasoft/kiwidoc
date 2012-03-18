
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

package com.pongasoft.util.core.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * The purpose of this annotation is to specify that a method (usually a setter)
 * should be used only during the initialization phase. This is mostly used by dependency injection,
 * because the 'setters' way of dealing with dependency injection is much more friendly than the
 * 'constructor' way. A method marked with this annotation should be called only at initialization
 * time and never after. Results are not guaranteed if the contract is broken. Note that it should
 * actually be easy to enforce this behavior with aop!
 *
 * @author yan@pongasoft.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FieldInitializer
{
  /**
   * Specify whether the field is optional or not. By default the field is required, so you don't
   * have to specify this value in most cases.
   */
  boolean optional() default false;
}
