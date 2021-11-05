package io.setl.verafied.did.validate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.annotation.Annotation;
import java.net.URI;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

import org.junit.Before;
import org.junit.Test;

import io.setl.verafied.did.validate.DidUrl.Has;

/**
 * @author Simon Greatrix on 04/11/2021.
 */
public class DidUrlValidatorTest {

  static class MyAnnotation implements DidUrl {

    Has fragment;

    Has path;

    String prefix;

    Has query;


    public MyAnnotation(String prefix, Has path, Has query, Has fragment) {
      this.fragment = fragment;
      this.path = path;
      this.prefix = prefix;
      this.query = query;
    }


    @Override
    public Class<? extends Annotation> annotationType() {
      return DidUrl.class;
    }


    @Override
    public Class<?>[] groups() {
      return new Class[0];
    }


    @Override
    public Has hasFragment() {
      return fragment;
    }


    @Override
    public Has hasPath() {
      return path;
    }


    @Override
    public Has hasQuery() {
      return query;
    }


    @Override
    public String message() {
      return "message";
    }


    @Override
    public String pathPrefix() {
      return prefix;
    }


    @Override
    public Class<? extends Payload>[] payload() {
      return new Class[0];
    }

  }



  ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);

  ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);


  @Before
  public void before() {
    when(context.buildConstraintViolationWithTemplate(any())).thenReturn(builder);
  }


  @Test
  public void testIsValid() {
    assertTrue(DidUrlValidator.isValid(URI.create("did:setl:wibble")));
    assertTrue(DidUrlValidator.isValid(URI.create("did:setl:wibble#hash")));
    assertTrue(DidUrlValidator.isValid(URI.create("did:setl:wibble?a=b&c=d")));
    assertTrue(DidUrlValidator.isValid(URI.create("did:setl:wibble/path/1/2?e=f#456")));
    assertFalse(DidUrlValidator.isValid(null));
    assertFalse(DidUrlValidator.isValid(URI.create("ftp:setl:wibble/path/1/2?e=f#456")));
    assertFalse(DidUrlValidator.isValid(URI.create("did:SETL:wibble/path/1/2?e=f#456")));
    assertFalse(DidUrlValidator.isValid(URI.create("did:setl:w%bble/path/1/2?e=f#456")));
  }

  @Test
  public void testFragmentNo() {
    MyAnnotation myAnnotation = new MyAnnotation("", Has.EITHER,Has.EITHER,Has.NO);
    DidUrlValidator validator = new DidUrlValidator();
    validator.initialize(myAnnotation);
    assertTrue(validator.isValid(null, context));
    assertTrue(validator.isValid(URI.create("did:setl:wibble"),context));
    assertFalse(validator.isValid(URI.create("did:setl:wibble#fragment"),context));
    verify(context).buildConstraintViolationWithTemplate(eq("{io.setl.verafied.did.validate.DidUrl.fragmentPresent}"));
  }


  @Test
  public void testFragmentYes() {
    MyAnnotation myAnnotation = new MyAnnotation("", Has.EITHER,Has.EITHER,Has.YES);
    DidUrlValidator validator = new DidUrlValidator();
    validator.initialize(myAnnotation);
    assertTrue(validator.isValid(null, context));
    assertTrue(validator.isValid(URI.create("did:setl:wibble#fragment"),context));
    assertFalse(validator.isValid(URI.create("did:setl:wibble"),context));
    verify(context).buildConstraintViolationWithTemplate(eq("{io.setl.verafied.did.validate.DidUrl.missingFragment}"));
  }



  @Test
  public void testFragmentBad() {
    MyAnnotation myAnnotation = new MyAnnotation("", Has.EITHER,Has.EITHER,Has.EITHER);
    DidUrlValidator validator = new DidUrlValidator();
    validator.initialize(myAnnotation);
    assertTrue(validator.isValid(null, context));
    assertTrue(validator.isValid(URI.create("did:setl:wibble#fragment"),context));
    assertFalse(validator.isValid(URI.create("did:setl:wibble#fragm[]"),context));
    verify(context).buildConstraintViolationWithTemplate(eq("{io.setl.verafied.did.validate.DidUrl.badFragment}"));
  }


  @Test
  public void testPathNo() {
    MyAnnotation myAnnotation = new MyAnnotation("", Has.NO,Has.EITHER,Has.EITHER);
    DidUrlValidator validator = new DidUrlValidator();
    validator.initialize(myAnnotation);
    assertTrue(validator.isValid(null, context));
    assertTrue(validator.isValid(URI.create("did:setl:wibble"),context));
    assertFalse(validator.isValid(URI.create("did:setl:wibble/path/a"),context));
    verify(context).buildConstraintViolationWithTemplate(eq("{io.setl.verafied.did.validate.DidUrl.pathPresent}"));
  }


  @Test
  public void testPathYes() {
    MyAnnotation myAnnotation = new MyAnnotation("", Has.YES,Has.EITHER,Has.EITHER);
    DidUrlValidator validator = new DidUrlValidator();
    validator.initialize(myAnnotation);
    assertTrue(validator.isValid(null, context));
    assertTrue(validator.isValid(URI.create("did:setl:wibble/abc"),context));
    assertFalse(validator.isValid(URI.create("did:setl:wibble"),context));
    verify(context).buildConstraintViolationWithTemplate(eq("{io.setl.verafied.did.validate.DidUrl.missingPath}"));
  }



  @Test
  public void testPathBad() {
    MyAnnotation myAnnotation = new MyAnnotation("", Has.EITHER,Has.EITHER,Has.EITHER);
    DidUrlValidator validator = new DidUrlValidator();
    validator.initialize(myAnnotation);
    assertTrue(validator.isValid(null, context));
    assertTrue(validator.isValid(URI.create("did:setl:wibble/ab/cd/ef/"),context));
    assertFalse(validator.isValid(URI.create("did:setl:wibble/ab[]/cde"),context));
    verify(context).buildConstraintViolationWithTemplate(eq("{io.setl.verafied.did.validate.DidUrl.badPath}"));
  }

  @Test
  public void testPathPrefix() {
    MyAnnotation myAnnotation = new MyAnnotation("/start", Has.YES,Has.EITHER,Has.EITHER);
    DidUrlValidator validator = new DidUrlValidator();
    validator.initialize(myAnnotation);
    assertTrue(validator.isValid(null, context));
    assertTrue(validator.isValid(URI.create("did:setl:wibble/start/a/b"),context));
    assertTrue(validator.isValid(URI.create("did:setl:wibble/starting/a/b"),context));
    assertFalse(validator.isValid(URI.create("did:setl:wibble/something"),context));
    verify(context).buildConstraintViolationWithTemplate(eq("{io.setl.verafied.did.validate.DidUrl.pathPrefix}"));
  }

  @Test
  public void testQueryNo() {
    MyAnnotation myAnnotation = new MyAnnotation("", Has.EITHER,Has.NO,Has.EITHER);
    DidUrlValidator validator = new DidUrlValidator();
    validator.initialize(myAnnotation);
    assertTrue(validator.isValid(null, context));
    assertTrue(validator.isValid(URI.create("did:setl:wibble"),context));
    assertFalse(validator.isValid(URI.create("did:setl:wibble?abcd"),context));
    verify(context).buildConstraintViolationWithTemplate(eq("{io.setl.verafied.did.validate.DidUrl.queryPresent}"));
  }


  @Test
  public void testQueryYes() {
    MyAnnotation myAnnotation = new MyAnnotation("", Has.EITHER,Has.YES,Has.EITHER);
    DidUrlValidator validator = new DidUrlValidator();
    validator.initialize(myAnnotation);
    assertTrue(validator.isValid(null, context));
    assertTrue(validator.isValid(URI.create("did:setl:wibble?578"),context));
    assertFalse(validator.isValid(URI.create("did:setl:wibble"),context));
    verify(context).buildConstraintViolationWithTemplate(eq("{io.setl.verafied.did.validate.DidUrl.missingQuery}"));
  }



  @Test
  public void testQueryBad() {
    MyAnnotation myAnnotation = new MyAnnotation("", Has.EITHER,Has.EITHER,Has.EITHER);
    DidUrlValidator validator = new DidUrlValidator();
    validator.initialize(myAnnotation);
    assertTrue(validator.isValid(null, context));
    assertTrue(validator.isValid(URI.create("did:setl:wibble?a=b&c=d"),context));
    assertFalse(validator.isValid(URI.create("did:setl:wibble?[]=123"),context));
    verify(context).buildConstraintViolationWithTemplate(eq("{io.setl.verafied.did.validate.DidUrl.badQuery}"));
  }
}