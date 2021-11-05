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

import javax.json.JsonObject;
import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An interface that indicates the object can have a standard proof attached to it.
 *
 * @author Simon Greatrix on 27/10/2020.
 */
public interface Provable {

  /**
   * Convert this to an JSON representation. This JSON is what will be signed or verified.
   *
   * @return the JSON representation of this.
   */
  JsonObject asJson();


  /**
   * A utility method to call within setters when this should be immutable if a proof is attached. If a proof is non-null, then this method will throw an
   * <code>IllegalStateException</code>
   *
   * @throws IllegalStateException if this has a non-null proof
   */
  default void checkNotProven() {
    if (getProof() != null) {
      throw new IllegalStateException("Cannot change data when a proof is attached");
    }
  }


  /**
   * Get the "proof" element of this, if it has been set.
   *
   * @return the proof, or null
   */
  @JsonProperty("proof")
  @JsonInclude(Include.NON_NULL)
  @Valid
  Proof getProof();


  /**
   * Set the "proof" element of this which contains the signature. It is recommended that an object with a non-null proof be immutable.
   *
   * @param proof the proof element
   */
  void setProof(Proof proof);

}
