package io.setl.verafied;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.security.SecureRandom;
import java.util.function.UnaryOperator;
import javax.json.JsonValue;
import javax.json.spi.JsonProvider;

import org.junit.Test;

/**
 * @author Simon Greatrix on 05/11/2021.
 */
public class CredentialConstantsTest {

  @Test
  public void testLogSafe() {
    UnaryOperator<String> original = CredentialConstants.getLogSafe();
    UnaryOperator<String> safer = String::toUpperCase;
    CredentialConstants.setLogSafe(safer);
    assertSame(safer, CredentialConstants.getLogSafe());
    assertEquals("FRED", CredentialConstants.logSafe("Fred"));
    CredentialConstants.setLogSafe(original);
  }


  @Test
  public void testSetRandom() {
    assertNotNull(CredentialConstants.getSecureRandom());
    SecureRandom random = new SecureRandom();
    CredentialConstants.setSecureRandom(random);
    assertSame(random, CredentialConstants.getSecureRandom());
  }


  @Test
  public void testStandardContext() {
    JsonValue original = CredentialConstants.getStandardContext();
    JsonValue value = JsonProvider.provider().createValue("my context");
    CredentialConstants.setStandardContext(value);
    assertEquals(value, CredentialConstants.getStandardContext());
    CredentialConstants.setStandardContext(original);
  }

}