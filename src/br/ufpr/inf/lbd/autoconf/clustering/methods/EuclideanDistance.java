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

package br.ufpr.inf.lbd.autoconf.clustering.methods;

import br.ufpr.inf.lbd.autoconf.Wrapper;
import br.ufpr.inf.lbd.autoconf.clustering.Classifiers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * User: Edson
 * Date: 5/27/13
 * Time: 4:39 PM
 */
public class EuclideanDistance implements Classifiers {
  /* List of operators from all jobs
   * <GroupID, <OperatorName, OperatorCounter> > */
  HashMap<Integer, Map<String, Integer>> operators = new HashMap<Integer, Map<String, Integer>>();
  HashMap<Integer, String> tuningFiles = new HashMap<Integer, String>();
  HashMap<Integer, Integer> counter = new HashMap<Integer, Integer>();

  /**
   *
   * @param home
   */
  public EuclideanDistance(String home) {
    System.out.println("\t- Using Euclidean Distance to compare groups.");
    loadTuningGroups(home + "/conf/operators/");
  }

  /**
   * 
   * @param wrapper
   * @return wrapper Tuned Wrapper
   */
  @Override
  public Wrapper classify(Wrapper wrapper) {
    double distance = 0;
    double minor = Double.MAX_VALUE;
    int minorId = 0;
    
    Map<String, Integer> features = wrapper.getFeatureVector().getFeatures();
    Map<String, Integer> nearest = new HashMap<String, Integer>();
    
    if (operators.isEmpty()) {
      System.out.println("AutoConf: Warn: There is no tuningFiles group. Impossible to auto-configure job...");
      return wrapper;
    } else {
      for (Map.Entry<Integer, Map<String, Integer>> entry : operators.entrySet()) {
        distance = euclideanDistance(features, entry.getValue());
        if (distance < minor) {          
          minor = distance;
          minorId = entry.getKey();
          nearest.clear();
          nearest.putAll(entry.getValue());
        }
      }
    }

    System.out.println(" * Job has been classified as " + minorId);
    updateGroupCounter(minorId);
    copyTo(minorId, wrapper);
    return wrapper;
  }

  /**
   * Calculates the Euclidean distance between two feature vectors
   * srqt((p-q)^2 + (p1-q1)^2 + ... + (pn-qn)^2)
   *
   * @param f1
   * @param f2
   * @return distance
   */
  private double euclideanDistance (Map<String, Integer> f1, Map<String, Integer> f2) {
    double distance = 0;
    int p=0, q=0;
    Iterator i = f1.entrySet().iterator();
    System.out.println(" Features: " );
    while (i.hasNext()) {
      Map.Entry<String, Integer> item = (Map.Entry<String, Integer>) i.next();
      q=f2.get(item.getKey());
      p=item.getValue();
      distance += Math.pow((p - q), 2);

      if (distance != 0) {
        System.out.println(item.getKey() + " = " + item.getValue() + " ");
      }
    }
    System.out.println(" Distance from zero is " + distance + ".");
    return Math.sqrt(distance);
  }

  /**
   *
   * @param groupId
   * @param w
   * @throws IOException
   */
  private void copyTo(int groupId, Wrapper w) {
    w.getTuningKnobs().loadKnobsFromFile(tuningFiles.get(groupId));
  }

  /**
   * Load tuningFiles groups from all .xml file in tuningDir
   * A file must contain a single tuningFiles group.
   * @param tuningDir
   */
  public void loadTuningGroups(String tuningDir) {
    int groupId = 1;
    String fileName;
    String tuningFile= "";
    File folder = new File(tuningDir);
    File[] files = folder.listFiles();
    
    Map<String, Integer> m = new HashMap<String, Integer>();
    Properties properties = new Properties();
    try {
      /* For all .xml files in tuningDir */
      System.out.print("\t- Loading groups ");
      for (int i = 0; i < files.length; i++) {
        if (files[i].isFile()) {
          fileName = files[i].getName();
          if (fileName.endsWith(".xml") || fileName.endsWith(".XML")) {
            FileInputStream inputStream = new FileInputStream(files[i].getAbsoluteFile());
            properties.loadFromXML(inputStream);

            /* Save the tuningFiles operators in this.operators */
            for (String key : properties.stringPropertyNames()) {
              if (key.equalsIgnoreCase("groupid")) {
                groupId = Integer.parseInt(properties.getProperty(key));
              } else if (key.equalsIgnoreCase("tuning")) {
                /* Load tuningFiles configuration for groupId */
                tuningFile = properties.getProperty(key);
              } else {
                // System.out.print(key + ":" + properties.getProperty(key)+ " ");
                String value = properties.getProperty(key);
                m.put(key, Integer.valueOf(value));
              }
            }
            System.out.print(groupId + " ");
            tuningFiles.put(groupId, tuningFile);
            operators.put(groupId, m);
            m.clear();
            properties.clear();
            inputStream.close();
            updateGroupCounter(groupId);
          }
        }
      }
      System.out.println();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (InvalidPropertiesFormatException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   *
   * @param groupId
   */
  private void updateGroupCounter(int groupId) {
    if (counter.containsKey(groupId))
      counter.put(groupId, counter.get(groupId) + 1);
    else
      counter.put(groupId, 1);
  }
}
