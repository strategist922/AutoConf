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
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.util.StringUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class TuningKnobs implements Serializable {
  String jobName = "unknown";
  String jarFile = "unknown";
  long inputSize = 0;
  HashMap<String, Long> InputData = new HashMap<String, Long>();
  Properties knobs = new Properties();
  Class<? extends Mapper> mapperClass = null;
  Class<? extends Reducer> reducerClass = null;

  public TuningKnobs(Configuration conf) throws IOException {
    /* Jar file */
    JobConf j = (JobConf) conf;
    setJarFile(j.getJar());

    /* Input Size */
    setInputSize(conf);

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
    if (name != null) {
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

  public HashMap<String, Long> getInputData() {
    return InputData;
  }

  public long getInputSize() {
    return inputSize;
  }

  public void setInputSize(long inputSize) {
    this.inputSize = inputSize;
  }


  void setInputSize(Configuration conf) throws IOException {
    String src = "";
    Path paths[] = FileInputFormat.getInputPaths((JobConf)conf);
    for (Path p : paths) {
      String d[] = p.toString().split("//");
      if (d.length<1) return;

      String dirs[] = d[1].split("/");
      if (dirs.length<1) return;

      for (int i=1; i<dirs.length; i++) {
        src = src + "/" + dirs[i];
      }
    }

    if (src.isEmpty()) {
       System.out.println(" *** AutoConf: Input path not found. ");
       return;
    }

    Path srcPath = new Path(src);
    FileSystem srcFs = srcPath.getFileSystem(conf);
    Path[] pathItems = FileUtil.stat2Paths(srcFs.globStatus(srcPath), srcPath);
    FileStatus items[] = srcFs.listStatus(pathItems);

    if ((items == null) || ((items.length == 0) && (!srcFs.exists(srcPath)))) {
      throw new FileNotFoundException("Cannot access " + src + ": No such file or directory.");
    } else {
      long length[] = new long[items.length];
      for (int i = 0; i < items.length; i++) {
        length[i] = items[i].isDir() ? srcFs.getContentSummary(items[i].getPath()).getLength() : items[i].getLen();
        int len = String.valueOf(length[i]).length();
      }

      for (int i = 0; i < items.length; i++) {
        InputData.put(items[i].getPath().toString(), length[i]);
        inputSize += length[i];
      }
    }
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

  public void showConfiguration() {
    System.out.println("\n");
    System.out.println(" *** JobName :: " + getJobName());
    System.out.println(" *** JarFile :: " + getJarFile());

    HashMap<String, Long> m = getInputData();
    Iterator i = m.entrySet().iterator();
    while (i.hasNext()) {
      Map.Entry inputData = (Map.Entry) i.next();
      System.out.println(" *** Input data  :: " + inputData.getValue() + " " + inputData.getKey());
    }

    System.out.println(" *** Total input size :: " + getInputSize());

    if (getMapperName() != null)
      System.out.println(" *** Mapper  :: " + getMapperName().getName());
    if (getReducerName() != null)
      System.out.println(" *** Reducer :: " + getReducerName().getName());

    /* show the tuning knobs */
    if (getKnobs().size() > 0) {
      System.out.println(" *** Knobs   :: " + getKnobs().size());
      Enumeration e = knobs.propertyNames();
      while (e.hasMoreElements()) {
        String key = (String) e.nextElement();
        System.out.println("    " + key + " = " + knobs.getProperty(key));
      }
      System.out.println("\n");
    }
  }
}
