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

import static io.setl.verafied.UnacceptableDocumentException.mapOf;

import java.net.URI;
import java.security.InvalidKeyException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Objects;

import io.setl.verafied.UnacceptableDocumentException;
import io.setl.verafied.data.Proof;
import io.setl.verafied.data.jwk.SigningAlgorithm;
import io.setl.verafied.did.DecentralizedIdentifier;
import io.setl.verafied.did.DidId;
import io.setl.verafied.did.DidStore;
import io.setl.verafied.did.DidStoreException;
import io.setl.verafied.did.VerificationMethod;
import io.setl.verafied.did.validate.DidUrl.Has;
import io.setl.verafied.did.validate.DidUrlValidator;

/**
 * Common information for document verification.
 *
 * @author Simon Greatrix on 15/10/2020.
 */
public class VerifyContext extends SharedContext {

  private final DidStore didStore;

  private SigningAlgorithm algorithm;

  private byte[] allegedSignature;

  private VerificationMethod verificationMethod;


  public VerifyContext(DidStore didStore) {
    this.didStore = Objects.requireNonNull(didStore);
  }


  /**
   * Identify the verification method required by a specific signed document.
   *
   * @param proof the signed document we are trying to verify
   *
   * @return the identified method
   */
  public VerificationMethod findVerificationMethod(Proof proof) throws DidStoreException, UnacceptableDocumentException {
    // The proof should specify a verification method which is known to us.
    URI method = proof.getVerificationMethod();
    if (method == null) {
      throw new UnacceptableDocumentException("proof_no_verification_method", "Proof does not contain a 'verificationMethod'");
    }
    if (!DidUrlValidator.isValid(method, "", Has.EITHER, Has.EITHER, Has.YES)) {
      throw new UnacceptableDocumentException("proof_verification_method_not_did", "Specified 'verificationMethod' is not a valid 'did:' URI",
          mapOf("verificationMethod", method)
      );
    }

    // Extract the DID id and Key ID from the DID URI
    DidId didId = new DidId(method);
    setDidWithKey(didId);

    // fetch the DID from the store
    DecentralizedIdentifier did = getDidStore().fetch(didId.withoutFragment());
    if (did == null) {
      throw new UnacceptableDocumentException("proof_did_unknown", "DID associated with the document is not available", mapOf("did", didId));
    }

    // Look for the key in the DID
    List<VerificationMethod> methods = did.getVerificationMethod();
    verificationMethod = null;
    for (VerificationMethod vm : methods) {
      if (Objects.equals(vm.getId(), method)) {
        verificationMethod = vm;
        return vm;
      }
    }

    // not matched
    throw new UnacceptableDocumentException("proof_verification_method_not_matched", "No such verification method in specified DID",
        mapOf("verificationMethod", method)
    );
  }


  /**
   * Get the signing algorithm used by the proof.
   *
   * @return the signing algorithm
   *
   * @throws IllegalStateException if the signing algorithm has not been set yet
   * @see #setAlgorithm(SigningAlgorithm)
   */
  public SigningAlgorithm getAlgorithm() {
    if (algorithm == null) {
      throw new IllegalStateException("Signing algorithm has not been set.");
    }
    return algorithm;
  }


  /**
   * Get the bytes of the alleged signature that is attached to the document.
   *
   * @return the bytes of the alleged signature
   *
   * @throws IllegalStateException if the alleged signature has not been set yet
   * @see #setAllegedSignature(byte[])
   */
  public byte[] getAllegedSignature() {
    if (allegedSignature == null) {
      throw new IllegalStateException("Alleged signature has not been set yet.");
    }
    return allegedSignature.clone();
  }


  /**
   * Get the DID Store from which Decentralized Identity Documents can be retrieved.
   *
   * @return DID store
   */
  public DidStore getDidStore() {
    return didStore;
  }


  /**
   * Get the verification method used to check the signature.
   *
   * @return the verification method
   *
   * @throws IllegalStateException if the verification method has not been set
   * @see #findVerificationMethod(Proof)
   */
  public VerificationMethod getVerificationMethod() {
    if (verificationMethod == null) {
      throw new IllegalStateException("Verification method has not been set yet.");
    }
    return verificationMethod.copy();
  }


  public void setAlgorithm(SigningAlgorithm inputAlg) {
    algorithm = inputAlg;
  }


  public void setAllegedSignature(byte[] allegedSignature) {
    this.allegedSignature = allegedSignature.clone();
  }


  /**
   * Perform verification, checking the signature.
   *
   * @throws InvalidKeySpecException       if the key in the DID is invalid
   * @throws UnacceptableDocumentException if the signature is invalid
   */
  public void verify() throws InvalidKeySpecException, UnacceptableDocumentException {
    try {
      Signature signature = getAlgorithm().createSignature();
      signature.initVerify(getVerificationMethod().getPublicKeyJwk().getPublicKey());
      signature.update(getBytesToSign());
      if (!signature.verify(getAllegedSignature())) {
        throw new UnacceptableDocumentException("proof_incorrect_signature", "Incorrect signature");
      }
    } catch (InvalidKeyException e) {
      throw new UnacceptableDocumentException(
          "proof_wrong_signature_method",
          "Declared JWS Signature algorithm does not match the declared verification method",
          mapOf("errorMessage", e.toString()), e
      );
    } catch (SignatureException e) {
      throw new UnacceptableDocumentException("proof_invalid_signature", "Invalid signature",
          mapOf("errorMessage", e.toString()), e
      );
    }
  }

}
