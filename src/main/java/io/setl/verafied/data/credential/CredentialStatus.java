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
import java.net.URISyntaxException;
import java.util.UUID;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * The specification for how the status of a credential can be checked.
 *
 * @author Simon Greatrix on 27/07/2020.
 */
@JsonDeserialize(using = StatusDeserializer.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class CredentialStatus {

  /**
   * A URI that will be assigned to an instance if no ID is specified. This uses the "example" schema, which is guaranteed not to be used by a proper ID,
   * according to RFC-7595.
   */
  public static final URI UNSET_URI = URI.create("example:unset.status.id");

  private final URI id;

  private final String type;


  /**
   * New instance. A credential status must have an ID, but it may be desirable to only set the type and allow a processor to derive an ID.
   *
   * @param id   credential status ID. If null, a unique one will be generated using the UNSET_URI as a template.
   * @param type credential status check type
   */
  protected CredentialStatus(
      @JsonProperty(value = "id") URI id,
      @JsonProperty(value = "type", required = true) String type
  ) {
    URI myId;
    try {
      myId = id != null ? id : new URI(UNSET_URI.getScheme(), UNSET_URI.getSchemeSpecificPart(), UUID.randomUUID().toString());
    } catch (URISyntaxException e) {
      // Not reachable
      myId = UNSET_URI;
    }
    this.id = myId;
    this.type = type;
  }


  @NotNull
  public URI getId() {
    return id;
  }


  @NotEmpty
  public String getType() {
    return type;
  }


  /**
   * Test if this status was created with an unset ID. This tests if the ID's scheme matches that used by the UNSET_URI value.
   */
  @JsonIgnore
  public boolean isUnsetId() {
    return getId().getScheme().equals(UNSET_URI.getScheme());
  }

}
