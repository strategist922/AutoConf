/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.ufpr.inf.lbd.autoconf.clustering;

import br.ufpr.inf.lbd.autoconf.clustering.methods.Equality;
import br.ufpr.inf.lbd.autoconf.clustering.methods.EuclideanDistance;

public class ClassifiersFactory {
  /**
   *
   * @param c
   * @return
   */
  public static Classifiers getClassifier(String c) {
    return null;
  }

  /**
   *
   * @param c
   * @param home
   * @return
   */
  public static Classifiers getClassifier(String c, String home) {
    if (c.equalsIgnoreCase("EuclideanDistance")) {
      return new EuclideanDistance(home);
    } else if (c.equalsIgnoreCase("Equality")) {
      return new Equality(home);
    }
    return getClassifier(c);
  }
}
