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

package io.setl.verafied.data.presentation;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.validation.constraints.AssertTrue;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;

import io.setl.verafied.CredentialConstants;
import io.setl.verafied.data.JsonConvert;
import io.setl.verafied.data.Proof;
import io.setl.verafied.data.Provable;
import io.setl.verafied.data.credential.Credential;
import io.setl.verafied.did.validate.DidUri;

/**
 * A representation of a verifiable presentation.
 *
 * @author Simon Greatrix on 20/07/2020.
 */
@Schema(
    description = "A verifiable presentation"
)
@JsonDeserialize(builder = PresentationBuilder.class)
public class Presentation implements Provable {

  /** The default context for a presentation. */
  static final JsonArray DEFAULT_CONTEXT = CredentialConstants.JSON_PROVIDER.createArrayBuilder().add(CredentialConstants.CREDENTIAL_CONTEXT).build();

  /** The applicable contexts. */
  private JsonValue context = DEFAULT_CONTEXT;

  /** The issuer of this presentation. */
  private URI holder;

  /** The optional URI for this presentation. */
  private URI id;

  /** The cryptographic proof associated with this. */
  private Proof proof;

  /** The types present in the credentials. */
  private Set<String> type = Set.of(CredentialConstants.VERIFIABLE_PRESENTATION_TYPE);

  /** The credentials. */
  private List<Credential> verifiableCredential = List.of();


  @Override
  public JsonObject asJson() {
    return JsonConvert.toJson(this).asJsonObject();
  }


  @JsonProperty("@context")
  @Schema(
      description = "An ordered set of URIs that define the JSON-LD context",
      required = true,
      ref = "#/components/schemas/json.value"
  )
  public JsonValue getContext() {
    return context;
  }


  @Schema(
      description = "The OPTIONAL issuer of this presentation"
  )
  @JsonInclude(Include.NON_NULL)
  @DidUri
  public URI getHolder() {
    return holder;
  }


  @Schema(
      description = "An OPTIONAL URI from which this credential may be retrieved."
  )
  @JsonInclude(Include.NON_NULL)
  public URI getId() {
    return id;
  }


  @Schema(
      description = "The cryptographic proof associated with this."
  )
  public Proof getProof() {
    return proof != null ? new Proof(proof) : null;
  }


  @Schema(
      description = "An unordered set of type definitions which must be URIs or mappable to URIs via the context. "
          + "These define the data type of the credential subjects. The type 'VerifiableCredential' will be automatically added if it is not included.",
      required = true
  )
  public Set<String> getType() {
    return Collections.unmodifiableSet(type);
  }


  @Schema(
      description = "The credentials being presented"
  )
  public List<Credential> getVerifiableCredential() {
    return List.copyOf(verifiableCredential);
  }


  /**
   * Verify that the type is valid. The type must be "VerifiableCredential" or "https://www.w3.org/2018/credentials#VerifiablePresentation"
   *
   * @return true if context is valid
   */
  @AssertTrue(message = "io.setl.chain.cw.data.Presentation.isValidType.message")
  @JsonIgnore
  public boolean isValidType() {
    return type != null && (type.contains(CredentialConstants.VERIFIABLE_PRESENTATION_TYPE) || type.contains(
        "https://www.w3.org/2018/credentials#VerifiablePresentation"));
  }


  /**
   * Set the JSON-LD context. The context is an ordered set of URIs and the first must be the standard credential context.
   *
   * @param newContext the new context.
   */
  public void setContext(JsonValue newContext) {
    checkNotProven();
    context = newContext;
  }


  public void setHolder(URI holder) {
    checkNotProven();
    this.holder = holder;
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
    LinkedHashSet<String> newSet = new LinkedHashSet<>(newType);
    type = newSet;
  }


  public void setVerifiableCredential(List<Credential> verifiableCredential) {
    checkNotProven();
    this.verifiableCredential = List.copyOf(verifiableCredential);
  }

}
