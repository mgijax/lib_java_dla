package org.jax.mgi.shr.dla;

import junit.framework.*;

public class TestExceptionHandler
    extends TestCase {
  private DLAExceptionHandler eh = null;

  public TestExceptionHandler(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
    super.setUp();
    eh =
        new DLAExceptionHandler((DataLoadLogger)DataLoadLogger.getInstance());
  }

  protected void tearDown() throws Exception {
    eh = null;
    super.tearDown();
  }

  public void testCounters() {
    assertEquals(eh.getErrorCount(), 0);
    assertEquals(eh.getDataErrorCount(), 0);
    eh.handleException(new DLAException("", false));
    eh.handleException(new DLAException("", false));
    eh.handleException(new DLAException("", true));
    assertEquals(eh.getDataErrorCount(), 1);
    assertEquals(eh.getErrorCount(), 3);
  }


}
