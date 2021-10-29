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

package io.setl.verafied;

import java.util.Map;

/**
 * A checked exception thrown when validating a document. The exception message will indicate why the document failed validation.
 *
 * @author Simon Greatrix on 29/10/2021.
 */
public class UnacceptableDocumentException extends Exception {

  private final String code;

  private final transient Map<String, Object> parameters;


  /**
   * New instance.
   *
   * @param code       the error code.
   * @param message    a sample message
   * @param parameters parameters associated with this exception
   */
  public UnacceptableDocumentException(String code, String message, Map<String, Object> parameters) {
    super(message);
    this.code = code;
    this.parameters = Map.copyOf(parameters);
  }


  /**
   * New instance with no parameters.
   *
   * @param code    the error code.
   * @param message a sample message
   */
  public UnacceptableDocumentException(String code, String message) {
    this(code, message, Map.of());
  }


  /**
   * Get the specific error code.
   *
   * @return the error code
   */
  public String getCode() {
    return code;
  }


  /**
   * Get the parameters associated with this error type.
   *
   * @return the parameters
   */
  public Map<String, Object> getParameters() {
    return Map.copyOf(parameters);
  }

}
