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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 *
 */
public class TuningKnobs implements Serializable {
  Properties knobs = new Properties();

  /**
   *
   * @param conf
   * @throws IOException
   */
  public TuningKnobs(Configuration conf) throws IOException {
    setTuningKnobs(conf);
  }

  /**
   * Get the tuning knobs
   * @return
   */
  public Properties getTuningKnobs() {
    return this.knobs;
  }

  /**
   * Copy the Tuning Knobs from the Configuration conf
   * to the local this.knobs
   * @param path
   */
  public void loadKnobsFromFile(String path) {
    try {
      FileInputStream inputStream = new FileInputStream(path);
      knobs.load(inputStream);
      inputStream.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
  }

  /**
   * Copy the Tuning Knobs from the Configuration conf
   * to the local this.knobs
   * @param conf
   */
 private void setTuningKnobs(Configuration conf) {
    Iterator i = conf.iterator();
    while (i.hasNext()) {
      Map.Entry<String, String> p = (Map.Entry<String, String>) i.next();
      if (knobs.getProperty(p.getKey()) != null)
        knobs.setProperty(p.getKey(), p.getValue());
    }
  }


  /**
   * Copy the Tuning knobs from this.knobs
   * to the Configuration conf of the job.
   * @param conf
   */
  public void applyTuning(Configuration conf) {
    System.out.println("AutoConf: Tuning job: ");
    if (knobs.size() > 0) {
      for (Object aConf : conf) {
        Map.Entry<String, String> p = (Map.Entry<String, String>) aConf;
        if (knobs.getProperty(p.getKey()) != null) {
          conf.set(p.getKey(), knobs.getProperty(p.getKey()));
          System.out.println("\t - " + p.getKey() + " from " + p.getValue() + " to " + knobs.getProperty(p.getKey()));
        }
      }
    } else {
      System.out.println("\t - AutoConf: There is no knobs to tune. Nothing to do.");
    }
  }

  /**
   * Print all the Tuning Knobs from this.knobs
   */
  public void show() {
    if (getTuningKnobs().size() > 0) {
      System.out.println(" *** Knobs   :: " + getTuningKnobs().size());
      Enumeration e = knobs.propertyNames();
      while (e.hasMoreElements()) {
        String key = (String) e.nextElement();
        System.out.println("    " + key + " = " + knobs.getProperty(key));
      }
      System.out.println("\n");
    }
  }
}
