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

package io.setl.verafied.data.presentation;

import java.net.URI;
import java.util.List;
import java.util.Set;
import javax.json.JsonValue;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import io.setl.verafied.CredentialConstants;
import io.setl.verafied.data.Proof;
import io.setl.verafied.data.credential.Credential;

/**
 * Builder for presentation instances.
 *
 * @author Simon Greatrix on 04/11/2021.
 */
@SuppressFBWarnings("EI_EXPOSE_REP2") // The Presentation class creates defensive copies, so no need to do it here
public final class PresentationBuilder {

  private JsonValue context = Presentation.DEFAULT_CONTEXT;

  private URI holder;

  private URI id;

  private Proof proof;

  private Set<String> type = Set.of(CredentialConstants.VERIFIABLE_PRESENTATION_TYPE);

  private List<Credential> verifiableCredential = List.of();


  /**
   * Build the presentation.
   *
   * @return the presentation
   */
  public Presentation build() {
    Presentation presentation = new Presentation();
    presentation.setContext(context);
    presentation.setHolder(holder);
    presentation.setId(id);
    presentation.setType(type);
    presentation.setVerifiableCredential(verifiableCredential);

    // Set proof last
    presentation.setProof(proof);

    return presentation;
  }


  @JsonProperty("@context")
  public PresentationBuilder withContext(JsonValue context) {
    this.context = context;
    return this;
  }


  public PresentationBuilder withHolder(URI holder) {
    this.holder = holder;
    return this;
  }


  public PresentationBuilder withId(URI id) {
    this.id = id;
    return this;
  }


  public PresentationBuilder withProof(Proof proof) {
    this.proof = proof;
    return this;
  }


  public PresentationBuilder withType(Set<String> type) {
    this.type = type;
    return this;
  }


  public PresentationBuilder withVerifiableCredential(List<Credential> verifiableCredential) {
    this.verifiableCredential = verifiableCredential;
    return this;
  }

}
