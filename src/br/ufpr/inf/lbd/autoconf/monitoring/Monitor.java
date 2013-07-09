<<<<<<< HEAD
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

=======
>>>>>>> d3546c528d36d61db66978913b7a995a6cb881f2
package br.ufpr.inf.lbd.autoconf.monitoring;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Monitor {
  private String hadoopHome;
  private String autoconfHome;
  private String path;
<<<<<<< HEAD
  private String jobName;

  /**
   *
   * @param jobName
   */
=======
  private String jobName = "";

>>>>>>> d3546c528d36d61db66978913b7a995a6cb881f2
  public Monitor(String jobName) {
    getenv();
    setJobName(jobName);
  }

  /**
   * Set the job name, which is as name for the trace files
   * @param jobName
   */
  private void setJobName(String jobName) {
    this.jobName = jobName;
<<<<<<< HEAD
    this.jobName = this.jobName.replace("\n", " ").replace(" ", "_").replace("...","_");
=======

>>>>>>> d3546c528d36d61db66978913b7a995a6cb881f2
  }

  /**
   * Start monitoring
   * @throws IOException
   * @throws InterruptedException
   */
  public void start() throws IOException, InterruptedException {
<<<<<<< HEAD
    // System.out.println("AutoConf: Monitor: Start.");
    // monitor("start");
=======
    monitor("start");
>>>>>>> d3546c528d36d61db66978913b7a995a6cb881f2
  }

  /**
   * Stop monitoring
   * @throws IOException
   * @throws InterruptedException
   */
  public void stop() throws IOException, InterruptedException {
<<<<<<< HEAD
    // System.out.println("AutoConf: Monitor: Stop.");
    // monitor("stop");
    // monitor("save");
=======
    monitor("save");
    monitor("stop");
>>>>>>> d3546c528d36d61db66978913b7a995a6cb881f2
  }

  /**
   * Launch the monitor script
   * @param resource
   * @throws IOException
   * @throws InterruptedException
   */
  private void monitor (String resource) throws IOException, InterruptedException {
    if (resource.matches("start")) {
<<<<<<< HEAD
      jobstat_exec(" -a "); // monitoring all slaves
    } else if (resource.matches("stop")) {
      jobstat_exec(" -k all"); // stop monitoring all slaves
    } else if (resource.matches("save")) {
      jobstat_exec(" -s " + jobName); // Save trace info
      // Thread.sleep(500); // sleep 0.5 seconds
      // jobstat_exec(" -t "); // Copy trace info from slaves
      // jobstat_exec(" -r "); // Remove trace info in slaves
=======
      jobstat(" -a "); // monitoring all slaves
    } else if (resource.matches("stop")) {
      jobstat(" -k all"); // stop monitoring all slaves
    } else if (resource.matches("save")) {
      jobstat(" -s " + jobName); // Save trace info
      jobstat(" -t "); // Copy trace info from slaves
      jobstat(" -r "); // Remove trace info in slaves
>>>>>>> d3546c528d36d61db66978913b7a995a6cb881f2
    }
  }

  /**
<<<<<<< HEAD
   * Launch the monitor script with ProcessBuilder
   * XXX: Not working on my Mac :|
=======
   * Launch the monitor script
>>>>>>> d3546c528d36d61db66978913b7a995a6cb881f2
   * @param operation
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  public int jobstat (String operation) throws IOException, InterruptedException {
    Process process;
<<<<<<< HEAD
    ProcessBuilder script = new ProcessBuilder("./jobstat", operation);
=======
    ProcessBuilder script = new ProcessBuilder("jobstat", operation);
>>>>>>> d3546c528d36d61db66978913b7a995a6cb881f2
    Map<String, String> env = script.environment();
    env.put("HADOOP_HOME", hadoopHome);
    env.put("AUTOCONF_HOME", autoconfHome);
    env.put("PATH", path);
    script.directory(new File(autoconfHome + "/bin"));
<<<<<<< HEAD
    System.out.println("AutoConf: Monitor: HADOOP_HOME="+ hadoopHome);
    System.out.println("AutoConf: Monitor: AUTOCONF_HOME="+ autoconfHome);
    System.out.println("AutoConf: Monitor: PATH="+ path);
    System.out.println("AutoConf: Monitor: Lauching jobstat " + operation);
    System.out.println("AutoConf: Monitor: Command: " + script.command() + " @ " + script.directory());

    process = script.start();
    process.waitFor();
    int e = process.exitValue();
    System.out.println("AutoConf: Monitor: Exit Value: " + e);
    return e;
  }

  /**
   * Launch the monitor script with exec
   * @param operation
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  public int jobstat_exec (String operation) throws IOException, InterruptedException {
    Process p = Runtime.getRuntime().exec(autoconfHome + "/bin/jobstat " + operation);
    System.out.println("AutoConf: Monitor: JobName is " + jobName);
    System.out.println("AutoConf: Monitor: Lauching jobstat " + operation);
    p.waitFor();
    return p.exitValue();
=======
    process = script.start();
    System.out.println("AutoConf: Monitor: Lauching jobstat " + operation);
    return process.waitFor();
>>>>>>> d3546c528d36d61db66978913b7a995a6cb881f2
  }

  /**
   * Get the environment for the jobstat processes
   */
  public void getenv () {
    hadoopHome = System.getenv("HADOOP_HOME");
    if (hadoopHome == null) {
      System.out.println("AutoConf: error: HADOOP_HOME is not set up.");
      System.exit(-1);
    }

    autoconfHome = System.getenv("AUTOCONF_HOME");
    if (autoconfHome == null) {
      System.out.println("AutoConf: error: AUTOCONF_HOME is not set up.");
      System.exit(-1);
    }

    path = System.getenv("PATH");
    if (path == null) {
      System.out.println("AutoConf: error: PATH is not set up.");
      System.exit(-1);
    }
  }
}