package br.ufpr.inf.lbd.autoconf.classifiers;

import br.ufpr.inf.lbd.autoconf.Wrapper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * User: Edson
 * Date: 5/27/13
 * Time: 4:39 PM
 */
public class AC_EuclideanDistance implements Classifiers {
  Map<Map<String, Integer>, Integer> features;

  @Override
  public Wrapper classify(Wrapper wrapper) {

    /* Get Feature Vector */
    Map<String, Integer> key = wrapper.getFeatureVector().getFeatures();

    if (features.containsKey(key))
      features.put(key, features.get(key) + 1);
    else
      features.put(key, 1);

    show();
    return wrapper;
  }

  public void show() {
    Iterator i = features.entrySet().iterator();
    while (i.hasNext()) {
      Map.Entry<String, Integer> f = (Map.Entry<String, Integer>) i.next();
      System.out.println(" " + f.getKey() + " " + f.getValue());
    }
  }
}
