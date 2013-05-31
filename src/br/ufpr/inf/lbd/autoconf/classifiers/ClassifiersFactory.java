package br.ufpr.inf.lbd.autoconf.classifiers;

public class ClassifiersFactory {
  public static Classifiers getClassifier(String c) {
    if (c.equalsIgnoreCase("KMeans"))
      return new AC_KMeans();
    else if (c.equalsIgnoreCase("EuclideanDistance"))
      return new AC_EuclideanDistance();
    else if (c.equalsIgnoreCase("Equality"))
      return new AC_Equality();
    return null;
  }
}
