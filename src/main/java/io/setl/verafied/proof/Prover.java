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

package io.setl.verafied.proof;

import java.security.GeneralSecurityException;
import javax.json.JsonObject;

import io.setl.verafied.data.Proof;
import io.setl.verafied.data.Provable;
import io.setl.verafied.data.TypedKeyPair;
import io.setl.verafied.did.DidStoreException;

/**
 * Instance for something that can attach proofs to documents and verify such proofs.
 *
 * @author Simon Greatrix on 03/10/2020.
 */
public interface Prover {

  void attachProof(ProofContext context, Provable input, TypedKeyPair keyPair) throws GeneralSecurityException;


  VerifyOutput verifyProof(VerifyContext context, JsonObject input, Proof proof)
      throws GeneralSecurityException, DidStoreException;

}
