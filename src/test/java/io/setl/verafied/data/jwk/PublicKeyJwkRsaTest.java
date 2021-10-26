package io.setl.verafied.data.jwk;

import java.security.GeneralSecurityException;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Simon Greatrix on 04/07/2020.
 */
public class PublicKeyJwkRsaTest {

  @Test
  public void test256() throws GeneralSecurityException, JsonProcessingException {
    PublicKeyJwkFactoryTest.testAlgorithm(SigningAlgorithm.RS256);
  }


  @Test
  @Ignore // Ignore as it takes ages
  public void test384() throws GeneralSecurityException, JsonProcessingException {
    PublicKeyJwkFactoryTest.testAlgorithm(SigningAlgorithm.RS384);
  }


  @Test
  @Ignore // Ignore as it takes ages
  public void test512() throws GeneralSecurityException, JsonProcessingException {
    PublicKeyJwkFactoryTest.testAlgorithm(SigningAlgorithm.RS512);
  }

}