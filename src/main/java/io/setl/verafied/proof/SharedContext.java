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
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.setl.verafied.did.DidId;

/**
 * Context used by both the verifier and the prover.
 *
 * @author Simon Greatrix on 22/10/2020.
 */
public class SharedContext {

  private static final Logger logger = LoggerFactory.getLogger(SharedContext.class);

  /** The bytes that were to be signed. */
  private byte[] bytesToSign;

  /** The DID ID as extracted from didWithKey. */
  private URI didId;

  /** The DID URL including the key specifier as a fragment. */
  private DidId didWithKey;

  /** The key ID as extracted from didWithKey. */
  private String keyId;


  public byte[] getBytesToSign() {
    if (bytesToSign == null) {
      throw new IllegalStateException("'bytesToSign' is not set yet.");
    }
    return bytesToSign;
  }


  public URI getDidId() {
    if (didId == null) {
      throw new IllegalStateException("'didId' is not set yet.");
    }
    return didId;
  }


  public DidId getDidWithKey() {
    if (didWithKey == null) {
      throw new IllegalStateException("'didWithKey' is not set yet.");
    }
    return didWithKey;
  }


  public String getKeyId() {
    if (didId == null) {
      throw new IllegalStateException("'didId' is not set yet, so the verification method is also unavailable.");
    }
    return keyId;
  }


  /**
   * Set the bytes-to-sign.
   *
   * @param bytesToSign That bytes that should be (or were if verifying) signed.
   */
  public void setBytesToSign(byte[] bytesToSign) {
    if (bytesToSign == null) {
      throw new IllegalArgumentException("'bytesToSign' must not be null");
    }
    this.bytesToSign = bytesToSign;
    if (logger.isDebugEnabled()) {
      logger.debug("Bytes to sign={}", Base64.getUrlEncoder().encodeToString(bytesToSign));
    }
  }


  /**
   * Set the DID with key fragment URI. This also splits the DID with fragment into its constituent parts.
   *
   * @param id the DID with key fragment
   */
  public void setDidWithKey(DidId id) {
    if (id == null) {
      throw new IllegalArgumentException("DID ID must not be null");
    }
    didWithKey = id;
    didId = id.withoutFragment().getUri();
    keyId = id.getFragment();
    if (keyId == null) {
      throw new IllegalArgumentException("DID ID must specify a verification method. Got: " + logSafe(id.toString()));
    }
  }

}
