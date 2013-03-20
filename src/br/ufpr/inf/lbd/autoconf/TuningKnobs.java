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

import org.apache.hadoop.conf.Configuration;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class TuningKnobs implements Serializable {
  String jobName = "no-name";
  Properties resources = new Properties();


  public TuningKnobs(Configuration conf) {
    // TODO: conf.get("mapred.job.name") is not working :(
    Iterator i = conf.iterator();
    while (i.hasNext()) {
      Map.Entry<String, String> p = (Map.Entry<String, String>) i.next();
      if (p.getKey().matches("mapred.job.name")) {
        setJobName(p.getValue());
      }
    }
    applyKnobs(conf);
  }

  public TuningKnobs(String name, Configuration conf) {
    setJobName(name);
    applyKnobs(conf);
  }

  public TuningKnobs(String name, Properties properties) {
    setJobName(name);
    resources = properties;
  }

  private void applyKnobs(Configuration conf) {
    System.out.println("AutoConf: There are " + conf.size() + " knobs available.");
    Iterator i = conf.iterator();
    while (i.hasNext()) {
      Map.Entry<String, String> p = (Map.Entry<String, String>) i.next();
      if (resources.getProperty(p.getKey()) != null)
         resources.setProperty(p.getKey(), p.getValue());
    }
  }

  public Properties getResources() {
    return this.resources;
  }

  public void setJobName(String name) {
    if (name != null)  {
      this.jobName = name;
    }
  }

  public String getJobName() {
    return this.jobName;
  }

  public void copyResourcesTo(Configuration conf) {
    if (resources.size() > 0) {
      System.out.println("AutoConf: Applying " + resources.size() + " tuned knobs from " + conf.size() + " knobs.");
      Iterator i = conf.iterator();
      while (i.hasNext()) {
        Map.Entry<String, String> p = (Map.Entry<String, String>) i.next();
        if (resources.getProperty(p.getKey()) != null) {
          conf.set(p.getKey(), resources.getProperty(p.getKey()));
//          System.out.println(" *** " + p.getKey() + " :: " + resources.getProperty(p.getKey()));
        }
      }
    }
  }

  public void showConfiguration () {
    System.out.println("AutoConf: " + jobName + " has " + resources.size());
    Enumeration e = resources.propertyNames();
    while (e.hasMoreElements()) {
      String key = (String) e.nextElement();
      System.out.println(" --> " + key + " :: " + resources.getProperty(key));
    }
  }
}
