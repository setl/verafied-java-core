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

import java.lang.reflect.InvocationTargetException;
import java.net.URI;

import io.setl.verafied.did.DidId;
import io.setl.verafied.did.validate.DidUrl.Has;
import io.setl.verafied.did.validate.DidUrlValidator;

/**
 * Context used by both the verifier and the prover.
 *
 * @author Simon Greatrix on 22/10/2020.
 */
public class SharedContext {

  private Object auxiliary;

  /** The bytes that were to be signed. */
  private byte[] bytesToSign;

  /** The DID ID as extracted from didWithKey. */
  private URI didId;

  /** The DID URL including the key specifier as a fragment. */
  private DidId didWithKey;

  /** The key ID as extracted from didWithKey. */
  private String keyId;


  /**
   * Get auxiliary data on the context. If there is no auxiliary data yet, a no-argument constructor will be called on the class. If that doesn't work for you,
   * you will have to explicitly set the auxiliary data before it is required.
   *
   * @param type the auxiliary type
   * @param <T>  the auxiliary type
   *
   * @return the auxiliary data
   */
  @SuppressWarnings("unchecked")
  public <T> T getAuxiliary(Class<T> type) {
    if (auxiliary != null) {
      return type.cast(auxiliary);
    }
    T aux = null;
    try {
      aux = type.getDeclaredConstructor().newInstance();
    } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      throw new InternalError("Invalid auxiliary class: " + type.getName(), e);
    }
    auxiliary = aux;
    return aux;
  }


  /**
   * Get the "bytes-to-sign" value.
   *
   * @return the bytes
   *
   * @throws IllegalStateException if "bytesToSign" has not yet been set
   * @see #setBytesToSign(byte[])
   */
  public byte[] getBytesToSign() {
    if (bytesToSign == null) {
      throw new IllegalStateException("'bytesToSign' is not set yet.");
    }
    return bytesToSign.clone();
  }


  /**
   * Get the DID ID.
   *
   * @return the ID
   *
   * @throws IllegalStateException if "didId" has not yet been set
   * @see #setDidWithKey(DidId)
   */
  public URI getDidId() {
    if (didId == null) {
      throw new IllegalStateException("'didId' is not set yet.");
    }
    return didId;
  }


  /**
   * Get the DID ID and selected verification method.
   *
   * @return the ID
   *
   * @throws IllegalStateException if "didWithKey" has not yet been set
   * @see #setDidWithKey(DidId)
   */
  public DidId getDidWithKey() {
    if (didWithKey == null) {
      throw new IllegalStateException("'didWithKey' is not set yet.");
    }
    return didWithKey;
  }


  /**
   * Get the key ID value which specifies the DID's verification method.
   *
   * @return the ID
   *
   * @throws IllegalStateException if "keyId" has not yet been set
   * @see #setDidWithKey(DidId)
   */
  public String getKeyId() {
    if (keyId == null) {
      throw new IllegalStateException("'keyId' is not set yet, so the verification method is also unavailable.");
    }
    return keyId;
  }


  /**
   * Set the auxiliary data.
   *
   * @param auxiliary the new data
   */
  public void setAuxiliary(Object auxiliary) {
    this.auxiliary = auxiliary;
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
    this.bytesToSign = bytesToSign.clone();
  }


  /**
   * Set the DID with key fragment URI. This also splits the DID with fragment into its constituent parts so sets "didId" and "keyId".
   *
   * @param id the DID with key fragment
   */
  public void setDidWithKey(DidId id) {
    if (id == null) {
      throw new IllegalArgumentException("DID ID must not be null");
    }
    if (!DidUrlValidator.isValid(id.getUri(), "", Has.EITHER, Has.EITHER, Has.YES)) {
      throw new IllegalArgumentException("DID ID must be valid: " + id.getUri());
    }
    didWithKey = id;
    didId = id.withoutFragment().getUri();
    keyId = id.getFragment();
  }

}
