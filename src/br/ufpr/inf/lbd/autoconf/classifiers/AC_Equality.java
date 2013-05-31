package br.ufpr.inf.lbd.autoconf.classifiers;

import br.ufpr.inf.lbd.autoconf.Wrapper;

import java.util.Iterator;
import java.util.Map;



/**
 * User: Edson
 * Date: 5/27/13
 * Time: 6:43 PM
 */
public class AC_Equality implements Classifiers {

  Map<Map<String, Integer>, Integer> features;

  @Override
  public Wrapper classify(Wrapper wrapper) {

    /* Get Feature Vector */
    Map<String, Integer> key = wrapper.getFeatureVector().getFeatures();

    if (features.containsKey(key))
      features.put(key, features.get(key) + 1);
    else
      features.put(key, 1);

    return wrapper;
  }
}
