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

/**
 * A mechanism for retrieving Decentralized Identifier documents from some storage.
 *
 * @author Simon Greatrix on 23/10/2021.
 */
public interface DidStore {

  /**
   * Fetch a Decentralized Identifier document from storage. If the document cannot be found, return null.
   *
   * @param didId the document's ID
   *
   * @return the document, or null
   *
   * @throws DidStoreException if the storage mechanism fails
   */
  DecentralizedIdentifier fetch(DidId didId) throws DidStoreException;

}
