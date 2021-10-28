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

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;


/**
 * Annotation to mark that a field must be a valid DIR URL. By default, the path, query and fragment parts are optional.
 *
 * @author Simon Greatrix on 07/09/2020.
 */
@Documented
@Constraint(validatedBy = DidUrlValidator.class)
@Target({METHOD, FIELD, PARAMETER, ANNOTATION_TYPE, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DidUrl {

  /** Enumeration to specify whether a part of the URL has to be present or not. */
  enum Has {
    /** The URL part must be present. */
    YES,

    /** The URL part must not be present. */
    NO,

    /** The URL part is optional. */
    EITHER
  }


  /** Validation groups. */
  Class<?>[] groups() default {};


  /** Specify if the URL must have a fragment specifier. (Optional by default) */
  Has hasFragment() default Has.EITHER;


  /** Specify if the URL must have a path component. (Optional by default) */
  Has hasPath() default Has.EITHER;


  /** Specify if the URL must have a query part. (Optional by default) */
  Has hasQuery() default Has.EITHER;


  /** Message to use by default. */
  String message() default "{io.setl.chain.cw.data.validate.DidUrl.message}";


  /** Required prefix to the URL path, if any. (Empty by default) */
  String pathPrefix() default "";


  /** Validation payload. */
  Class<? extends Payload>[] payload() default {};

}
