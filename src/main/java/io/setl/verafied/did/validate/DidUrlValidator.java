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
import java.util.regex.Pattern;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import io.setl.verafied.did.validate.DidUrl.Has;

/**
 * Enforces the DID URL specification, which is:
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

  private static final Pattern PATH_ABEMPTY = Pattern.compile("(?:/(?:[A-Za-z0-9\\-._~!$&'()*+,;=:@]|%\\p{XDigit}\\p{XDigit})*)*");


  public static boolean isValid(URI value) {
    return isValid(value, "", Has.EITHER, Has.EITHER, Has.EITHER);
  }


  public static boolean isValid(URI value, String pathPrefix, Has hasPath, Has hasQuery, Has hasFragment) {
    if (value == null) {
      return false;
    }

    // Must pass all the DID URI validation rules
    if (!DidUriValidator.isValid(value.getScheme(), value.getSchemeSpecificPart(), true)) {
      return false;
    }

    String part = value.getRawFragment();
    if (part != null) {
      if (hasFragment == Has.NO) {
        return false;
      }
      if (!DID_FRAGMENT.matcher(part).matches()) {
        return false;
      }
    } else {
      if (hasFragment == Has.YES) {
        return false;
      }
    }

    part = value.getRawSchemeSpecificPart();
    int p = part.indexOf('?');
    if (p != -1) {
      if (hasQuery == Has.NO) {
        return false;
      }
      String f = part.substring(p + 1);
      // fragments and queries validate on the same reg-exp
      if (!DID_FRAGMENT.matcher(f).matches()) {
        return false;
      }
      part = part.substring(0, p);
    } else {
      if (hasQuery == Has.YES) {
        return false;
      }
    }

    p = part.indexOf('/');
    if (p != -1) {
      if (hasPath == Has.NO) {
        return false;
      }
      String f = part.substring(p);
      // we have an absolute path
      if (!PATH_ABEMPTY.matcher(f).matches()) {
        return false;
      }
      if (!f.startsWith(pathPrefix)) {
        return false;
      }
    } else {
      if (hasPath == Has.NO) {
        return false;
      }
    }

    return true;
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
  public boolean isValid(URI value, ConstraintValidatorContext context) {
    if (value == null) {
      // Test for null with the appropriate annotation
      return true;
    }

    // Must pass all the DID URI validation rules
    if (!DidUriValidator.isValid(value.getScheme(), value.getSchemeSpecificPart(), context, true)) {
      return false;
    }

    String part = value.getRawFragment();
    if (part != null) {
      if (hasFragment == Has.NO) {
        context.buildConstraintViolationWithTemplate("{io.setl.chain.cw.data.validate.DidUrl.fragmentPresent}").addConstraintViolation();
        return false;
      }
      if (!DID_FRAGMENT.matcher(part).matches()) {
        context.buildConstraintViolationWithTemplate("{io.setl.chain.cw.data.validate.DidUrl.badFragment}").addConstraintViolation();
        return false;
      }
    } else {
      if (hasFragment == Has.YES) {
        context.buildConstraintViolationWithTemplate("{io.setl.chain.cw.data.validate.DidUrl.missingFragment}").addConstraintViolation();
        return false;
      }

    }

    part = value.getRawSchemeSpecificPart();
    int p = part.indexOf('?');
    if (p != -1) {
      if (hasQuery == Has.NO) {
        context.buildConstraintViolationWithTemplate("{io.setl.chain.cw.data.validate.DidUrl.queryPresent}").addConstraintViolation();
        return false;
      }
      String f = part.substring(p + 1);
      // fragments and queries validate on the same reg-exp
      if (!DID_FRAGMENT.matcher(f).matches()) {
        context.buildConstraintViolationWithTemplate("{io.setl.chain.cw.data.validate.DidUrl.badQuery}").addConstraintViolation();
        return false;
      }
      part = part.substring(0, p);
    } else {
      if (hasQuery == Has.YES) {
        context.buildConstraintViolationWithTemplate("{io.setl.chain.cw.data.validate.DidUrl.missingQuery}").addConstraintViolation();
        return false;
      }
    }

    p = part.indexOf('/');
    if (p != -1) {
      if (hasPath == Has.NO) {
        context.buildConstraintViolationWithTemplate("{io.setl.chain.cw.data.validate.DidUrl.pathPresent}").addConstraintViolation();
        return false;
      }
      String f = part.substring(p);
      // we have an absolute path
      if (!PATH_ABEMPTY.matcher(f).matches()) {
        context.buildConstraintViolationWithTemplate("{io.setl.chain.cw.data.validate.DidUrl.badPath}").addConstraintViolation();
        return false;
      }
      if (!f.startsWith(pathPrefix)) {
        context.buildConstraintViolationWithTemplate("{io.setl.chain.cw.data.validate.DidUrl.pathPrefix}").addConstraintViolation();
        return false;
      }
    } else {
      if (hasPath == Has.NO) {
        context.buildConstraintViolationWithTemplate("{io.setl.chain.cw.data.validate.DidUrl.missingPath}").addConstraintViolation();
        return false;
      }
    }

    return true;
  }

}
