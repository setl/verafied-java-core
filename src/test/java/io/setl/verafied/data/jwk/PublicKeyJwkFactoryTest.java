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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.util.Base64;
import javax.json.JsonStructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.v3.oas.annotations.media.Schema;
import org.junit.Test;

import io.setl.json.Canonical;
import io.setl.verafied.data.JsonConvert;

/**
 * @author Simon Greatrix on 05/07/2020.
 */
public class PublicKeyJwkFactoryTest {

  @SuppressWarnings("java:S5785")
  public static void testAlgorithm(SigningAlgorithm algorithm) throws GeneralSecurityException, JsonProcessingException {
    KeyPair keyPair = algorithm.createKeyPair();
    PublicKeyJwk jwk = PublicKeyJwkFactory.from(keyPair.getPublic());
    JsonStructure json = JsonConvert.toJson(jwk);
    PublicKeyJwk jwk2 = JsonConvert.toInstance(json, PublicKeyJwk.class);

    // JSON should be the same
    assertEquals(json, JsonConvert.toJson(jwk2));

    // Public keys should be the same
    assertArrayEquals(keyPair.getPublic().getEncoded(), jwk2.getPublicKey().getEncoded());

    assertEquals(jwk, jwk2);
    assertEquals(jwk.hashCode(), jwk2.hashCode());
    assertEquals(jwk, jwk.copy());

    assertTrue(jwk.equals(jwk));
    assertFalse(jwk.equals(null));
    assertFalse(jwk.equals(""));

    jwk2.setUse("foobar");
    assertFalse(jwk.equals(jwk2));
  }

  @Test
  public void test() {
    SigningAlgorithm algorithm = SigningAlgorithm.PS256;
    KeyPair keyPair = algorithm.createKeyPair();
    PublicKeyJwk jwk = PublicKeyJwkFactory.from(keyPair.getPublic());
    JsonStructure json = JsonConvert.toJson(jwk);
    System.out.println(Canonical.cast(json).toPrettyString());

    System.out.println(Base64.getMimeEncoder().encodeToString(keyPair.getPrivate().getEncoded()));
  }

}
