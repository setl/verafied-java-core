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

import static io.setl.verafied.CredentialConstants.logSafe;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumeration of the types of credentials which a verification key can sign.
 *
 * @author Simon Greatrix on 02/07/2020.
 */
public enum KeyUsage {
  ASSERTION("Assertion"),
  AUTHENTICATION("Authentication"),
  CAPABILITY_DELEGATION("CapabilityDelegation"),
  CAPABILITY_INVOCATION("CapabilityInvocation");


  /**
   * Get the KeyUsage instance by its ID.
   *
   * @param id the ID
   *
   * @return the instance
   *
   * @throws IllegalArgumentException if the ID is not recognised
   */
  @JsonCreator
  public static KeyUsage forId(String id) {
    for (KeyUsage keyUsage : KeyUsage.values()) {
      if (keyUsage.getId().equals(id)) {
        return keyUsage;
      }
    }
    throw new IllegalArgumentException("Unknown key usage: " + logSafe(id));
  }


  private final String id;


  KeyUsage(String id) {
    this.id = id;
  }


  @JsonValue
  public String getId() {
    return id;
  }
}
