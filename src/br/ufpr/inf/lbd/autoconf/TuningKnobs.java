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
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.Reducer;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class TuningKnobs implements Serializable {
  String jobName = "unknown";
  String jarFile = "unknown";
  Properties knobs = new Properties();
  Class<? extends Mapper> mapperClass = null;
  Class<? extends Reducer> reducerClass = null;

  public TuningKnobs(Configuration conf) {
    /* Jar file */
    JobConf j = (JobConf) conf;
    System.out.println("-->>> "+ j.getJar());
    setJarFile(j.getJar());
    
    /* Mapper Class */
    setMapperName(j.getMapperClass());
    
    /* Reducer Class */
    setReducerName(j.getReducerClass());

    /* Job Name */
    setJobName(j.getJobName());

    /* Copy the tuning knobs from conf to this.knobs */
    saveTuningKnobs(conf);
  }

  public TuningKnobs(String name, Configuration conf) {
    setJobName(name);
    saveTuningKnobs(conf);
  }

  public TuningKnobs(String name, Properties properties) {
    setJobName(name);
    knobs = properties;
  }

  public Properties getKnobs() {
    return this.knobs;
  }

  public void setJobName(String name) {
    if (name != null)  {
      this.jobName = name;
    }
  }

  private void setMapperName(Class<? extends Mapper> mapperClass) {
    this.mapperClass = mapperClass;
  }

  public Class<? extends Mapper> getMapperName() {
    return this.mapperClass;
  }

  private void setReducerName(Class<? extends Reducer> reducerClass) {
    this.reducerClass = reducerClass;
  }

  public Class<? extends Reducer> getReducerName() {
    return this.reducerClass;
  }

  public String getJobName() {
    return this.jobName;
  }

  public void setJarFile(String j) {
    this.jarFile = j;
  }

  public String getJarFile() {
    return this.jarFile;
  }

  private void saveTuningKnobs(Configuration conf) {
    System.out.println("AutoConf: There are " + conf.size() + " knobs available.");
    Iterator i = conf.iterator();
    while (i.hasNext()) {
      Map.Entry<String, String> p = (Map.Entry<String, String>) i.next();
      if (knobs.getProperty(p.getKey()) != null)
        knobs.setProperty(p.getKey(), p.getValue());
    }
  }

  public void copyResourcesTo(Configuration conf) {
    if (knobs.size() > 0) {
      System.out.println("AutoConf: Applying " + knobs.size() + " tuned knobs from " + conf.size() + " knobs.");
      Iterator i = conf.iterator();
      while (i.hasNext()) {
        Map.Entry<String, String> p = (Map.Entry<String, String>) i.next();
        if (knobs.getProperty(p.getKey()) != null) {
          conf.set(p.getKey(), knobs.getProperty(p.getKey()));
//          System.out.println(" *** " + p.getKey() + " :: " + knobs.getProperty(p.getKey()));
        }
      }
    }
  }

  public void showConfiguration () {
    System.out.println(" *** JobName :: " + getJobName());
    System.out.println(" *** Knobs   :: " + getKnobs().size());
    System.out.println(" *** JarFile :: " + getJarFile() );

    if (getMapperName() != null)
      System.out.println(" *** Mapper  :: " + getMapperName().getName());
    if (getReducerName() != null)
      System.out.println(" *** Reducer :: " + getReducerName().getName());

    /* show the tuning knobs */
    System.out.println(" *** Tuning Knobs");
    Enumeration e = knobs.propertyNames();
    while (e.hasMoreElements()) {
      String key = (String) e.nextElement();
      System.out.println("    " + key + " = " + knobs.getProperty(key));
    }

  }
}
