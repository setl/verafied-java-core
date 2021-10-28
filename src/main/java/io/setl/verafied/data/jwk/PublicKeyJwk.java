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

import java.net.URI;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Objects;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;

import io.setl.verafied.CredentialConstants;
import io.setl.verafied.did.validate.DidUrl;
import io.setl.verafied.did.validate.DidUrl.Has;

/**
 * Representation of a JSON Web Key.
 *
 * @author Simon Greatrix on 27/06/2020.
 */
@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "kty")
@JsonSubTypes({
    @Type(value = PublicKeyJwkEc.class, name = "ec"),
    @Type(value = PublicKeyJwkOkp.class, name = "okp"),
    @Type(value = PublicKeyJwkRsa.class, name = "rsa")
})
public abstract class PublicKeyJwk {

  static {
    // Ensure BouncyCastle is loaded
    CredentialConstants.initialise();
  }

  /** The key ID. */
  private URI keyId;

  /** The expected key use. Typically, "sig" for signing. */
  private String use = "sig";


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof PublicKeyJwk)) {
      return false;
    }

    PublicKeyJwk that = (PublicKeyJwk) o;
    return Objects.equals(keyId, that.keyId) && Objects.equals(use, that.use);
  }


  @JsonProperty("kid")
  @NotNull
  @DidUrl(hasFragment = Has.YES)
  public URI getKeyId() {
    return keyId;
  }


  @JsonIgnore
  @Hidden
  @NotNull
  public abstract KeyType getKeyType();


  @NotNull
  @JsonProperty("kty")
  @Schema(name = "kty", required = true)
  public String getKeyTypeName() {
    return getKeyType().id();
  }


  @JsonIgnore
  public abstract PublicKey getPublicKey() throws InvalidKeySpecException;


  @JsonProperty(value = "use")
  public String getUse() {
    return use;
  }


  @Override
  public int hashCode() {
    int result = keyId != null ? keyId.hashCode() : 0;
    result = 31 * result + (use != null ? use.hashCode() : 0);
    return result;
  }


  public void setKeyId(URI keyId) {
    this.keyId = keyId;
  }


  public void setUse(String use) {
    this.use = use;
  }

}
