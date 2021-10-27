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
