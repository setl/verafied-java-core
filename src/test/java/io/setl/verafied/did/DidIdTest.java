package io.setl.verafied.did;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.net.URI;

import org.junit.Test;

/**
 * @author Simon Greatrix on 04/11/2021.
 */
public class DidIdTest {

  @Test
  public void test() {
    URI uri = URI.create("did:setl:abcdef/foo/bar?a=b&c=d#1234");
    DidId did1 = new DidId("setl", "abcdef", "/foo/bar", "a=b&c=d", "1234");
    DidId did2 = new DidId(uri);
    assertEquals(did1, did2);
    assertEquals(did2, did1);
    assertEquals(did1.getFragment(), did2.getFragment());
    assertEquals(did1.getId(), did2.getId());
    assertEquals(did1.getMethod(), did2.getMethod());
    assertEquals(did1.getPath(), did2.getPath());
    assertEquals(did1.getQuery(), did2.getQuery());
    assertEquals(did1.getUri(), did2.getUri());
    assertEquals(did1.hashCode(), did2.hashCode());
    assertTrue(did1.isValid());
    assertTrue(did2.isValid());
    assertEquals(did1, did1);
    assertEquals(uri.toString(), did1.toString());
  }


  @Test
  public void testFragment() {
    URI uri1 = URI.create("did:setl:abcdef#1234");
    URI uri2 = URI.create("did:setl:abcdef");
    DidId did1 = new DidId(uri1);
    DidId did2 = new DidId(uri2);
    assertNotEquals(did1, did2);
    assertEquals(did2, did1.withoutFragment());
    assertSame(did2, did2.withoutFragment());
  }

}