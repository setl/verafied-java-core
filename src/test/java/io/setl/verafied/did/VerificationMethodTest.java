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

package io.setl.verafied.did;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

import java.net.URI;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.ECGenParameterSpec;
import java.util.List;

import org.junit.Test;

import io.setl.verafied.data.jwk.PublicKeyJwk;
import io.setl.verafied.data.jwk.PublicKeyJwkFactory;

/**
 * @author Simon Greatrix on 16/07/2020.
 */
public class VerificationMethodTest {

  @Test
  public void tests() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
    KeyPairGenerator generator = KeyPairGenerator.getInstance("EC");
    generator.initialize(new ECGenParameterSpec("NIST P-256"));
    KeyPair keyPair = generator.generateKeyPair();

    VerificationMethod vm = new VerificationMethod();
    vm.setId(URI.create("did:setl:user#wibble"));
    assertEquals(URI.create("did:setl:user#wibble"), vm.getId());
    VerificationMethod vm2 = new VerificationMethod();
    vm2.setId(URI.create("did:setl:user#wobble"));
    assertNotEquals(vm, vm2);
    vm2.setId(URI.create("did:setl:user#wibble"));
    assertEquals(vm, vm2);
    assertEquals(vm.hashCode(), vm2.hashCode());
    assertEquals(vm,vm);
    assertFalse(vm.equals(null));
    assertFalse(vm.equals(""));

    PublicKeyJwk jwk = PublicKeyJwkFactory.from(keyPair.getPublic());
    jwk.setKeyId(URI.create("did:setl:user#test"));
    vm.setId(null);
    assertNull(vm.getId());
    vm.setPublicKeyJwk(jwk);
    assertEquals(jwk, vm.getPublicKeyJwk());
    assertEquals(URI.create("did:setl:user#test"), vm.getId());

    List<URI> l = List.of(URI.create("did:setl:a"));
    vm.setController(l);
    assertEquals(l, vm.getController());
    assertNotSame(l, vm.getController());
  }

}
