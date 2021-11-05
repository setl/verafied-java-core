package io.setl.verafied.data.jwk;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Simon Greatrix on 05/11/2021.
 */
public class KeyTypeTest {

  @Test
  public void test() {
    for (KeyType keyType : KeyType.values()) {
      assertEquals(keyType, KeyType.fromId(keyType.id()));
    }

  }

}