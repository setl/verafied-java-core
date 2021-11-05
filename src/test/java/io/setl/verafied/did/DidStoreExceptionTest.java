package io.setl.verafied.did;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author Simon Greatrix on 04/11/2021.
 */
public class DidStoreExceptionTest {

  @Test
  public void test1() {
    DidStoreException e = new DidStoreException();
    assertNull(e.getMessage());
    assertNull(e.getCause());
  }


  @Test
  public void test2() {
    DidStoreException e = new DidStoreException("message");
    assertEquals("message", e.getMessage());
    assertNull(e.getCause());
  }

  @Test
  public void test3() {
    Exception c = new IllegalArgumentException("cause");
    DidStoreException e = new DidStoreException(c);
    assertEquals(c.toString(), e.getMessage());
    assertEquals(c, e.getCause());
  }

  @Test
  public void test4() {
    Exception c = new IllegalArgumentException("cause");
    DidStoreException e = new DidStoreException("message",c);
    assertEquals("message", e.getMessage());
    assertEquals(c, e.getCause());
  }
}