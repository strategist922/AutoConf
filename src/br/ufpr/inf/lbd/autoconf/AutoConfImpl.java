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
 * Unless required by applicable law or agreed to in
 * writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.ufpr.inf.lbd.autoconf;

import br.ufpr.inf.lbd.autoconf.clustering.Classifiers;
import br.ufpr.inf.lbd.autoconf.clustering.ClassifiersFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;

/**
 *
 */
public class AutoConfImpl extends Thread implements AutoConf  {
  public static AutoConfServer server;

  /**
   *
   * @throws RemoteException
   */
  public AutoConfImpl() throws RemoteException {
  }

  /**
   *
   * @param args
   * @throws RemoteException
   */
  public static void main(String args[]) throws RemoteException {
    server = new AutoConfImpl.AutoConfServer();
    server.startServer();
  }

  /**
   *
   * @param wrapper
   * @return
   * @throws RemoteException
   */
  @Override
  public Wrapper autoConfigure(Wrapper wrapper) throws RemoteException {
    return server.autoConfigure(wrapper);
  }

  /**
   *
   * @throws RemoteException
   */
  @Override
  public void startServer() throws RemoteException {
    server.startServer();
  }

  /**
   *
   */
  public void run() {
    server.startServer();
  }

  /**
   * AutoConf server Class
   */
  private static class AutoConfServer  {
    public Classifiers classifier;
    private String classifierName;
    private String home;
    private String host;
    private String port;

    /**
     *
     * @throws RemoteException
     */
    public AutoConfServer() throws RemoteException {
      try {
        getenv();
        /* Set the Classifier */
        classifier = ClassifiersFactory.getClassifier(classifierName, home);
      } catch (IOException e) {
        e.getMessage();
        e.printStackTrace();
      }
    }

    /**
     *
     */
    public void run() {
      startServer();
    }

    /**
     * Classify the given wrapper
     * @param wrapper The job info used in the tuning process
     * @return w New wrapper with the optimized tuning knobs
     * @throws RemoteException
     */
    public Wrapper autoConfigure(Wrapper wrapper) throws RemoteException {
      return classifier.classify(wrapper);
    }

    /**
     * Start AutoConf server
     */
    public void startServer() {
      if (System.getSecurityManager() == null)
        System.setSecurityManager(new RMISecurityManager());
      try {
        AutoConfImpl server = new AutoConfImpl();
        AutoConf stub = (AutoConf) UnicastRemoteObject.exportObject(server, 0);
        Registry registry = LocateRegistry.createRegistry(Integer.parseInt(port));
        registry.rebind("//" + host + "/AutoConf", stub);
        System.out.println(" * AutoConf server is running at " +  host + ":" + port);
      } catch (Exception e) {
        System.err.println("AutoConf error: " + e.getMessage());
        e. printStackTrace();
      }
    }

    /**
     * Load AutoConf server environment
     * @throws IOException
     */
    public void getenv() throws IOException {
      home = System.getenv("AUTOCONF_HOME");
      if (home == null) {
        System.out.println("AutoConf: error: AUTOCONF_HOME not set.");
        System.exit(-1);
      }

      Properties config = new Properties();
      FileInputStream in = new FileInputStream(home + "/autoconf.conf");
      config.load(in);

      host = config.getProperty("AUTOCONF_HOST");
      if (host.isEmpty()) {
        System.out.println("AutoConf: AUTOCONF_HOST has not been set up. Exiting...");
        System.exit(-1);
      }

      port = config.getProperty("AUTOCONF_PORT");
      if (port.isEmpty()) {
        System.out.println("AutoConf: AUTOCONF_PORT has not been set up. Exiting...");
        System.exit(-1);;
      }

      classifierName = config.getProperty("AUTOCONF_CLASSIFIER");
      if (classifierName.isEmpty()) {
        System.out.println("AutoConf: AUTOCONF_CLASSIFIER has not been set up. Exiting...");
        System.exit(-1);;
      }
      in.close();
    }
  }
}
