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
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>;.
 *
 * </notice>
 */

package io.setl.verafied.data.credential;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import javax.json.JsonObject;
import javax.json.JsonValue;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import io.setl.verafied.CredentialConstants;
import io.setl.verafied.data.Proof;

/**
 * A builder for credentials.
 *
 * @author Simon Greatrix on 03/11/2021.
 */
@SuppressFBWarnings("EI_EXPOSE_REP2") // The Credential class creates defensive copies, so no need to do it here
public final class CredentialBuilder {

  private JsonValue context;

  private CredentialStatus credentialStatus;

  private JsonObject credentialSubject = JsonValue.EMPTY_JSON_OBJECT;

  private Instant expirationDate;

  private URI id;

  private Instant issuanceDate;

  private URI issuer;

  private Proof proof;

  private Set<String> type = Credential.MINIMAL_TYPE;


  /**
   * New instance.
   */
  public CredentialBuilder() {
    context = CredentialConstants.getStandardContext();
    issuanceDate = CredentialConstants.getClock().instant();
    expirationDate = issuanceDate.plus(Duration.ofDays(365));
  }


  /**
   * Build the credential.
   *
   * @return the credential
   */
  public Credential build() {
    Credential credential = new Credential();
    credential.setContext(context);
    credential.setCredentialStatus(credentialStatus);
    credential.setCredentialSubject(credentialSubject);
    credential.setExpirationDate(expirationDate);
    credential.setId(id);
    credential.setIssuanceDate(issuanceDate);
    credential.setIssuer(issuer);
    credential.setType(type);

    // Proof must be set last
    credential.setProof(proof);

    return credential;
  }


  @JsonProperty("@context")
  public CredentialBuilder withContext(JsonValue context) {
    this.context = context;
    return this;
  }


  public CredentialBuilder withCredentialStatus(CredentialStatus credentialStatus) {
    this.credentialStatus = credentialStatus;
    return this;
  }


  public CredentialBuilder withCredentialSubject(JsonObject credentialSubject) {
    this.credentialSubject = credentialSubject;
    return this;
  }


  public CredentialBuilder withExpirationDate(Instant expirationDate) {
    this.expirationDate = expirationDate;
    return this;
  }


  public CredentialBuilder withId(URI id) {
    this.id = id;
    return this;
  }


  public CredentialBuilder withIssuanceDate(Instant issuanceDate) {
    this.issuanceDate = issuanceDate;
    return this;
  }


  public CredentialBuilder withIssuer(URI issuer) {
    this.issuer = issuer;
    return this;
  }


  public CredentialBuilder withProof(Proof proof) {
    this.proof = proof;
    return this;
  }


  public CredentialBuilder withType(Set<String> type) {
    this.type = type;
    return this;
  }

}
