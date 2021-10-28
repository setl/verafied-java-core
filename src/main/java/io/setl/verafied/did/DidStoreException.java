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
 * Indicate a failure to retrieve a Decentralized Identity Document from storage.
 *
 * @author Simon Greatrix on 23/10/2021.
 */
public class DidStoreException extends Exception {

  public DidStoreException() {
  }


  public DidStoreException(String message) {
    super(message);
  }


  public DidStoreException(String message, Throwable cause) {
    super(message, cause);
  }


  public DidStoreException(Throwable cause) {
    super(cause);
  }

}
