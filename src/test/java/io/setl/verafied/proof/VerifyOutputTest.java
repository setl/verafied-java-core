package io.setl.verafied.proof;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Test;

import io.setl.verafied.UnacceptableDocumentException;

/**
 * @author Simon Greatrix on 05/11/2021.
 */
public class VerifyOutputTest {

  Map<String, Object> params = Map.of("a", new Object(), "b", "c");

  VerifyOutput fail1 = VerifyOutput.fail("code1", "this is a message", params);


  @Test
  public void convert() {
    try {
      fail1.throwIfFailed();
      fail();
    } catch (UnacceptableDocumentException e) {
      VerifyOutput fail2 = new VerifyOutput(e);
      assertEquals(fail1, fail2);
      assertEquals(fail1.hashCode(), fail2.hashCode());
    }
  }


  @SuppressWarnings("java:S5785") // for testing of equals(Object)
  @Test
  public void equals() {
    VerifyOutput o1 = new VerifyOutput(true, null, null, null);
    assertEquals(VerifyOutput.OK, o1);
    assertEquals(o1, o1);
    assertEquals(fail1, fail1);
    assertFalse(fail1.equals(null));
    assertFalse(fail1.equals(""));
  }


  @Test
  public void get() {
    assertNull(VerifyOutput.OK.getCode());
    assertNull(VerifyOutput.OK.getMessage());
    assertTrue(VerifyOutput.OK.getParameters().isEmpty());

    assertEquals("code1", fail1.getCode());
    assertEquals("this is a message", fail1.getMessage());
    assertEquals(params, fail1.getParameters());
  }


  @Test
  public void testIsOk() throws UnacceptableDocumentException {
    assertTrue(VerifyOutput.OK.isOk());
    assertEquals(VerifyOutput.OK, new VerifyOutput());
    assertFalse(fail1.isOk());
    VerifyOutput.OK.throwIfFailed();
  }

}