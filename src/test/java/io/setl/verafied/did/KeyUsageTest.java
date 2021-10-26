package io.setl.verafied.did;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Simon Greatrix on 16/07/2020.
 */
public class KeyUsageTest {

  @Test
  public void forId() {
    for (KeyUsage usage : KeyUsage.values()) {
      assertEquals(usage, KeyUsage.forId(usage.getId()));
    }
  }


  @Test(expected = IllegalArgumentException.class)
  public void notForId() {
    KeyUsage.forId("WIBBLE!!!!");
  }

}