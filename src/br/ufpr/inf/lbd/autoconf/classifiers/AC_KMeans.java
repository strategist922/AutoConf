package br.ufpr.inf.lbd.autoconf.classifiers;

import br.ufpr.inf.lbd.autoconf.Wrapper;
import weka.clusterers.SimpleKMeans;

import java.util.HashMap;
import java.util.Map;

public class AC_KMeans implements Classifiers {

  @Override
  public Wrapper classify(Wrapper wrapper) {
    long startTime = System.currentTimeMillis();
    try {
      runKMeans(wrapper);
    } catch (Exception e) {
      System.out.println("AutoConf: Error during KMeans. Exiting... ");
      e.getMessage();
      e.printStackTrace();
    }
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    System.out.println("AutoConf: KMeans spent " + (int) ((elapsedTime / 1000) % 60) + " seconds to cluster.");
    return wrapper;
  }

  private void runKMeans(Wrapper wrapper) throws Exception {
    SimpleKMeans kmeans = new SimpleKMeans();

    Map<String, Integer> features = new HashMap<String, Integer>();
    features = wrapper.getFeatureVector().getFeatures();

    kmeans.setSeed(10);

    // This is the important parameter to set
    kmeans.setPreserveInstancesOrder(true);
    kmeans.setNumClusters(10);
//    kmeans.buildClusterer(instances);

    // This array returns the cluster number (starting with 0) for each instance
    // The array has as many elements as the number of instances
    int[] assignments = kmeans.getAssignments();
    int i=0;
    for(int clusterNum : assignments) {
      System.out.printf("Instance %d -> Cluster %d", i, clusterNum);
      i++;
    }
  }
}
