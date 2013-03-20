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

package br.ufpr.inf.lbd.autoconf;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

public class Index {
  static TreeMap<String, TuningKnobs> tree = new TreeMap<String, TuningKnobs>();

  /* TODO: Optimize Job Configuration */
  public TuningKnobs optimize(TuningKnobs k) {
    return k;
  }

  /* TODO: This add should lock the tree to add new knobs
   *       Translates TuningKnobs to Configuration */
  public TuningKnobs getTunedKnobs(TuningKnobs k) {
    TuningKnobs a;
    String name = k.getJobName().split(" ")[0];

    if (!k.getJobName().matches("no-name")) {
      System.out.println(" *** AutoConf: Configuring " + name);
    }

    if (tree.containsKey(name)) {
      a = tree.get(name);
      a.setJobName(k.getJobName());
      return a;
    } else {
      // Uncomment the lines below in case
      // you want jobs writing the TuningKnobs.resources
      //optimize(k);
      //tree.put(name, k);
      return k;
    }
  }

  public boolean add(TuningKnobs k) {
    System.out.println(" *** AutoConf: Adding " + k.getJobName());
    if (tree.containsKey(k.getJobName())) {
      tree.put(k.getJobName(), k);
      return true;
    } else {
      tree.put(k.getJobName(), k);
      return false;
    }
  }

  public boolean update(TuningKnobs k) {
    System.out.println(" *** AutoConf: Updating " + k.getJobName());
    if (tree.containsKey(k.getJobName())) {
      tree.put(k.getJobName(), k);
      return true;
    } else {
      tree.put(k.getJobName(), k);
      return false;
    }
  }

  public boolean remove(TuningKnobs k) {
    if (tree.containsKey(k.getJobName())) {
      System.out.println(" *** AutoConf: Removing " + k.getJobName());
      tree.remove(k.getJobName());
      return true;
    }
    return false;
  }

  public void list() {
    if (tree.isEmpty()) {
      System.out.println("AutoConf: List: There is no tuned jobs.");
    } else {
      Set set = tree.keySet();
      System.out.println("AutoConf: List: The following jobs are indexed.");
      for (Iterator i = set.iterator(); i.hasNext();){
        String jobName = (String) i.next();
        System.out.println(" +++ " + jobName + " has " + tree.get(jobName).getResources().size() + " tuned knobs.");
      }
    }
  }

  public void show(TuningKnobs k) {
    System.out.println(k.getJobName());
    if (tree.containsKey(k.getJobName())) {
      TuningKnobs tuningKnobs = tree.get(k.getJobName());
      tuningKnobs.showConfiguration();
    }
  }
}
