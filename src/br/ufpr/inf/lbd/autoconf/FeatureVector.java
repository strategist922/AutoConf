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

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.exec.Operator;
import org.apache.hadoop.hive.ql.plan.MapredWork;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.Reducer;

import java.beans.XMLDecoder;
import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FeatureVector implements Serializable, Comparable<FeatureVector> {

  private long inputSize;
  /* This should move to autoconf.conf */
  private String autoConfJobDir = "/tmp/autoconf/jobs/";
  private String jobName;
  private String jar;
  private String query;
  private String plan;
  private Class<? extends Mapper> mapperClass = null;
  private Class<? extends Reducer> reducerClass = null;
  private HashMap<String, Long> inputData = new HashMap<String, Long>();
  private Map<String, Integer> featureVector = new HashMap<String, Integer>();

  /**
   *
   * @param conf
   * @throws IOException
   */
  public FeatureVector (Configuration conf) throws IOException {
    initializeFeatureVector();
    setTuningKnobs(conf);
  }
  public FeatureVector () {

	  }
  
  
  public void setFeatureVector(Map<String, Integer> featureVector){
	  this.featureVector=featureVector;
  }

  /**
   *
   */
  private void initializeFeatureVector() {
	  
	 	
	for(Operators op : Operators.values()){
		featureVector.put(op.toString(),0);
	}
   /*
    * TODO: delete the puts below
    * Switched to the Enumeration "Operators"
    * 
    featureVector.put("EX", 0); // ExtractOperator
    featureVector.put("FIL", 0); // FilterOperator
    featureVector.put("FOR", 0); // ForwardOperator
    featureVector.put("FS", 0);  // FileSinkOperator
    featureVector.put("GBY", 0); // GroupByOperator
    featureVector.put("HASHTABLEDUMMY", 0); // HashTableDummyOperator
    featureVector.put("HASHTABLESINK", 0); // HashTableSinkOperator
    featureVector.put("JOIN", 0); // CommonJoinOperator
    featureVector.put("LIM", 0); // LimitOperator
    featureVector.put("LVF", 0); // LateralViewForwardOperator
    featureVector.put("LVJ", 0); // LateralViewJoinOperator
    featureVector.put("MAP", 0); // MapOperator
    featureVector.put("MAPJOIN", 0); // MapJoinOperator
    // featureVector.put("MAPJOIN", 0); // SMBMapJoinOperator
    featureVector.put("OP", 0); // Operator
    featureVector.put("PTF", 0); // PTFOperator
    featureVector.put("RS", 0); // ReduceSinkOperator
    featureVector.put("SCR", 0); // ScriptOperator
    featureVector.put("SEL", 0); // SelectOperator
    featureVector.put("TS", 0); // TableScanOperator
    featureVector.put("UDTF", 0); // UDTFOperator
    featureVector.put("UNION", 0); // UnionOperator*/
  }

  /**
   *
   * @param conf
   * @throws IOException
   */
  private void setTuningKnobs(Configuration conf) throws IOException {
    JobConf jobConf = (JobConf) conf;

    setJobName(((JobConf) conf).getJobName());

    /* If there is a query, save query and plan to autoConfJobDir */
    setQuery(jobConf);
    if (!getQuery().matches("")) {
      setJar(jobConf);
      setPlan(jobConf);

      saveJar(jobConf);
      savePlan(jobConf);
      saveQuery(jobConf);
      saveConfiguration(jobConf);
      setFeatures();
    }
  }

  /**
   *
   * @return
   */
  public String getJobName() {
    return jobName;
  }

  /**
   *
   * @param jobName
   */
  public void setJobName(String jobName) {
    this.jobName = (getAutoConfJobDir() + jobName).replace("\n", " ").replace(" ", "_").replace("...","_");
  }

  /**
   *
   * @param conf
   * @throws IOException
   */
  public void saveConfiguration(JobConf conf) throws IOException {
    saveConfiguration(conf, ".old");
  }

  /**
   *
   * @param conf
   * @param ext
   * @throws IOException
   */
  public void saveConfiguration(JobConf conf, String ext) throws IOException {
    OutputStream out = new FileOutputStream(getJobName() + ext);
    conf.writeXml(out);
    System.out.println("AutoConf: Configuration has been saved to " + getJobName() + ext + ". ");
  }

  /**
   *
   * @param conf
   * @throws IOException
   */
  private void setQuery(JobConf conf) throws IOException {
    String hiveQL = "";
    if (conf.get("hive.query.string") != null) {
      hiveQL = conf.get("hive.query.string");
    }
    this.query = hiveQL;
  }

  /**
   *
   * @return
   */
  public String getQuery() {
    return query;
  }

  /**
   *
   * @param conf
   * @throws IOException
   */
  private void saveQuery(JobConf conf) throws IOException {
    String path = getJobName() + ".hive";
    File file = new File(path);
    FileUtils.writeStringToFile(file, query);
  }

  /**
   *
   * @param conf
   * @throws IOException
   */
  private void setJar(JobConf conf) throws IOException {
    String jobJar = getJobName() + ".jar";
    Path local = new Path(jobJar);
    this.jar = local.toString();
  }

  /**
   *
   * @return
   */
  public String getJar() {
    return this.jar;
  }

  /**
   *
   * @param conf
   * @throws IOException
   */
  private void saveJar(JobConf conf) throws IOException {
    Path local = new Path(getJar());
    String jarHDFSPath = conf.get("mapred.jar");
    if (jarHDFSPath.isEmpty()) {
      System.out.println("AutoConf: Error: MapRedJar not found. Exiting...");
      throw new IOException();
    }

    File dir = new File(autoConfJobDir);
    if (!dir.exists()) {
      if (!dir.mkdirs())
        throw new IOException();
    }

    FileSystem hdfsFileSystem = FileSystem.get(conf);
    Path hdfs = new Path(jarHDFSPath);
    String fileName = hdfs.getName();

    if (hdfsFileSystem.exists(hdfs)) {
      hdfsFileSystem.copyToLocalFile(false, hdfs, local);
      System.out.println("AutoConf: Jar file " + fileName + " copied to " + local);
    } else {
      System.out.println("AutoConf: Jar file " + fileName + " does not exist on " + local);
      throw new IOException();
    }
  }

  /**
   *
   * @param conf
   * @throws IOException
   */
  private void setPlan(JobConf conf) throws IOException {
    this.plan = getJobName() + ".plan";
  }

  /**
   *
   * @return
   */
  public String getPlan() {
    return plan;
  }

  /**
   *
   * @param conf
   * @throws IOException
   */
  private void savePlan(JobConf conf) throws IOException {
    String planPath = conf.get("hive.exec.plan");
    if (planPath.isEmpty()) {
      System.out.println("AutoConf: Error: Query Plan not found. Exiting...");
      throw new IOException();
    }

    File dir = new File(getAutoConfJobDir());
    if (!dir.exists()) {
      if (!dir.mkdirs())
        throw new IOException();
    }

    Path local = new Path(getPlan());
    Path hdfs = new Path(planPath);
    FileSystem hdfsFileSystem = FileSystem.get(conf);

    if (hdfsFileSystem.exists(hdfs)) {
      hdfsFileSystem.copyToLocalFile(false, hdfs, local);
      System.out.println("AutoConf: Query Plan file " + hdfs.getName() + " copied to " + local);
    } else {
      System.out.println("AutoConf: Query Plan file " + hdfs.getName() + " does not exist on " + local);
      throw new IOException();
    }
  }

  /**
   *
   * @param mapperClass
   */
  private void setMapperName(Class<? extends Mapper> mapperClass) {
    this.mapperClass = mapperClass;
  }

  /**
   *
   * @return
   */
  public Class<? extends Mapper> getMapperName() {
    return this.mapperClass;
  }

  /**
   *
   * @param reducerClass
   */
  private void setReducerName(Class<? extends Reducer> reducerClass) {
    this.reducerClass = reducerClass;
  }

  /**
   *
   * @return
   */
  public Class<? extends Reducer> getReducerName() {
    return this.reducerClass;
  }

  /**
   *
   * @return
   */
  public String getAutoConfJobDir() {
    return autoConfJobDir;
  }

  /**
   *
   * @param autoConfJobDir
   */
  private void setAutoConfJobDir(String autoConfJobDir) {
    this.autoConfJobDir = autoConfJobDir;
  }

  /**
   *
   * @return
   */
  public HashMap<String, Long> getInputData() {
    return inputData;
  }

  /**
   * Get the occurrencies of each operator from a given query.
   * Each sub-phase has a distinct group of featureVector.
   *
   * Must be executed after setPlan().
   *
   * @throws FileNotFoundException
   */
  private void setFeatures() throws FileNotFoundException {
    /* deserialize plan */
    FileInputStream os = new FileInputStream(getPlan());
    XMLDecoder decoder = new XMLDecoder(os);
    MapredWork plan;
    plan = (MapredWork) decoder.readObject();
    decoder.close();

    List<Operator<?>> opr = plan.getAllOperators();
    for (Operator o : opr) {
      String key = o.getName();
      if (featureVector.containsKey(key))
        featureVector.put(key, featureVector.get(key) + 1);
      else
        featureVector.put(key, 1);
    }
  }

  /**
   *
   * @return
   */
  public Map<String, Integer> getFeatures() {
    return featureVector;
  }

  /**
   *
   * @return
   */
  public String getFeaturesAsString() {
    String f = new String("");
    Iterator i = featureVector.entrySet().iterator();
    while (i.hasNext()) {
      Map.Entry<String, Integer> m = (Map.Entry<String, Integer>) i.next();
      f = f + "(" + m.getKey() + "," + m.getValue().toString() + ")";
    }
    return f;
  }

  /**
   *
   * @param conf
   * @throws IOException
   */
  private void setInputSize(Configuration conf) throws IOException {
    String src = "";
    Path paths[] = FileInputFormat.getInputPaths((JobConf) conf);
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
      return;
    }

    Path srcPath = new Path(src);
    FileSystem srcFs = srcPath.getFileSystem(conf);
    Path[] pathItems = FileUtil.stat2Paths(srcFs.globStatus(srcPath), srcPath);
    FileStatus items[] = srcFs.listStatus(pathItems);

    if ((items == null) || ((items.length == 0) && (!srcFs.exists(srcPath)))) {
      System.out.println("AutoConf: Warning: Could not determine the size of input " + src);
      // throw new FileNotFoundException();
    } else {
      long length[] = new long[items.length];
      for (int i = 0; i < items.length; i++) {
        length[i] = items[i].isDir() ? srcFs.getContentSummary(items[i].getPath()).getLength() : items[i].getLen();
        int len = String.valueOf(length[i]).length();
      }

      for (int i = 0; i < items.length; i++) {
        inputData.put(items[i].getPath().toString(), length[i]);
        inputSize += length[i];
      }
    }
  }

  /**
   *
   * @return
   */
  public long getInputSize() {
    return inputSize;
  }

@Override
public int compareTo(FeatureVector fv) {
	
	for (String key : fv.featureVector.keySet()) {
	      int srcValue = 0, destValue = 0;
	      if (fv.featureVector.get(key) != null)
	        srcValue = fv.featureVector.get(key);
	      if (this.featureVector.get(key) != null)
	        destValue = this.featureVector.get(key);
	      if (srcValue != destValue) {
	        return 1;
	      }
	    }
	    return 0;
}
}
