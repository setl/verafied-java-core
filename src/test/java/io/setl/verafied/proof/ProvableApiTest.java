package io.setl.verafied.proof;

import static org.junit.Assert.assertEquals;

import java.util.Set;
import javax.json.spi.JsonProvider;

import org.junit.Test;

import io.setl.verafied.CredentialConstants;
import io.setl.verafied.UnacceptableDocumentException;

/**
 * @author Simon Greatrix on 04/11/2021.
 */
public class ProvableApiTest {

  @Test(expected = UnacceptableDocumentException.class)
  public void testGetTypesBadArrayType1() throws UnacceptableDocumentException {
    ProvableApi.getTypes(JsonProvider.provider().createArrayBuilder().add("Hello").addNull().build(), "test-case", "id");
  }


  @Test(expected = UnacceptableDocumentException.class)
  public void testGetTypesBadArrayType2() throws UnacceptableDocumentException {
    ProvableApi.getTypes(JsonProvider.provider().createArrayBuilder().add("Hello").add(45).build(), "test-case", "id");
  }


  @Test(expected = UnacceptableDocumentException.class)
  public void testGetTypesBadSingleType() throws UnacceptableDocumentException {
    ProvableApi.getTypes(JsonProvider.provider().createValue(23), "test-case", "id");
  }


  @Test
  public void testGetTypesHappy() throws UnacceptableDocumentException {
    Set<String> types = ProvableApi.getTypes(JsonProvider.provider().createArrayBuilder().add("Hello").add("World").build(), "test-case", "id");
    assertEquals(Set.of("Hello", "World"), types);
    types = ProvableApi.getTypes(JsonProvider.provider().createValue("Hello World"), "test-case", "id");
    assertEquals(Set.of("Hello World"), types);
  }


  @Test(expected = UnacceptableDocumentException.class)
  public void testGetTypesNoType() throws UnacceptableDocumentException {
    ProvableApi.getTypes(null, "test-case", "id");
  }


  @Test(expected = UnacceptableDocumentException.class)
  public void testVerifyContextBadContext() throws UnacceptableDocumentException {
    ProvableApi.verifyContext(JsonProvider.provider().createValue("OtherContext"), "thing", "id");
  }


  @Test(expected = UnacceptableDocumentException.class)
  public void testVerifyContextBadOrder() throws UnacceptableDocumentException {
    ProvableApi.verifyContext(
        JsonProvider.provider().createArrayBuilder().add("OtherContext").add(CredentialConstants.CREDENTIAL_CONTEXT).build(),
        "thing", "id"
    );
  }


  @Test(expected = UnacceptableDocumentException.class)
  public void testVerifyContextBadType() throws UnacceptableDocumentException {
    ProvableApi.verifyContext(JsonProvider.provider().createValue(23), "thing", "id");
  }


  @Test
  public void testVerifyContextHappy() throws UnacceptableDocumentException {
    ProvableApi.verifyContext(JsonProvider.provider().createValue(CredentialConstants.CREDENTIAL_CONTEXT), "thing", "id");
    ProvableApi.verifyContext(JsonProvider.provider().createArrayBuilder().add(CredentialConstants.CREDENTIAL_CONTEXT).add("OtherContext").build(),
        "thing", "id"
    );
  }


  @Test(expected = UnacceptableDocumentException.class)
  public void testVerifyContextNull() throws UnacceptableDocumentException {
    ProvableApi.verifyContext(null, "thing", "id");
  }


  @Test
  public void testVerifyTypeHappy() throws UnacceptableDocumentException {
    ProvableApi.verifyType(Set.of("abc", "def"), "type", "id", "abc");
  }


  @Test(expected = UnacceptableDocumentException.class)
  public void testVerifyTypeMissingType() throws UnacceptableDocumentException {
    ProvableApi.verifyType(Set.of("abc", "def"), "type", "id", "ghi");
  }


  @Test(expected = UnacceptableDocumentException.class)
  public void testVerifyTypeNoType() throws UnacceptableDocumentException {
    ProvableApi.verifyType(null, "type", "id", "abc");
  }

}