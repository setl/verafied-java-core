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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import io.setl.verafied.UnacceptableDocumentException;

/**
 * The result of a cryptographic verification.
 *
 * @author Simon Greatrix on 10/07/2020.
 */
@Schema(
    description = "The result of a cryptographic verification."
)
public class VerifyOutput {

  public static final VerifyOutput OK = new VerifyOutput();

  private static final long serialVersionUID = 1L;


  public static VerifyOutput fail(
      String code,
      String message,
      Map<String, Object> parameters
  ) {
    return new VerifyOutput(code, message, parameters);
  }


  @Schema(
      description = "The error code, if the document failed to verify"
  )
  private final String code;

  @Schema(
      description = "If true, the document verified."
  )
  private final boolean isOk;

  @Schema(
      description = "An optional detail message which should indicate the reason for any verification failure."
  )
  private final String message;

  @Schema(
      description = "Parameters related to the error code, if the document failed to verify."
  )
  private final Map<String, Object> parameters;


  /**
   * Transform an unacceptable document exception into a verify output instance.
   *
   * @param unacceptable the exception
   */
  public VerifyOutput(UnacceptableDocumentException unacceptable) {
    isOk = false;
    code = unacceptable.getCode();
    message = unacceptable.getMessage();
    parameters = unacceptable.getParameters();
  }


  /**
   * New instance.
   *
   * @param isOk       did the input verify?
   * @param code       the error code indication why verification failed, if it did
   * @param message    the detail of why verification failed, if it did
   * @param parameters parameters associated with the verification failure, if it failed
   */
  @JsonCreator
  public VerifyOutput(
      @JsonProperty(value = "ok", required = true) boolean isOk,
      @JsonProperty("code") String code,
      @JsonProperty("message") String message,
      @JsonProperty("parameters") Map<String, Object> parameters
  ) {
    this.isOk = isOk;
    this.message = message;
    this.code = code;
    this.parameters = (parameters != null) ? Map.copyOf(parameters) : Map.of();
  }


  /**
   * New instance for a failed verification.
   *
   * @param message the detail of why verification failed, if it did
   */
  public VerifyOutput(
      String code,
      String message,
      Map<String, Object> parameters
  ) {
    isOk = false;
    this.code = Objects.requireNonNull(code);
    this.message = Objects.requireNonNull(message);
    if (parameters == null) {
      this.parameters = Map.of();
    } else {
      HashMap<String, Object> map = new HashMap<>(parameters);
      this.parameters = Collections.unmodifiableMap(map);
    }
  }


  /**
   * New instance for a successful verification.
   */
  public VerifyOutput() {
    isOk = true;
    code = null;
    message = null;
    parameters = Map.of();
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof VerifyOutput)) {
      return false;
    }
    VerifyOutput that = (VerifyOutput) o;
    return isOk == that.isOk && Objects.equals(code, that.code) && Objects.equals(message, that.message)
        && parameters.equals(that.parameters);
  }


  @JsonInclude(Include.NON_EMPTY)
  public String getCode() {
    return code;
  }


  @JsonInclude(Include.NON_EMPTY)
  public String getMessage() {
    return message;
  }


  @JsonInclude(Include.NON_EMPTY)
  public Map<String, Object> getParameters() {
    return Map.copyOf(parameters);
  }


  @Override
  public int hashCode() {
    return Objects.hash(code, isOk, message, parameters);
  }


  public boolean isOk() {
    return isOk;
  }


  /**
   * If this result indicates a failure, transform the failure into an exception and throw it.
   *
   * @throws UnacceptableDocumentException if this result indicated a failure
   */
  public void throwIfFailed() throws UnacceptableDocumentException {
    if (!isOk) {
      throw new UnacceptableDocumentException(code, message, parameters);
    }
  }

}
