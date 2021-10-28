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

import static io.setl.verafied.did.validate.DidUriValidator.addViolation;

import java.net.URI;
import java.util.regex.Pattern;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import io.setl.verafied.did.validate.DidUrl.Has;

/**
 * Enforces the DID URL specification. This is:
 *
 * <pre>
 * did-url = did path-abempty [ "?" query ] [ "#" fragment ]
 *
 * did                = "did:" method-name ":" method-specific-id
 * method-name        = 1*method-char
 * method-char        = %x61-7A / DIGIT
 * method-specific-id = *( *idchar ":" ) 1*idchar
 * idchar             = ALPHA / DIGIT / "." / "-" / "_"
 *
 * path-abempty  = *( "/" segment )
 * segment       = *pchar
 * query         = *( pchar / "/" / "?" )
 * fragment      = *( pchar / "/" / "?" )
 * pchar         = unreserved / pct-encoded / sub-delims / ":" / "@"
 * unreserved    = ALPHA / DIGIT / "-" / "." / "_" / "~"
 * pct-encoded   = "%" HEXDIG HEXDIG
 * sub-delims    = "!" / "$" / "&" / "'" / "(" / ")" / "*" / "+" / "," / ";" / "="
 * </pre>
 *
 * @author Simon Greatrix on 24/09/2020.
 */
public class DidUrlValidator implements ConstraintValidator<DidUrl, URI> {

  private static final Pattern DID_FRAGMENT = Pattern.compile("(?:[A-Za-z0-9\\-._~!$&'()*+,;=:@/?]|%\\p{XDigit}\\p{XDigit})*");

  private static final Pattern PATH_ABEMPTY = Pattern.compile("(?:/(?:[A-Za-z0-9\\-._~!$&'()*+,;=:@/]|%\\p{XDigit}\\p{XDigit})*)?");


  public static boolean isValid(URI value) {
    return isValid(value, "", Has.EITHER, Has.EITHER, Has.EITHER);
  }


  /**
   * Validate if a URI conforms to the rules for a DID URI with the specified additional requirements.
   *
   * @param value       the value to test
   * @param pathPrefix  the prefix
   * @param hasPath     requirement for whether a path must be present or not
   * @param hasQuery    requirement for whether a query must be present or not
   * @param hasFragment requirement for whether a fragment must be present or not
   *
   * @return true if value
   */
  @SuppressWarnings("checkstyle:OverloadMethodsDeclarationOrder")
  public static boolean isValid(URI value, String pathPrefix, Has hasPath, Has hasQuery, Has hasFragment) {
    if (value == null) {
      return false;
    }
    return isValid(value, null, pathPrefix, hasPath, hasQuery, hasFragment);
  }


  private static boolean isValid(URI value, ConstraintValidatorContext context, String pathPrefix, Has hasPath, Has hasQuery, Has hasFragment) {
    // Must pass all the DID URI validation rules
    if (!DidUriValidator.isValid(value.getScheme(), value.getSchemeSpecificPart(), context, true)) {
      return false;
    }

    if (!isValidFragment(context, value, hasFragment)) {
      return false;
    }

    String part = value.getRawSchemeSpecificPart();
    int p = isValidQuery(context, part, hasQuery);
    if (p < 0) {
      return false;
    }
    part = part.substring(0, p);

    return isValidPath(context, part, hasPath, pathPrefix);
  }


  private static boolean isValidFragment(ConstraintValidatorContext context, URI value, Has hasFragment) {
    String part = value.getRawFragment();
    if (part != null) {
      if (hasFragment == Has.NO) {
        addViolation(context, "{io.setl.verafied.did.validate.DidUrl.fragmentPresent}");
        return false;
      }
      if (!DID_FRAGMENT.matcher(part).matches()) {
        addViolation(context, "{io.setl.verafied.did.validate.DidUrl.badFragment}");
        return false;
      }
    } else {
      if (hasFragment == Has.YES) {
        addViolation(context, "{io.setl.verafied.did.validate.DidUrl.missingFragment}");
        return false;
      }
    }
    return true;
  }


  private static boolean isValidPath(ConstraintValidatorContext context, String part, Has hasPath, String pathPrefix) {
    int p = part.indexOf('/');
    if (p != -1) {
      if (hasPath == Has.NO) {
        addViolation(context, "{io.setl.verafied.did.validate.DidUrl.pathPresent}");
        return false;
      }
      String f = part.substring(p);
      // we have an absolute path
      if (!PATH_ABEMPTY.matcher(f).matches()) {
        addViolation(context, "{io.setl.verafied.did.validate.DidUrl.badPath}");
        return false;
      }
      if (pathPrefix != null && !f.startsWith(pathPrefix)) {
        addViolation(context, "{io.setl.verafied.did.validate.DidUrl.pathPrefix}");
        return false;
      }
    } else {
      if (hasPath == Has.NO) {
        addViolation(context, "{io.setl.verafied.did.validate.DidUrl.missingPath}");
        return false;
      }
    }

    return true;
  }


  private static int isValidQuery(ConstraintValidatorContext context, String part, Has hasQuery) {
    int p = part.indexOf('?');
    if (p != -1) {
      if (hasQuery == Has.NO) {
        addViolation(context, "{io.setl.verafied.did.validate.DidUrl.queryPresent}");
        return -1;
      }
      String f = part.substring(p + 1);
      // fragments and queries validate on the same reg-exp
      if (!DID_FRAGMENT.matcher(f).matches()) {
        addViolation(context, "{io.setl.verafied.did.validate.DidUrl.badQuery}");
        return -1;
      }
      return p;
    } else {
      if (hasQuery == Has.YES) {
        addViolation(context, "{io.setl.verafied.did.validate.DidUrl.missingQuery}");
        return -1;
      }
    }
    return part.length();
  }


  private Has hasFragment;

  private Has hasPath;

  private Has hasQuery;

  private String pathPrefix;


  @Override
  public void initialize(DidUrl constraintAnnotation) {
    hasPath = constraintAnnotation.hasPath();
    hasQuery = constraintAnnotation.hasQuery();
    hasFragment = constraintAnnotation.hasFragment();
    pathPrefix = constraintAnnotation.pathPrefix();
  }


  @Override
  @SuppressWarnings("checkstyle:OverloadMethodsDeclarationOrder")
  public boolean isValid(URI value, ConstraintValidatorContext context) {
    if (value == null) {
      // Test for null with the appropriate annotation
      return true;
    }

    return isValid(value, context, pathPrefix, hasPath, hasQuery, hasFragment);
  }

}
