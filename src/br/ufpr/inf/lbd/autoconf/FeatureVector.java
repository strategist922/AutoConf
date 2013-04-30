package br.ufpr.inf.lbd.autoconf;

/**
 * User: Edson
 * Date: 4/30/13
 * Time: 4:37 PM
 */
public class FeatureVector {

  int cpu;
  int mem;
  int net;
  int disk;

  public void FeatureVector () {
    setCpu(0);
    setDisk(0);
    setMem(0);
    setNet(0);
  }

  public int getCpu() {
    return cpu;
  }

  public void setCpu(int cpu) {
    this.cpu = cpu;
  }

  public int getMem() {
    return mem;
  }

  public void setMem(int mem) {
    this.mem = mem;
  }

  public int getNet() {
    return net;
  }

  public void setNet(int net) {
    this.net = net;
  }

  public int getDisk() {
    return disk;
  }

  public void setDisk(int disk) {
    this.disk = disk;
  }
}
