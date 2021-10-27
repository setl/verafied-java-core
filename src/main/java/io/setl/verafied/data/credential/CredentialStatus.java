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

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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
public class CredentialStatus {

  private final URI id;

  private final String type;


  /**
   * New instance.
   *
   * @param id           credential ID
   * @param type         credential status check type
   */
  public CredentialStatus(
      @JsonProperty(value = "id", required = true) URI id,
      @JsonProperty(value = "type", required = true) String type
  ) {
    this.id = id;
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

}
