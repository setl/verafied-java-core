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

package io.setl.verafied.data;

import java.security.PrivateKey;

import io.setl.verafied.data.jwk.SigningAlgorithm;

/**
 * Combination of a private key and its JWK signing algorithm.
 *
 * @author Simon Greatrix on 23/10/2021.
 */
public class TypedKeyPair {

  private final SigningAlgorithm algorithm;

  private final PrivateKey privateKey;


  /**
   * New instance.
   *
   * @param algorithm  the used to sign with the private key.
   * @param privateKey the private key
   */
  public TypedKeyPair(SigningAlgorithm algorithm, PrivateKey privateKey) {
    this.algorithm = algorithm;
    this.privateKey = privateKey;
  }


  public SigningAlgorithm getAlgorithm() {
    return algorithm;
  }


  public PrivateKey getPrivateKey() {
    return privateKey;
  }

}
