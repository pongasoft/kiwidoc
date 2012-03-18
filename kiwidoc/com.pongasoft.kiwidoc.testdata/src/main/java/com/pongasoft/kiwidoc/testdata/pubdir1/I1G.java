
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

package com.pongasoft.kiwidoc.testdata.pubdir1;

import java.util.List;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * javadoc for I1G before dot. javadoc for I1G {@link I1#getF1()} after dot {@link I2}. {@param <F> hello}
 * {@inheritDoc}
 *
 * @param <E> javadoc param E
 * @param <F>
 * @param <G> {@link com.pongasoft.kiwidoc.testdata.pubdir1 hello!}
 * @param <H> A {@link com.pongasoft.kiwidoc.testdata.pubdir1.I1G} B
 * @param <I> javadoc param I
 * @param <J> javadoc param J
 * @param <K> javadoc param K
 * @param <L> javadoc param L
 * @param <M> javadoc param M
 * @param invalidName javadoc invalid name
 * @param
 * @author YP
 * @author YP2
 * @author
 * @see A1
 * @see C2 this is some extra text {@link C2}
 * @see
 * @see http://www.pongasoft.com
 */
@A1(value=10,
    myString = "String",
    myClass = C1.class,
    retentionPolicy = RetentionPolicy.RUNTIME,
    retention = @Retention(RetentionPolicy.SOURCE),
    targets = {@Target({ElementType.METHOD, ElementType.CONSTRUCTOR}), @Target(ElementType.ANNOTATION_TYPE)}
)
@Pub1Class4.NestedAnnotation(100)
public interface I1G<E,
                     F extends Number,
                     G extends Number & Comparable<Number>,
                     H extends E,
                     I extends List<E>,
                     J extends List<Number>,
                     K extends List<? extends Number>,
                     L extends List<? super Number>,
                     M extends List<? extends List<Number>>,
                     N extends C1.Inner1 & I1.Inner1 & List<? extends C1.Inner1>>
{
}