package io.setl.verafied.data.jwk;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.Signature;
import java.util.EnumSet;
import java.util.Set;

import org.junit.Test;

/**
 * @author Simon Greatrix on 10/07/2020.
 */
public class SigningAlgorithmTest {


  @Test
  public void createSignature() throws GeneralSecurityException {
    byte[] message = "Hello, World!".getBytes(StandardCharsets.US_ASCII);
    Set<SigningAlgorithm> algsToTest = EnumSet.allOf(SigningAlgorithm.class);
    algsToTest.remove(SigningAlgorithm.NONE);
    algsToTest.remove(SigningAlgorithm.RS384);
    algsToTest.remove(SigningAlgorithm.RS512);
    algsToTest.remove(SigningAlgorithm.PS384);
    algsToTest.remove(SigningAlgorithm.PS512);
    for (SigningAlgorithm alg : algsToTest) {
      KeyPair keyPair = alg.createKeyPair();
      System.out.println(keyPair.getPublic());
      Signature signature = alg.createSignature();
      signature.initSign(keyPair.getPrivate());
      signature.update(message);
      byte[] out = signature.sign();

      signature = alg.createSignature();
      signature.initVerify(keyPair.getPublic());
      signature.update(message);
      assertTrue(signature.verify(out));
    }
  }


  @Test
  public void testGetters() {
    for (SigningAlgorithm alg : SigningAlgorithm.values()) {
      if (alg == SigningAlgorithm.NONE) {
        continue;
      }
      assertNotNull(alg.getDescription());
      assertNotNull(alg.getGeneratorName());
      assertNotNull(alg.getGeneratorSpec());
      assertNotNull(alg.getJwkName());
      assertNotNull(alg.getKeyType());
      assertNotNull(alg.getSigningName());
    }
  }

}