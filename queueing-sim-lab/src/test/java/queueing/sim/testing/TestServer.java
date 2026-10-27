package queueing.sim.testing;

import queueing.sim.ServerView;

public final class TestServer implements ServerView {

  private final int id;
  private boolean busy;

  public TestServer(int id) {
    this.id = id;
  }

  @Override
  public int id() {
    return id;
  }

  @Override
  public boolean isBusy() {
    return busy;
  }

  void busy(boolean busy) {
    this.busy = busy;
  }
}
