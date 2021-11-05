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

import java.util.HashMap;
import java.util.Map;

import io.setl.verafied.did.DecentralizedIdentifier;
import io.setl.verafied.did.DidId;
import io.setl.verafied.did.DidStore;
import io.setl.verafied.did.DidStoreException;

/**
 * @author Simon Greatrix on 03/11/2021.
 */
public class TestDidStore implements DidStore {

  private final Map<DidId, DidStoreException> errors = new HashMap<>();

  private final Map<DidId, DecentralizedIdentifier> store = new HashMap<>();


  public void add(DecentralizedIdentifier did) {
    store.put(did.getDidId(), did);
  }


  @Override
  public DecentralizedIdentifier fetch(DidId didId) throws DidStoreException {
    DecentralizedIdentifier document = store.get(didId);
    if (document == null) {
      DidStoreException error = errors.get(didId);
      if (error != null) {
        throw error;
      }
    }
    return document;
  }


  public void setError(DidId didId, DidStoreException error) {
    errors.put(didId, error);
  }

}
