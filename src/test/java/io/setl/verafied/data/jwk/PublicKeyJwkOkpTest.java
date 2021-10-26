package io.setl.verafied.data.jwk;

import java.security.GeneralSecurityException;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;


/**
 * @author Simon Greatrix on 04/07/2020.
 */
public class PublicKeyJwkOkpTest {


  @Test
  public void test25519() throws GeneralSecurityException, JsonProcessingException {
    PublicKeyJwkFactoryTest.testAlgorithm(SigningAlgorithm.ED25519);
  }


  @Test
  public void test448() throws GeneralSecurityException, JsonProcessingException {
    PublicKeyJwkFactoryTest.testAlgorithm(SigningAlgorithm.ED448);
  }

}