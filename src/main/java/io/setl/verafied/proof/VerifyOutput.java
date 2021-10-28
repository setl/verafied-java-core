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

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The result of a cryptographic verification.
 *
 * @author Simon Greatrix on 10/07/2020.
 */
@Schema(
    description = "The result of a cryptographic verification."
)
public class VerifyOutput implements Serializable {

  public static final VerifyOutput OK_CREDENTIAL = new VerifyOutput(true, null, VerifyType.CREDENTIAL);

  public static final VerifyOutput OK_JSON = new VerifyOutput(true, null, VerifyType.SIGNED_JSON);

  public static final VerifyOutput OK_PRESENTATION = new VerifyOutput(true, null, VerifyType.PRESENTATION);


  public static VerifyOutput fail(String detail, VerifyType verifyType) {
    return new VerifyOutput(false, detail, verifyType);
  }


  @Schema(
      description = "An optional detail message which should indicate the reason for any verification failure."
  )
  private final String detail;

  @Schema(
      description = "If true, the document verified."
  )
  private final boolean isOk;

  @Schema(
      description = "The kind of verification that was performed."
  )
  private final VerifyType verifyType;


  /**
   * New instance.
   *
   * @param isOk       did the input verify?
   * @param detail     the detail of why verification failed, if it did
   * @param verifyType the verification type performed.
   */
  public VerifyOutput(
      @JsonProperty("ok") boolean isOk,
      @JsonProperty("detail") String detail,
      @JsonProperty("verifyType") VerifyType verifyType
  ) {
    this.isOk = isOk;
    this.detail = detail;
    this.verifyType = verifyType;
  }


  @JsonInclude(Include.NON_EMPTY)
  public String getDetail() {
    return detail;
  }


  public VerifyType getVerifyType() {
    return verifyType;
  }


  public boolean isOk() {
    return isOk;
  }


}
