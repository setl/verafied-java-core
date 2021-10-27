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

package io.setl.verafied.did.validate;

import java.net.URI;
import java.util.Objects;
import java.util.regex.Pattern;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Enforces the DID URI specification, which is:
 *
 * <pre>
 * did                = "did:" method-name ":" method-specific-id
 * method-name        = 1*method-char
 * method-char        = %x61-7A / DIGIT
 * method-specific-id = *( *idchar ":" ) 1*idchar
 * idchar             = ALPHA / DIGIT / "." / "-" / "_"
 * </pre>
 *
 * @author Simon Greatrix on 24/09/2020.
 */
public class DidUriValidator implements ConstraintValidator<DidUri, URI> {

  /** A DID ID must match this. */
  private static final Pattern DID_ID = Pattern.compile("(?:[A-Za-z0-9._-]*:)*[A-Za-z0-9._-]+");

  /** A DID method must only use these characters. */
  private static final Pattern DID_METHOD = Pattern.compile("[a-z0-9]+");


  /**
   * Validate a URI as a DID. If the input is not valid, then there is no indication as to why from this method.
   *
   * @param uri the URI to validate
   *
   * @return true if the input is valid
   */
  public static boolean isValid(URI uri) {
    return uri != null && isValid(uri.getScheme(), uri.getRawSchemeSpecificPart(), false);
  }


  /**
   * Validate a URI or URL as a DID. If the input is not valid, then there is no indication as to why from this method.
   *
   * @param scheme the scheme, which must be "did"
   * @param part   the scheme specific part
   * @param isUrl  if true, allow a path, query and fragment.
   *
   * @return true if the input is valid
   */
  public static boolean isValid(String scheme, String part, boolean isUrl) {
    // Scheme must be specified, and must be "did" as lower case.
    if (!Objects.equals("did", scheme)) {
      return false;
    }

    int separator = part.indexOf(':');
    if (separator == -1 || separator == part.length() - 1) {
      return false;
    }

    String method = part.substring(0, separator);
    if (!DID_METHOD.matcher(method).matches()) {
      return false;
    }

    part = part.substring(separator + 1);
    if (isUrl) {
      // can have a query, so remove that
      int p = part.indexOf('?');
      if (p != -1) {
        part = part.substring(0, p);
      }

      // can have a path, so remove that
      p = part.indexOf('/');
      if (p != -1) {
        part = part.substring(0, p);
      }
    }

    if (!DID_ID.matcher(part).matches()) {
      return false;
    }

    return true;
  }


  /**
   * Validate a URI or URL as a DID.
   *
   * @param scheme  the scheme, which must be "did"
   * @param part    the scheme specific part
   * @param context the validation context
   * @param isUrl   if true, allow a path, query and fragment.
   *
   * @return true if the input is valid
   */
  public static boolean isValid(String scheme, String part, ConstraintValidatorContext context, boolean isUrl) {
    // Scheme must be specified, and must be "did" as lower case.
    if (!Objects.equals("did", scheme)) {
      context.buildConstraintViolationWithTemplate("{io.setl.chain.cw.data.validate.DidUri.badScheme}").addConstraintViolation();
      return false;
    }

    int separator = part.indexOf(':');
    if (separator == -1 || separator == part.length() - 1) {
      context.buildConstraintViolationWithTemplate("{io.setl.chain.cw.data.validate.DidUri.missingMethod}").addConstraintViolation();
      return false;
    }

    String method = part.substring(0, separator);
    if (!DID_METHOD.matcher(method).matches()) {
      context.buildConstraintViolationWithTemplate("{io.setl.chain.cw.data.validate.DidUri.methodLowerCase}").addConstraintViolation();
      return false;
    }

    part = part.substring(separator + 1);
    if (isUrl) {
      // can have a query, so remove that
      int p = part.indexOf('?');
      if (p != -1) {
        part = part.substring(0, p);
      }

      // can have a path, so remove that
      p = part.indexOf('/');
      if (p != -1) {
        part = part.substring(0, p);
      }
    }

    if (!DID_ID.matcher(part).matches()) {
      context.buildConstraintViolationWithTemplate("{io.setl.chain.cw.data.validate.DidUri.invalidId}").addConstraintViolation();
      return false;
    }

    return true;
  }


  @Override
  public boolean isValid(URI value, ConstraintValidatorContext context) {
    if (value == null) {
      // Test for null with the appropriate annotation
      return true;
    }

    if (value.getFragment() != null) {
      context.buildConstraintViolationWithTemplate("{io.setl.chain.cw.data.validate.DidUri.fragmentPresent}").addConstraintViolation();
      return false;
    }

    return isValid(value.getScheme(), value.getRawSchemeSpecificPart(), context, false);
  }

}
