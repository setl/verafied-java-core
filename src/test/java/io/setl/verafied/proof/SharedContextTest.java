package io.setl.verafied.proof;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.net.URI;

import org.junit.Test;

import io.setl.verafied.did.DidId;

/**
 * @author Simon Greatrix on 05/11/2021.
 */
public class SharedContextTest {

  SharedContext context = new ProofContext(new CanonicalJsonWithJws());


  @Test
  public void testBytesToSign() {
    byte[] bytes = new byte[]{1, 2, 3, 4};
    context.setBytesToSign(bytes);
    assertArrayEquals(bytes, context.getBytesToSign());
    assertNotSame(bytes, context.getBytesToSign());
  }


  @Test(expected = IllegalStateException.class)
  public void testBytesToSignMustBeSet() {
    context.getBytesToSign();
  }


  @Test(expected = IllegalArgumentException.class)
  public void testBytesToSignNotNull() {
    context.setBytesToSign(null);
  }


  @Test(expected = IllegalStateException.class)
  public void testDidIdMustBeSet() {
    context.getDidId();
  }


  @Test
  public void testDidWithKey() {
    DidId didId = new DidId("setl", "theman", null, null, "lock");
    context.setDidWithKey(didId);
    assertEquals(URI.create("did:setl:theman"), context.getDidId());
    assertEquals(didId, context.getDidWithKey());
    assertEquals("lock", context.getKeyId());

  }


  @Test(expected = IllegalStateException.class)
  public void testDidWithKeyMustBeSet() {
    context.getDidWithKey();
  }


  @Test(expected = IllegalArgumentException.class)
  public void testDidWithKeyNotNull() {
    context.setDidWithKey(null);
  }


  @Test(expected = IllegalArgumentException.class)
  public void testDidWithKeyNotValid() {
    context.setDidWithKey(new DidId("!", "$", null, null, null));
  }


  @Test(expected = IllegalStateException.class)
  public void testKeyIdMustBeSet() {
    context.getKeyId();
  }

}