package queueing.sim.testing;

import queueing.sim.QueueView;

public final class TestQueue implements QueueView {

  private final int id;
  private int size;

  public TestQueue(int id) {
    this.id = id;
  }

  @Override
  public int id() {
    return id;
  }

  @Override
  public int size() {
    return size;
  }

  public void size(int size) {
    this.size = size;
  }
}
