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

package io.setl.verafied.revocation;

import java.net.URI;

/**
 * An object that can tell if a credential has been revoked.
 *
 * @author Simon Greatrix on 28/07/2020.
 */
public interface RevocationChecker {

  /**
   * Test if a credential has been revoked. If the credential status type does not match a type handled by this store, then the implementer may choose what to
   * return.
   *
   * @param type   the credential status type.
   * @param issuer the token for the issuer of the credential that may have been revoked
   * @param id     the token for the ID of the credential that may have been revoked.
   *
   * @return true if the credential is revoked.
   */
  boolean test(String type, URI issuer, URI id);

}
