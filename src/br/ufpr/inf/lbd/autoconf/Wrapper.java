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

import java.io.IOException;
import java.io.Serializable;

public class Wrapper implements Serializable {
  private String jobName = "unknown";
  private TuningKnobs tuningKnobs;
  private FeatureVector featureVector;

  /**
   *
   * @param conf
   * @throws IOException
   */
  public Wrapper(Configuration conf) throws IOException {
    setJobName(((JobConf)conf).getJobName());
    setFeatureVector(new FeatureVector(conf));
    setTuningKnobs(new TuningKnobs(conf));
  }

  /**
   *
   * @param name
   */
  public void setJobName(String name) {
    if (name != null) {
      this.jobName = name.replace("\n","").replace(" ","_").replace("...","");
    }
  }

  /**
   *
   * @return
   */
  public String getJobName() {
    return this.jobName;
  }

  /**
   *
   * @return
   */
  public TuningKnobs getTuningKnobs() {
    return tuningKnobs;
  }

  /**
   *
   * @param tuningKnobs
   */
  public void setTuningKnobs(TuningKnobs tuningKnobs) {
    this.tuningKnobs = tuningKnobs;
  }

  /**
   *
   * @return
   */
  public FeatureVector getFeatureVector() {
    return featureVector;
  }

  /**
   *
   * @param featureVector
   */
  public void setFeatureVector(FeatureVector featureVector) {
    this.featureVector = featureVector;
  }
}
