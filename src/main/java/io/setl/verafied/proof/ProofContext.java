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
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Objects;

import io.setl.verafied.CredentialConstants;
import io.setl.verafied.data.TypedKeyPair;
import io.setl.verafied.data.jwk.SigningAlgorithm;

/**
 * Context for attaching a proof to a document.
 *
 * @author Simon Greatrix on 02/10/2020.
 */
public class ProofContext extends SharedContext {

  /** The proof generator. */
  private final Prover prover;

  /** The signature. */
  private byte[] signatureValue;


  public ProofContext(Prover prover) {
    this.prover = Objects.requireNonNull(prover);
  }


  public Prover getProver() {
    return prover;
  }


  /**
   * Get the signature bytes created by signing.
   *
   * @return the signature bytes
   */
  public byte[] getSignatureValue() {
    if (signatureValue == null) {
      throw new IllegalStateException("Signature value is not available. 'sign' method must be invoked first.");
    }
    return signatureValue.clone();
  }


  /**
   * Sign the document.
   */
  public void sign(TypedKeyPair typedKey) throws GeneralSecurityException {
    PrivateKey privateKey = typedKey.getPrivateKey();
    SigningAlgorithm algorithm = typedKey.getAlgorithm();
    Signature signature = algorithm.createSignature();
    signature.initSign(privateKey, CredentialConstants.getSecureRandom());
    signature.update(getBytesToSign());
    signatureValue = signature.sign();
  }

}
