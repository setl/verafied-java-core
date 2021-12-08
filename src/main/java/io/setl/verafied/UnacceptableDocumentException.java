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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A checked exception thrown when validating a document. The exception message will indicate why the document failed validation.
 *
 * @author Simon Greatrix on 29/10/2021.
 */
public class UnacceptableDocumentException extends Exception {

  private static Map<String, Object> make(Object... kvs) {
    HashMap<String, Object> map = new HashMap<>();
    for (int i = 0; i < kvs.length; i += 2) {
      map.put((String) kvs[i], kvs[i + 1]);
    }
    return Collections.unmodifiableMap(map);
  }


  /**
   * Create an empty map.
   *
   * @return an empty map
   */
  public static Map<String, Object> mapOf() {
    return Map.of();
  }


  /** Create a map from the specified keys and values. Unlink {@link java.util.Map#of(Object, Object)} the values can be null. */
  public static Map<String, Object> mapOf(String k1, Object v1) {
    return make(k1, v1);
  }


  /** Create a map from the specified keys and values. Unlink {@link java.util.Map#of(Object, Object)} the values can be null. */
  public static Map<String, Object> mapOf(String k1, Object v1, String k2, Object v2) {
    return make(k1, v1, k2, v2);
  }


  /** Create a map from the specified keys and values. Unlink {@link java.util.Map#of(Object, Object)} the values can be null. */
  public static Map<String, Object> mapOf(String k1, Object v1, String k2, Object v2, String k3, Object v3) {
    return make(k1, v1, k2, v2, k3, v3);
  }


  /** Create a map from the specified keys and values. Unlink {@link java.util.Map#of(Object, Object)} the values can be null. */
  public static Map<String, Object> mapOf(String k1, Object v1, String k2, Object v2, String k3, Object v3, String k4, Object v4) {
    return make(k1, v1, k2, v2, k3, v3, k4, v4);
  }


  /** Create a map from the specified keys and values. Unlink {@link java.util.Map#of(Object, Object)} the values can be null. */
  public static Map<String, Object> mapOf(String k1, Object v1, String k2, Object v2, String k3, Object v3, String k4, Object v4, String k5, Object v5) {
    return make(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5);
  }


  /** Create a map from the specified keys and values. Unlink {@link java.util.Map#of(Object, Object)} the values can be null. */
  public static Map<String, Object> mapOf(
      String k1, Object v1,
      String k2, Object v2,
      String k3, Object v3,
      String k4, Object v4,
      String k5, Object v5,
      String k6, Object v6
  ) {
    return make(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6);
  }


  /** Create a map from the specified keys and values. Unlink {@link java.util.Map#of(Object, Object)} the values can be null. */
  public static Map<String, Object> mapOf(
      String k1, Object v1,
      String k2, Object v2,
      String k3, Object v3,
      String k4, Object v4,
      String k5, Object v5,
      String k6, Object v6,
      String k7, Object v7
  ) {
    return make(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7);
  }


  /** Create a map from the specified keys and values. Unlink {@link java.util.Map#of(Object, Object)} the values can be null. */
  public static Map<String, Object> mapOf(
      String k1, Object v1,
      String k2, Object v2,
      String k3, Object v3,
      String k4, Object v4,
      String k5, Object v5,
      String k6, Object v6,
      String k7, Object v7,
      String k8, Object v8
  ) {
    return make(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8);
  }


  /** Create a map from the specified keys and values. Unlink {@link java.util.Map#of(Object, Object)} the values can be null. */
  public static Map<String, Object> mapOf(
      String k1, Object v1,
      String k2, Object v2,
      String k3, Object v3,
      String k4, Object v4,
      String k5, Object v5,
      String k6, Object v6,
      String k7, Object v7,
      String k8, Object v8,
      String k9, Object v9
  ) {
    return make(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8, k9, v9);
  }


  /** Create a map from the specified keys and values. Unlink {@link java.util.Map#of(Object, Object)} the values can be null. */
  public static Map<String, Object> mapOf(
      String k1, Object v1,
      String k2, Object v2,
      String k3, Object v3,
      String k4, Object v4,
      String k5, Object v5,
      String k6, Object v6,
      String k7, Object v7,
      String k8, Object v8,
      String k9, Object v9,
      String k10, Object v10
  ) {
    return make(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8, k9, v9, k10, v10);
  }


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
    this(code, message, parameters, null);
  }


  /**
   * New instance.
   *
   * @param code       the error code.
   * @param message    a sample message
   * @param parameters parameters associated with this exception
   */
  public UnacceptableDocumentException(String code, String message, Map<String, Object> parameters, Throwable cause) {
    super(message, cause);
    this.code = code;
    if (parameters == null || parameters.isEmpty()) {
      this.parameters = Map.of();
    } else {
      Map<String, Object> map = new HashMap<>(parameters);
      this.parameters = Collections.unmodifiableMap(map);
    }
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
