/* <notice>
 *
 *   SETL Blockchain
 *   Copyright (C) 2021 SETL Ltd
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License, version 3, as
 *   published by the Free Software Foundation.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * </notice>
 */

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
