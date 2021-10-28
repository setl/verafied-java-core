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

package io.setl.verafied.data;

import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.TreeMap;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.v3.oas.annotations.media.Schema;

import io.setl.verafied.did.validate.DidUrl;
import io.setl.verafied.did.validate.DidUrl.Has;

/**
 * A representation of a cryptographic proof.
 *
 * @author Simon Greatrix on 03/10/2020.
 */
@Schema(
    description = "The representation of a cryptographic proof."
)
public class Proof {

  @Schema(
      hidden = true
  )
  private final Map<String, Object> properties = new TreeMap<>();

  @Schema(
      description = "The time the proof was generated."
  )
  @JsonFormat(shape = Shape.STRING)
  @JsonInclude(Include.NON_NULL)
  private Instant created = Instant.now().truncatedTo(ChronoUnit.SECONDS);

  @Schema(
      description = "The identifier for the type of cryptographic proof."
  )
  private String type;

  @Schema(
      description = "The Decentralized Identifier's verification method to use to verify this proof."
  )
  @JsonInclude(Include.NON_NULL)
  private URI verificationMethod;


  /**
   * Get a value from this proof's properties, if it is of the correct type.
   *
   * @param type the required type
   * @param key  the property name
   * @param <T>  the required type
   *
   * @return the value, or null if missing or wrong type
   */
  public <T> T get(Class<? extends T> type, String key) {
    Object o = properties.get(key);
    // doesn't exist
    if (o == null) {
      return null;
    }

    // value exists, is it correct type?
    if (type.isInstance(o)) {
      return type.cast(o);
    }

    // exists but wrong type
    return null;
  }


  public Instant getCreated() {
    return created;
  }


  @JsonAnyGetter
  public Map<String, Object> getProperties() {
    return properties;
  }


  @NotEmpty
  public String getType() {
    return type;
  }


  @DidUrl(hasFragment = Has.YES)
  public URI getVerificationMethod() {
    return verificationMethod;
  }


  public void remove(String key) {
    properties.remove(key);
  }


  @JsonAnySetter
  public void set(String key, Object value) {
    properties.put(key, value);
  }


  public void setCreated(Instant created) {
    this.created = created;
  }


  public void setType(String type) {
    this.type = type;
  }


  public void setVerificationMethod(URI verificationMethod) {
    this.verificationMethod = verificationMethod;
  }

}
