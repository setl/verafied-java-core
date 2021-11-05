package io.setl.verafied.did.validate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import javax.validation.ConstraintValidatorContext;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Simon Greatrix on 04/11/2021.
 */
public class DidUriValidatorTest {

  ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);

  ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);


  @Test
  public void badId1() {
    assertFalse(DidUriValidator.isValid("did", "setl:wibble:", context, false));
    verify(context).buildConstraintViolationWithTemplate("{io.setl.verafied.did.validate.DidUri.invalidId}");
  }


  @Test
  public void badId2() {
    assertFalse(DidUriValidator.isValid("did", "setl:wibb/e", context, false));
    verify(context).buildConstraintViolationWithTemplate("{io.setl.verafied.did.validate.DidUri.invalidId}");
  }


  @Test
  public void badMethod() {
    assertFalse(DidUriValidator.isValid("did", "setl/wibble", context, false));
    verify(context).buildConstraintViolationWithTemplate("{io.setl.verafied.did.validate.DidUri.missingMethod}");
  }


  @Test
  public void badMethodCase() {
    assertFalse(DidUriValidator.isValid("did", "s*tl:wibble", context, false));
    verify(context).buildConstraintViolationWithTemplate("{io.setl.verafied.did.validate.DidUri.methodLowerCase}");
  }


  @Test
  public void badScheme1() {
    assertFalse(DidUriValidator.isValid("DID", "setl:wibble", context, false));
    verify(context).buildConstraintViolationWithTemplate("{io.setl.verafied.did.validate.DidUri.badScheme}");
  }

  @Test
  public void badScheme2() {
    assertFalse(DidUriValidator.isValid("ftp", "setl:wibble", context, false));
    verify(context).buildConstraintViolationWithTemplate("{io.setl.verafied.did.validate.DidUri.badScheme}");
  }

  @Test
  public void removeUrlPath() {
    assertFalse(DidUriValidator.isValid("did", "setl:wibble/path/a/b/v", false));
    assertTrue(DidUriValidator.isValid("did", "setl:wibble/path/a/b/v", true));
  }

  @Test
  public void removeUrlQuery() {
    assertFalse(DidUriValidator.isValid("did", "setl:wibble?a=b&c=d", false));
    assertTrue(DidUriValidator.isValid("did", "setl:wibble?a=b&c=d", true));
  }

  @Test
  public void removeUrlPathAndQuery() {
    assertFalse(DidUriValidator.isValid("did", "setl:wibble/path/a/b/v?a=b&c=d", false));
    assertTrue(DidUriValidator.isValid("did", "setl:wibble/path/a/b/v?a=b&c=d", true));
  }


  @Before
  public void before() {
    when(context.buildConstraintViolationWithTemplate(any())).thenReturn(builder);
  }


  @Test
  public void test() {
    DidUriValidator instance = new DidUriValidator();
    assertTrue(instance.isValid(null, context));
    assertTrue(instance.isValid(URI.create("did:setl:wibble"), context));
    assertFalse(instance.isValid(URI.create("did:setl:wibble#hash"), context));
    verify(context).buildConstraintViolationWithTemplate("{io.setl.verafied.did.validate.DidUri.fragmentPresent}");

    assertFalse(DidUriValidator.isValid(null));
    assertTrue(DidUriValidator.isValid(URI.create("did:setl:wibble")));
    assertFalse(DidUriValidator.isValid(URI.create("dod:setl:wibble")));
    assertFalse(DidUriValidator.isValid(URI.create("did:setl:wibble?hl=1")));
  }


}