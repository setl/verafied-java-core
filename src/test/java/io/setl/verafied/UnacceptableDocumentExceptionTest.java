package io.setl.verafied;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

/**
 * @author Simon Greatrix on 04/11/2021.
 */
public class UnacceptableDocumentExceptionTest {

  @Test
  public void test1() {
    UnacceptableDocumentException e = new UnacceptableDocumentException("code1","message1");
    assertEquals("code1",e.getCode());
    assertEquals("message1",e.getMessage());
    assertNotNull(e.getParameters());
    assertTrue(e.getParameters().isEmpty());
  }

  public void test2() {
    Map<String,Object> map = Map.of("a","b","c","d");
    UnacceptableDocumentException e = new UnacceptableDocumentException("code2","message2",map);
    assertEquals("code2",e.getCode());
    assertEquals("message2",e.getMessage());
    assertNotNull(e.getParameters());
    assertEquals(map,e.getParameters());
    assertNotSame(map,e.getParameters());
  }
}