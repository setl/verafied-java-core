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

package io.setl.verafied.data.credential;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;

import io.setl.verafied.CredentialConstants;
import io.setl.verafied.data.JsonConvert;
import io.setl.verafied.data.Proof;
import io.setl.verafied.data.Provable;
import io.setl.verafied.did.validate.DidUri;

/**
 * Representation of a verifiable credential.
 *
 * @author Simon Greatrix on 20/07/2020.
 */
@Schema(
    description = "A verifiable credential"
)
@JsonDeserialize(builder = CredentialBuilder.class)
public class Credential implements Provable {

  static final Set<String> MINIMAL_TYPE = Set.of(CredentialConstants.VERIFIABLE_CREDENTIAL_TYPE);

  /** The applicable contexts. */
  private JsonValue context = null;

  /** Mechanism to check this credential's status. */
  private CredentialStatus credentialStatus;

  /** The actual credential subjects. */
  private JsonObject credentialSubject = JsonValue.EMPTY_JSON_OBJECT;

  /** The expiration date. */
  private Instant expirationDate;

  /** The optional URI for this credential. */
  private URI id;

  /** When this credential was issued. */
  private Instant issuanceDate;

  /** The issuer of this credential. */
  private URI issuer;

  /** The cryptographic proof associated with this. */
  @JsonInclude(Include.NON_NULL)
  private Proof proof;

  /** The types present in the credentials. */
  private Set<String> type = MINIMAL_TYPE;


  /**
   * New instance.
   */
  public Credential() {
    context = CredentialConstants.getStandardContext();
    issuanceDate = CredentialConstants.getClock().instant();
    expirationDate = issuanceDate.plus(Duration.ofDays(365));
  }


  @Override
  public JsonObject asJson() {
    return JsonConvert.toJson(this).asJsonObject();
  }


  @JsonProperty("@context")
  @Schema(
      description = "The JSON-LD context, if required",
      ref = "#/components/schemas/json.value",
      required = true
  )
  @NotNull
  public JsonValue getContext() {
    return context;
  }


  @JsonProperty("credentialStatus")
  @JsonInclude(Include.NON_NULL)
  @Schema(
      description = "Specification of a web endpoint that can be used to check if a credential is still valid."
  )
  public CredentialStatus getCredentialStatus() {
    return credentialStatus;
  }


  @Schema(
      description = "The information that constitutes the credential and which is described by the type.", ref = "#/components/schemas/json.object"
  )
  public JsonObject getCredentialSubject() {
    return credentialSubject;
  }


  @Schema(
      description = "The date and time when this credential will expire. Defaults to 365 days from creation."
  )
  @JsonInclude(Include.NON_NULL)
  @JsonFormat(shape = Shape.STRING)
  public Instant getExpirationDate() {
    return expirationDate;
  }


  @Schema(
      description = "An OPTIONAL URI from which this credential may be retrieved."
  )
  @JsonInclude(Include.NON_NULL)
  public URI getId() {
    return id;
  }


  @Schema(
      description = "The date and time when this credential was issued. Automatically set on creation if not specified."
  )
  @JsonFormat(shape = Shape.STRING)
  public Instant getIssuanceDate() {
    return issuanceDate;
  }


  @Schema(
      description = "The issuer of this credential"
  )
  @DidUri
  @NotNull
  public URI getIssuer() {
    return issuer;
  }


  @Schema(
      description = "The cryptographic proof associated with this."
  )
  public Proof getProof() {
    return proof != null ? new Proof(proof) : null;
  }


  @Schema(
      description = "An unordered set of type definitions which must be URIs or mappable to URIs via the context. "
          + "These define the data type of the credential subjects."
  )
  public Set<String> getType() {
    return Collections.unmodifiableSet(type);
  }


  /**
   * Set the JSON-LD context for the credential, if required.
   *
   * @param newContext the new context.
   */
  public void setContext(JsonValue newContext) {
    checkNotProven();
    context = newContext;
  }


  public void setCredentialStatus(CredentialStatus credentialStatus) {
    checkNotProven();
    this.credentialStatus = credentialStatus;
  }


  /**
   * The information that constitutes the credential.
   *
   * @param credentialSubject the information
   */
  public void setCredentialSubject(JsonObject credentialSubject) {
    checkNotProven();
    this.credentialSubject = credentialSubject;
  }


  /**
   * Set this credential's expiration date.
   *
   * @param expirationDate the expiration date
   */
  public void setExpirationDate(Instant expirationDate) {
    checkNotProven();
    this.expirationDate = expirationDate;
  }


  /**
   * Set the URI that identifies this credential, if it has such a URI.
   *
   * @param id the identifying URI (optional)
   */
  public void setId(URI id) {
    checkNotProven();
    this.id = id;
  }


  public void setIssuanceDate(Instant issuanceDate) {
    checkNotProven();
    this.issuanceDate = issuanceDate;
  }


  public void setIssuer(URI issuer) {
    checkNotProven();
    this.issuer = issuer;
  }


  public void setProof(Proof proof) {
    this.proof = proof != null ? new Proof(proof) : null;
  }


  /**
   * Set the data types of the credentials. The type 'VerifiableCredential' is always present but more refined sub types are encouraged.
   *
   * @param newType the new types
   */
  public void setType(Set<String> newType) {
    checkNotProven();
    if (newType == null || newType.isEmpty()) {
      type = MINIMAL_TYPE;
      return;
    }
    if (!newType.contains(CredentialConstants.VERIFIABLE_CREDENTIAL_TYPE)) {
      throw new IllegalArgumentException("Type set must contain: " + CredentialConstants.VERIFIABLE_CREDENTIAL_TYPE);
    }
    type = new LinkedHashSet<>(newType);
  }


}
