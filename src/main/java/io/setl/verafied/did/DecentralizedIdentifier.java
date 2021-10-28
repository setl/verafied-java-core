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

import java.net.URI;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonPatchBuilder;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.setl.verafied.data.JsonConvert;
import io.setl.verafied.did.validate.DidUri;

/**
 * A DecentralizedIdentifier definition document.
 *
 * @author Simon Greatrix on 26/06/2020.
 */
public class DecentralizedIdentifier {

  private static void addMethod(List<URI> list, JsonPatchBuilder builder, String path, URI method) {
    if (list.contains(method)) {
      return;
    }
    list.add(method);
    builder.add(path, method.toString());
  }


  private static void removeMethod(List<URI> list, JsonPatchBuilder builder, String path, URI method) {
    int i = list.indexOf(method);
    if (i == -1) {
      return;
    }

    list.remove(i);
    String p = path + i;
    builder.test(p, method.toString());
    builder.remove(p);
  }


  /** List of verification methods that this identifier can use to assert things. */
  private final List<URI> assertionMethod = new ArrayList<>();

  /** List of verification methods that this identifier can use to authenticate things. */
  private final List<URI> authentication = new ArrayList<>();

  /** List of verification methods that this identifier can use to approve a delegation of their authority. */
  private final List<URI> capabilityDelegation = new ArrayList<>();

  /** List of verification methods that this identifier can use to invoke one of the powers their authority grants them. */
  private final List<URI> capabilityInvocation = new ArrayList<>();

  /** List of Decentralized Identifier that control the use of this identifier. */
  private final List<URI> controller = new ArrayList<>();

  /** Methods this identifier can use to assert, authenticate, invoke, or delegate. */
  private final List<VerificationMethod> verificationMethod = new ArrayList<>();

  /** The time at which this identifier was created. */
  private Instant created;

  /** The identifier of this. */
  private URI id;

  /** The time at which this identifier was updated. */
  private Instant updated;


  /**
   * Add an assertion method to this.
   *
   * @param builder patch builder for updating the JSON equivalent of this
   * @param method  the method to add
   */
  public void addAssertionMethod(JsonPatchBuilder builder, URI method) {
    addMethod(assertionMethod, builder, "/assertionMethod/-", method);
  }


  /**
   * Add an authentication method to this.
   *
   * @param builder patch builder for updating the JSON equivalent of this
   * @param method  the method to add
   */
  public void addAuthentication(JsonPatchBuilder builder, URI method) {
    addMethod(authentication, builder, "/authentication/-", method);
  }


  /**
   * Add a capability delegation method to this.
   *
   * @param builder patch builder for updating the JSON equivalent of this
   * @param method  the method to add
   */
  public void addCapabilityDelegation(JsonPatchBuilder builder, URI method) {
    addMethod(capabilityDelegation, builder, "/capabilityDelegation/-", method);
  }


  /**
   * Add a capability invocation method to this.
   *
   * @param builder patch builder for updating the JSON equivalent of this
   * @param method  the method to add
   */
  public void addCapabilityInvocation(JsonPatchBuilder builder, URI method) {
    addMethod(capabilityInvocation, builder, "/capabilityInvocation/-", method);
  }


  /**
   * Add a controller to this.
   *
   * @param builder patch builder for updating the JSON equivalent of this
   * @param id      the controller to add
   */
  public void addController(JsonPatchBuilder builder, URI id) {
    addMethod(controller, builder, "/controller/-", id);
  }


  /**
   * Add a verification method to this.
   *
   * @param builder   patch builder for updating the JSON equivalent of this
   * @param newMethod the method to add
   * @param usage     the uses to which the new method can be put
   */
  public void addVerificationMethod(JsonPatchBuilder builder, VerificationMethod newMethod, Set<KeyUsage> usage) {
    URI methodId = newMethod.getId();
    if (methodId == null) {
      throw new IllegalArgumentException("Verification method has no assigned ID");
    }
    // check ID not already in use
    for (VerificationMethod method : verificationMethod) {
      if (methodId.equals(method.getId())) {
        // already exists
        return;
      }
    }

    verificationMethod.add(newMethod);
    builder.add("/verificationMethod/-", JsonConvert.toJson(newMethod));
    for (KeyUsage u : usage) {
      switch (u) {
        case ASSERTION:
          addAssertionMethod(builder, methodId);
          break;
        case AUTHENTICATION:
          addAuthentication(builder, methodId);
          break;
        case CAPABILITY_DELEGATION:
          addCapabilityDelegation(builder, methodId);
          break;
        case CAPABILITY_INVOCATION:
          addCapabilityInvocation(builder, methodId);
          break;
        default:
          throw new IllegalArgumentException("Unknown key usage: " + u);
      }
    }
  }


  public List<URI> getAssertionMethod() {
    return Collections.unmodifiableList(assertionMethod);
  }


  public List<URI> getAuthentication() {
    return Collections.unmodifiableList(authentication);
  }


  public List<URI> getCapabilityDelegation() {
    return Collections.unmodifiableList(capabilityDelegation);
  }


  public List<URI> getCapabilityInvocation() {
    return Collections.unmodifiableList(capabilityInvocation);
  }


  @JsonProperty("@context")
  public JsonArray getContext() {
    return Json.createArrayBuilder().add("https://www.w3.org/ns/did/v1").build();
  }


