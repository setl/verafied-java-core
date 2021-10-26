package io.setl.verafied.data.jwk;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import javax.json.JsonStructure;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.setl.verafied.data.JsonConvert;

/**
 * @author Simon Greatrix on 05/07/2020.
 */
public class PublicKeyJwkFactoryTest {

  public static void testAlgorithm(SigningAlgorithm algorithm) throws GeneralSecurityException, JsonProcessingException {
    KeyPair keyPair = algorithm.createKeyPair();
    PublicKeyJwk jwk = PublicKeyJwkFactory.from(keyPair.getPublic());
    JsonStructure json = JsonConvert.toJson(jwk);
    PublicKeyJwk jwk2 = JsonConvert.toInstance(json, PublicKeyJwk.class);
    // JSON should be the same
    //System.out.println(json);
    assertEquals(json, JsonConvert.toJson(jwk2));

    // Public keys should be the same
    assertArrayEquals(keyPair.getPublic().getEncoded(), jwk2.getPublicKey().getEncoded());

    assertEquals(jwk, jwk2);
    assertEquals(jwk.hashCode(), jwk2.hashCode());
  }

}