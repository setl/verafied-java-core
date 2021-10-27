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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author Simon Greatrix on 27/06/2020.
 */
public enum KeyType {
  /** Octet Key Pair - used for Ed25519. See RFC 8037 */
  OKP,

  /** Elliptic Curve. See RFC 7517. */
  EC,

  /** Rivest-Shamir-Adelman key. See RFC 7517. */
  RSA;


  @JsonCreator
  public static KeyType fromId(String id) {
    return KeyType.valueOf(id.toUpperCase());
  }


  @JsonValue
  public String id() {
    return name().toLowerCase();
  }
}