  /**
   * The entities that can control and update this DID. Each entity is represented by their own DID URN.
   *
   * @return list of controlling entities
   */
  @JsonProperty("controller")
  public List<URI> getController() {
    return Collections.unmodifiableList(controller);
  }


  @JsonProperty("created")
  @JsonFormat(shape = Shape.STRING)
  public Instant getCreated() {
    return created;
  }


  /**
   * The "did:" URN that identifies this DID. This is the "DID Subject".
   *
   * @return the ID
   */
  @JsonProperty("id")
  @NotNull
  @DidUri
  public URI getId() {
    return id;
  }


  /**
   * Get the internal ID, which is the last part of the DID URI.
   *
   * @return the internal ID
   */
  @JsonIgnore
  public String getInternalId() {
    return (id != null) ? new DidId(id).getId() : null;
  }


  @JsonProperty("updated")
  @JsonFormat(shape = Shape.STRING)
  public Instant getUpdated() {
    return updated;
  }


  /**
   * Get a copy of the verification methods.
   *
   * @return a copy of the verification methods
   */
  @JsonProperty("verificationMethod")
  public List<@Valid VerificationMethod> getVerificationMethod() {
    ArrayList<VerificationMethod> list = new ArrayList<>(verificationMethod.size());
    verificationMethod.forEach(m -> list.add(m.copy()));
    return list;
  }


  /**
   * Remove an assertion method from this.
   *
   * @param builder patch builder for updating the JSON equivalent of this
   * @param method  the method to remove
   */
  public void removeAssertionMethod(JsonPatchBuilder builder, URI method) {
    removeMethod(assertionMethod, builder, "/assertionMethod/", method);
  }


  /**
   * Remove an authentication method from this.
   *
   * @param builder patch builder for updating the JSON equivalent of this
   * @param method  the method to remove
   */
  public void removeAuthentication(JsonPatchBuilder builder, URI method) {
    removeMethod(authentication, builder, "/authentication/", method);
  }


  /**
   * Remove a capability delegation method from this.
   *
   * @param builder patch builder for updating the JSON equivalent of this
   * @param method  the method to remove
   */
  public void removeCapabilityDelegation(JsonPatchBuilder builder, URI method) {
    removeMethod(capabilityDelegation, builder, "/capabilityDelegation/", method);
  }


  /**
   * Remove a capability invocation method from this.
   *
   * @param builder patch builder for updating the JSON equivalent of this
   * @param method  the method to remove
   */
  public void removeCapabilityInvocation(JsonPatchBuilder builder, URI method) {
    removeMethod(capabilityInvocation, builder, "/capabilityInvocation/", method);
  }


  /**
   * Remove a controller from this.
   *
   * @param builder patch builder for updating the JSON equivalent of this
   * @param id      the controller to remove
   */
  public void removeController(JsonPatchBuilder builder, URI id) {
    removeMethod(controller, builder, "/controller/", id);
  }


  /**
   * Remove a verification method from this.
   *
   * @param builder patch builder for updating the JSON equivalent of this
   * @param method  the method to remove
   */
  public void removeVerificationMethod(JsonPatchBuilder builder, URI method) {
    int index = -1;
    for (int i = 0; i < verificationMethod.size(); i++) {
      if (verificationMethod.get(i).getId().equals(method)) {
        index = i;
        break;
      }
    }

    if (index != -1) {
      verificationMethod.remove(index);
      builder.test("/verificationMethod/" + index + "/id", method.toString());
      builder.remove("/verificationMethod/" + index);
    }

    removeAssertionMethod(builder, method);
    removeAuthentication(builder, method);
    removeCapabilityDelegation(builder, method);
    removeCapabilityInvocation(builder, method);
  }


  public void setAssertionMethod(List<URI> assertionMethod) {
    this.assertionMethod.clear();
    this.assertionMethod.addAll(assertionMethod);
  }


  public void setAuthentication(List<URI> authentication) {
    this.authentication.clear();
    this.authentication.addAll(authentication);
  }


  public void setCapabilityDelegation(List<URI> capabilityDelegation) {
    this.capabilityDelegation.clear();
    this.capabilityDelegation.addAll(capabilityDelegation);
  }


  public void setCapabilityInvocation(List<URI> capabilityInvocation) {
    this.capabilityInvocation.clear();
    this.capabilityInvocation.addAll(capabilityInvocation);
  }


  public void setController(List<URI> controller) {
    this.controller.clear();
    this.controller.addAll(controller);
  }


  public void setCreated(Instant created) {
    this.created = created;
  }


  public void setId(URI id) {
    this.id = id;
  }


  /**
   * Set when this was updated. Cannot be set to an earlier time.
   *
   * @param updated the time of the updated. If null, now is assumed.
   */
  public void setUpdated(Instant updated) {
    if (updated == null) {
      updated = Instant.now();
    }
    if (this.updated == null || this.updated.isBefore(updated)) {
      this.updated = updated;
    }
  }


  public void setVerificationMethod(List<VerificationMethod> verificationMethod) {
    this.verificationMethod.clear();
    this.verificationMethod.addAll(verificationMethod);
  }


  public void updated(JsonPatchBuilder builder) {
    updated = Instant.now();
    builder.replace("/updated", DateTimeFormatter.ISO_INSTANT.format(updated));
  }

}
