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

import com.sun.org.apache.xalan.internal.xsltc.cmdline.getopt.GetOpt;
import com.sun.org.apache.xalan.internal.xsltc.cmdline.getopt.GetOptsException;
import org.apache.hadoop.conf.Configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;

/**
 * User: Edson
 * Date: 1/28/13
 * Time: 10:59 AM
 */
public class AutoConfCtl {
  static Properties resources = new Properties();
  static String confFile = null;
  static String jobName = null;

  private static AutoConf getAutoConfRMIServer () throws IOException, NotBoundException {
    Properties rmiconfig = new Properties();
    FileInputStream in = new FileInputStream("/tmp/autoconf.conf");
    rmiconfig.load(in);

    Registry registry = LocateRegistry.getRegistry(rmiconfig.getProperty("RMI_SERVER"), 50123);
    System.out.println("AutoConf: Connecting to AutoConf server at " + rmiconfig.getProperty("RMI_SERVER"));
    return (AutoConf) registry.lookup("//" + rmiconfig.getProperty("RMI_SERVER") + "/AutoConf");
  }

  public static void loadConfigFromFile(String file) {
    try {
      // TODO: This path must be configurable at autoconfctl.conf
      FileInputStream in = new FileInputStream(file);
      resources.load(in);
    } catch (Exception e) {
      System.out.println("AutConfUpdate: Error: ");
      System.out.println(e.getMessage());
    }
  }

  private static void usage() {
    System.out.println("USAGE: autoconfctl -j <jobName> -f <path-to-configuration> <-a|-u|-r|-p>.");
    System.out.println("autoconfctl -j <jobName>\tSet job name.");
    System.out.println("autoconfctl -f <path-to-configuration>\tSet configuration file path.");
    System.out.println("autoconfctl -a\tAdd a new configuration.");
    System.out.println("autoconfctl -u\tUpdate a configuration.");
    System.out.println("autoconfctl -r\tRemove a configuration.");
    System.out.println("autoconfctl -p\tPrint all knobs from a job.");
    System.out.println("autoconfctl -l\tList all job names.");
    System.out.println("autoconfctl -s\tStart AutoConf Server.");
    System.out.println("autoconfctl -s\tThis help.");
  }

  private static void add() throws NotBoundException, RemoteException, IOException {
      AutoConf autoConf = getAutoConfRMIServer();
      Configuration conf = new Configuration();
      loadConfigFromFile(confFile);
      TuningKnobs newconf = new TuningKnobs(jobName, resources);

      if (autoConf.add(newconf) ) {
        System.out.println("AutoConf Add: \"" + jobName + "\" has been added with success.");
      }
  }

  private static void update() throws NotBoundException, RemoteException, IOException {
      AutoConf autoConf = getAutoConfRMIServer();
      Configuration conf = new Configuration();
      loadConfigFromFile(confFile);
      TuningKnobs newconf = new TuningKnobs(jobName, resources);

      if (autoConf.update(newconf) ) {
        System.out.println("AutoConf Update: \"" + jobName + "\" has been updated with success.");
      }
  }

  private static void remove() throws NotBoundException, RemoteException, IOException {
      AutoConf autoConf = getAutoConfRMIServer();
      Configuration conf = new Configuration(false);
      TuningKnobs newconf = new TuningKnobs(jobName, conf);

      if (autoConf.remove(newconf) ) {
        System.out.println("AutoConf Remove: \"" + jobName + "\" has been removed with success.");
      }
  }

  private static void print() throws RemoteException, NotBoundException, IOException {
    if (jobName == null) {
      System.out.println("AutoConf: Please, specify the JobName with option -j.");
      System.exit(-1);
    }

    AutoConf autoConf = getAutoConfRMIServer();
    Configuration conf = new Configuration(false);
    TuningKnobs newconf = new TuningKnobs(jobName, conf);
    autoConf.show(newconf);
  }

  private static void list() throws RemoteException, NotBoundException, IOException {
    AutoConf autoConf = getAutoConfRMIServer();
    autoConf.list();
  }

  private static void setJobName(String name) {
    jobName = name;
  }

  private static void setConfigFile(String file) {
    confFile = file;
  }

  private static void startServer() throws RemoteException {
    AutoConfImpl server = new AutoConfImpl();
  }

  public static void main(String[] args) throws GetOptsException {
    int c;
    String arg;
    GetOpt g = new GetOpt(args, "j:f:aurplsh");

    try {
      while ((c = g.getNextOption()) != -1) {
        switch (c) {
          case 'a': add(); break;
          case 'u': update(); break;
          case 'r': remove(); break;
          case 'p': print(); break;
          case 'l': list(); break;
          case 'j': setJobName(g.getOptionArg()); break;
          case 'f': setConfigFile(g.getOptionArg()); break;
          case 's': startServer(); break;
          case 'h': usage(); break;
          case '?': usage(); break;
          default:  usage(); break;
        }
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
      System.out.println(e.getStackTrace());
    }
  }
}
