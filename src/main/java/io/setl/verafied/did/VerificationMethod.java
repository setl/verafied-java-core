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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.setl.verafied.data.jwk.PublicKeyJwk;
import io.setl.verafied.did.validate.DidUri;

/**
 * A verification method as defined in a DID.
 *
 * @author Simon Greatrix on 26/06/2020.
 */
public class VerificationMethod {

  /** The only supported type is JwsVerificationKey2020. */
  private static final String JWS_TYPE = "JwsVerificationKey2020";

  private final List<URI> controller = new ArrayList<>();

  private URI id;

  private PublicKeyJwk publicKeyJwk;

  private String type;


  public VerificationMethod() {
    // do nothing
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof VerificationMethod)) {
      return false;
    }

    VerificationMethod that = (VerificationMethod) o;

    if (!Objects.equals(controller, that.controller)) {
      return false;
    }
    if (!Objects.equals(id, that.id)) {
      return false;
    }
    return Objects.equals(publicKeyJwk, that.publicKeyJwk);
  }


  @JsonProperty("controller")
  @NotEmpty
  public List<@NotNull @DidUri URI> getController() {
    return Collections.unmodifiableList(controller);
  }


  @JsonProperty("id")
  public URI getId() {
    return id;
  }


  public PublicKeyJwk getPublicKeyJwk() {
    return publicKeyJwk;
  }


  @JsonProperty("typ")
  @NotNull
  public String getType() {
    return type;
  }


  @Override
  public int hashCode() {
    return Objects.hash(controller, id, publicKeyJwk);
  }


  @JsonIgnore
  @AssertTrue
  public boolean isTypeJwsVerificationKey2020() {
    return type.equals(JWS_TYPE);
  }


  /**
   * Set the DIDs who control this.
   *
   * @param controller the controllers
   */
  public void setController(List<URI> controller) {
    this.controller.clear();
    if (controller != null) {
      this.controller.addAll(controller);
    }
  }


  public void setId(URI id) {
    this.id = id;
  }


  /**
   * Set the public key. Note: if the ID is not set, then the ID is set to be the same as the JWK.
   *
   * @param publicKeyJwk the public key
   */
  public void setPublicKeyJwk(PublicKeyJwk publicKeyJwk) {
    this.type = JWS_TYPE;
    this.publicKeyJwk = publicKeyJwk;
    if (id == null) {
      id = publicKeyJwk.getKeyId();
    }
  }


  public void setType(String type) {
    this.type = type;
  }

}
