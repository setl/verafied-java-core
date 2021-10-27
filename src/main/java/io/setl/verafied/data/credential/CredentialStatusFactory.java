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
package io.setl.verafied.data.credential;

/**
 * A creator of the "credentialStatus" entries for credentials. This field indicates how one may test if a credential has been revoked.
 *
 * @author Simon Greatrix on 27/10/2021.
 */
public interface CredentialStatusFactory {

  /**
   * Set a "credentialStatus" value on the supplied credential. Additional modifications may be made to the credential to facilitate the status check. This
   * includes setting an expiration date, or an ID.
   */
  void setStatus(Credential credential);

}
