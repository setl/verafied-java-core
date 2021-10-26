package io.setl.verafied.data.jwk;

import java.security.GeneralSecurityException;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;

/**
 * @author Simon Greatrix on 05/07/2020.
 */
public class PublicKeyJwkEcTest {

  @Test
  public void testEs256() throws GeneralSecurityException, JsonProcessingException {
    PublicKeyJwkFactoryTest.testAlgorithm(SigningAlgorithm.ES256);
  }


  @Test
  public void testEs384() throws GeneralSecurityException, JsonProcessingException {
    PublicKeyJwkFactoryTest.testAlgorithm(SigningAlgorithm.ES384);
  }


  @Test
  public void testEs512() throws GeneralSecurityException, JsonProcessingException {
    PublicKeyJwkFactoryTest.testAlgorithm(SigningAlgorithm.ES512);
  }


  @Test
  public void testSecp256K1() throws GeneralSecurityException, JsonProcessingException {
    PublicKeyJwkFactoryTest.testAlgorithm(SigningAlgorithm.ES256K);
  }

}