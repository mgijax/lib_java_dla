package org.jax.mgi.shr.dla;

import junit.framework.*;

public class TestExceptionHandler
    extends TestCase {

  public TestExceptionHandler(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
    super.setUp();
  }

  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testCounters() {
    assertEquals(DLAExceptionHandler.getErrorCount(), 0);
    assertEquals(DLAExceptionHandler.getDataErrorCount(), 0);
    DLAExceptionHandler.handleException(new DLAException("", false));
    DLAExceptionHandler.handleException(new DLAException("", false));
    DLAExceptionHandler.handleException(new DLAException("", true));
    assertEquals(DLAExceptionHandler.getDataErrorCount(), 1);
    assertEquals(DLAExceptionHandler.getErrorCount(), 3);
  }


}
