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

import static io.setl.verafied.CredentialConstants.logSafe;

import java.net.URI;
import java.security.InvalidKeyException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Objects;

import io.setl.verafied.data.Proof;
import io.setl.verafied.data.jwk.SigningAlgorithm;
import io.setl.verafied.did.DecentralizedIdentifier;
import io.setl.verafied.did.DidId;
import io.setl.verafied.did.DidStore;
import io.setl.verafied.did.DidStoreException;
import io.setl.verafied.did.VerificationMethod;

/**
 * Common information for document verification.
 *
 * @author Simon Greatrix on 15/10/2020.
 */
public class VerifyContext extends SharedContext {

  private SigningAlgorithm algorithm;

  private byte[] allegedSignature;

  private final DidStore didStore;

  private VerificationMethod verificationMethod;


  public VerifyContext(DidStore didStore) {
    this.didStore = didStore;
  }


  /**
   * Identify the verification method required by a specific signed document.
   *
   * @param proof the signed document we are trying to verify
   *
   * @return the identified method
   */
  public VerificationMethod findVerificationMethod(Proof proof) throws DidStoreException, VerifyOutputException {
    // The proof should specify a verification method which is known to us.
    URI method = proof.getVerificationMethod();
    if (method == null) {
      throw new VerifyOutputException("Proof does not contain a 'verificationMethod'", VerifyType.SIGNED_JSON);
    }
    if (!"did".equals(method.getScheme())) {
      throw new VerifyOutputException("Specified 'verificationMethod' is not a 'did:' URI", VerifyType.SIGNED_JSON);
    }

    // Extract the DID id and Key ID from the DID URI
    DidId didId = new DidId(method);
    setDidWithKey(didId);

    // fetch the DID from the store
    DecentralizedIdentifier did = getDidStore().fetch(didId);

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
    throw new VerifyOutputException("No such verification method in specified DID: " + logSafe(String.valueOf(getDidWithKey())), VerifyType.SIGNED_JSON);
  }


  public SigningAlgorithm getAlgorithm() {
    if( algorithm==null ) {
      throw new IllegalStateException("Signing algorithm has not been set.");
    }
    return algorithm;
  }


  public byte[] getAllegedSignature() {
    if(allegedSignature==null ) {
      throw new IllegalStateException("Alleged signature has not been set yet.");
    }
    return allegedSignature;
  }


  public DidStore getDidStore() {
    if(didStore==null) {
      throw new IllegalStateException("DID store has not been set yet.");
    }
    return didStore;
  }


  public VerificationMethod getVerificationMethod() {
    if( verificationMethod==null ) {
      throw new IllegalStateException("Verification method has not been set yet.");
    }
    return verificationMethod;
  }


  public void setAlgorithm(SigningAlgorithm inputAlg) {
    algorithm = inputAlg;
  }


  public void setAllegedSignature(byte[] allegedSignature) {
    this.allegedSignature = allegedSignature;
  }


  /**
   * Perform verification, checking the signature.
   *
   * @return the results of the verification
   */
  public VerifyOutput verify() throws InvalidKeySpecException {
    try {
      Signature signature = getAlgorithm().createSignature();
      signature.initVerify(getVerificationMethod().getPublicKeyJwk().getPublicKey());
      signature.update(getBytesToSign());
      return signature.verify(getAllegedSignature()) ? VerifyOutput.OK_JSON : VerifyOutput.fail("Incorrect signature", VerifyType.SIGNED_JSON);
    } catch (InvalidKeyException e) {
      return VerifyOutput.fail("Declared JWS Signature algorithm does not match the declared verification method", VerifyType.SIGNED_JSON);
    } catch (SignatureException e) {
      return VerifyOutput.fail("Invalid signature", VerifyType.SIGNED_JSON);
    }
  }

}
