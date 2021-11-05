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
package io.setl.verafied.data.credential;

import java.net.URI;
import java.time.Duration;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Simon Greatrix on 02/11/2021.
 */
@JsonDeserialize
public class TestStatus extends CredentialStatus {

  public static final Duration DEFAULT_RECHECK_AFTER = Duration.ofDays(1);

  private final Duration recheckAfter;


  /**
   * New instance.
   *
   * @param id           credential ID
   * @param type         credential status check type
   * @param recheckAfter duration of a status check result
   */
  public TestStatus(
      @JsonProperty(value = "id") URI id,
      @JsonProperty(value = "type") String type,
      @JsonProperty(value = "recheckAfter") Duration recheckAfter
  ) {
    super(id, type);
    this.recheckAfter = recheckAfter != null ? recheckAfter : DEFAULT_RECHECK_AFTER;
  }


  @JsonFormat(shape = Shape.STRING)
  public Duration getRecheckAfter() {
    return recheckAfter;
  }

}
