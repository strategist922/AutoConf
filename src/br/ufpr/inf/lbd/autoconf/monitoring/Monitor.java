package br.ufpr.inf.lbd.autoconf.monitoring;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Monitor {
  private String hadoopHome;
  private String autoconfHome;
  private String path;
  private String jobName = "";

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

  }

  /**
   * Start monitoring
   * @throws IOException
   * @throws InterruptedException
   */
  public void start() throws IOException, InterruptedException {
    monitor("start");
  }

  /**
   * Stop monitoring
   * @throws IOException
   * @throws InterruptedException
   */
  public void stop() throws IOException, InterruptedException {
    monitor("save");
    monitor("stop");
  }

  /**
   * Launch the monitor script
   * @param resource
   * @throws IOException
   * @throws InterruptedException
   */
  private void monitor (String resource) throws IOException, InterruptedException {
    if (resource.matches("start")) {
      jobstat(" -a "); // monitoring all slaves
    } else if (resource.matches("stop")) {
      jobstat(" -k all"); // stop monitoring all slaves
    } else if (resource.matches("save")) {
      jobstat(" -s " + jobName); // Save trace info
      jobstat(" -t "); // Copy trace info from slaves
      jobstat(" -r "); // Remove trace info in slaves
    }
  }

  /**
   * Launch the monitor script
   * @param operation
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  public int jobstat (String operation) throws IOException, InterruptedException {
    Process process;
    ProcessBuilder script = new ProcessBuilder("jobstat", operation);
    Map<String, String> env = script.environment();
    env.put("HADOOP_HOME", hadoopHome);
    env.put("AUTOCONF_HOME", autoconfHome);
    env.put("PATH", path);
    script.directory(new File(autoconfHome + "/bin"));
    process = script.start();
    System.out.println("AutoConf: Monitor: Lauching jobstat " + operation);
    return process.waitFor();
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