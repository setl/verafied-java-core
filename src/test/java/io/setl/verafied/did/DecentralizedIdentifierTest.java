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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.EnumSet;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.json.Json;
import javax.json.JsonPatchBuilder;

import org.junit.Test;

/**
 * @author Simon Greatrix on 15/07/2020.
 */
public class DecentralizedIdentifierTest {

  DecentralizedIdentifier did = new DecentralizedIdentifier();

  JsonPatchBuilder jpb = Json.createPatchBuilder();


  @Test
  public void assertionMethod() {
    testList(
        did::setAssertionMethod,
        did::getAssertionMethod,
        did::addAssertionMethod,
        did::removeAssertionMethod
    );
  }


  @Test
  public void authentication() {
    testList(
        did::setAuthentication,
        did::getAuthentication,
        did::addAuthentication,
        did::removeAuthentication
    );
  }


  @Test
  public void capabilityDelegation() {
    testList(
        did::setCapabilityDelegation,
        did::getCapabilityDelegation,
        did::addCapabilityDelegation,
        did::removeCapabilityDelegation
    );
  }


  @Test
  public void capabilityInvocation() {
    testList(
        did::setCapabilityInvocation,
        did::getCapabilityInvocation,
        did::addCapabilityInvocation,
        did::removeCapabilityInvocation
    );
  }


  @Test
  public void controller() {
    testList(
        did::setController,
        did::getController,
        did::addController,
        did::removeController
    );
  }


  @Test
  public void getContext() {
    assertNotNull(did.getContext());
  }


  @Test
  public void getCreated() {
    assertNull(did.getCreated());
    Instant now = Instant.now();
    did.setCreated(now);
    assertEquals(now, did.getCreated());
  }


  @Test
  public void getId() {
    assertNull(did.getId());
    assertNull(did.getInternalId());
    did.setId(URI.create("did:setl:wibble"));
    assertEquals("did:setl:wibble", did.getId().toString());
    assertEquals("wibble", did.getInternalId());
  }


  @Test
  public void getUpdated() {
    assertNull(did.getUpdated());
    did.setUpdated(null);
    Instant i = did.getUpdated();
    assertNotNull(i);
    did.setUpdated(i.minus(1, ChronoUnit.MINUTES));
    assertEquals(i, did.getUpdated());
  }


  private void testList(
      Consumer<List<URI>> setter,
      Supplier<List<URI>> getter,
      BiConsumer<JsonPatchBuilder, URI> add,
      BiConsumer<JsonPatchBuilder, URI> remove
  ) {
    assertTrue(getter.get().isEmpty());
    List<URI> list1 = List.of(URI.create("did:setl:one"));
    setter.accept(list1);
    assertEquals(list1, getter.get());
    assertNotSame(list1, getter.get());
    add.accept(jpb, URI.create("did:setl:two"));
    add.accept(jpb, URI.create("did:setl:one"));

    String json = jpb.build().toJsonArray().toString().replaceAll("/[A-Za-z]*/", "/=/");
    assertEquals("[{\"op\":\"add\",\"path\":\"/=/-\",\"value\":\"did:setl:two\"}]", json);

    remove.accept(jpb, URI.create("did:setl:three"));
    remove.accept(jpb, URI.create("did:setl:one"));
    json = jpb.build().toJsonArray().toString().replaceAll("/[A-Za-z]*/", "/=/");
    assertEquals(
        "[{\"op\":\"add\",\"path\":\"/=/-\",\"value\":\"did:setl:two\"},{\"op\":\"test\",\"path\":\"/=/0\",\"value\":\"did:setl:one\"},{\"op\":\"remove\",\"path\":\"/=/0\"}]",
        json
    );
    assertEquals(List.of(URI.create("did:setl:two")), getter.get());
  }


  @Test
  public void updated() {
  }


  @Test
  public void verificationMethod() {
    VerificationMethod vm1 = new VerificationMethod();
    vm1.setId(URI.create("did:setl:user#vm1"));

    VerificationMethod vm2 = new VerificationMethod();
    vm2.setId(URI.create("did:setl:user#vm2"));

    assertTrue(did.getVerificationMethod().isEmpty());
    List<VerificationMethod> list1 = List.of(vm1);
    did.setVerificationMethod(list1);
    assertEquals(list1, did.getVerificationMethod());
    assertNotSame(list1, did.getVerificationMethod());
    did.addVerificationMethod(jpb, vm2, EnumSet.allOf(KeyUsage.class));
    did.addVerificationMethod(jpb, vm1, EnumSet.noneOf(KeyUsage.class));

    String json = jpb.build().toJsonArray().toString().replaceAll("/[A-Za-z]*/", "/=/");
    assertEquals(
        "[{\"op\":\"add\",\"path\":\"/=/-\",\"value\":{\"controller\":[],\"id\":\"did:setl:user#vm2\",\"publicKeyJwk\":null,\"typ\":null}},{\"op\":\"add\",\"path\":\"/=/-\",\"value\":\"did:setl:user#vm2\"},{\"op\":\"add\",\"path\":\"/=/-\",\"value\":\"did:setl:user#vm2\"},{\"op\":\"add\",\"path\":\"/=/-\",\"value\":\"did:setl:user#vm2\"},{\"op\":\"add\",\"path\":\"/=/-\",\"value\":\"did:setl:user#vm2\"}]",
        json
    );

    did.removeVerificationMethod(jpb, URI.create("did:setl:user#vm3"));
    did.removeVerificationMethod(jpb, URI.create("did:setl:user#vm1"));
    json = jpb.build().toJsonArray().toString().replaceAll("/[A-Za-z]*/", "/=/");
    assertEquals(
        "[{\"op\":\"add\",\"path\":\"/=/-\",\"value\":{\"controller\":[],\"id\":\"did:setl:user#vm2\",\"publicKeyJwk\":null,\"typ\":null}},{\"op\":\"add\",\"path\":\"/=/-\",\"value\":\"did:setl:user#vm2\"},{\"op\":\"add\",\"path\":\"/=/-\",\"value\":\"did:setl:user#vm2\"},{\"op\":\"add\",\"path\":\"/=/-\",\"value\":\"did:setl:user#vm2\"},{\"op\":\"add\",\"path\":\"/=/-\",\"value\":\"did:setl:user#vm2\"},{\"op\":\"test\",\"path\":\"/=/0/id\",\"value\":\"did:setl:user#vm1\"},{\"op\":\"remove\",\"path\":\"/=/0\"}]",
        json
    );
    assertEquals(List.of(vm2), did.getVerificationMethod());
  }

}
