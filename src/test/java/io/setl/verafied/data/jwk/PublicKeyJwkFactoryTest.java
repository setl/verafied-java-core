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
