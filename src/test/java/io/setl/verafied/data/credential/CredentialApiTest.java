package io.setl.verafied.data.credential;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Duration;
import java.util.Base64;
import javax.json.JsonValue;
import javax.json.spi.JsonProvider;

import org.junit.Before;
import org.junit.Test;

import io.setl.json.Canonical;
import io.setl.verafied.CredentialConstants;
import io.setl.verafied.UnacceptableDocumentException;
import io.setl.verafied.data.JsonConvert;
import io.setl.verafied.data.Proof;
import io.setl.verafied.data.TypedKeyPair;
import io.setl.verafied.data.jwk.SigningAlgorithm;
import io.setl.verafied.did.DecentralizedIdentifier;
import io.setl.verafied.did.DidId;
import io.setl.verafied.did.DidStoreException;
import io.setl.verafied.proof.CanonicalJsonWithJws;
import io.setl.verafied.proof.ProofContext;
import io.setl.verafied.proof.VerifyContext;
import io.setl.verafied.revocation.RevocationChecker;

/**
 * @author Simon Greatrix on 02/11/2021.
 */
public class CredentialApiTest {

  public static String load(InputStream in) throws IOException {
    byte[] bytes = in.readAllBytes();
    return new String(bytes, StandardCharsets.UTF_8);
  }


  Credential credential;

  DecentralizedIdentifier decentralizedIdentifier;

  TestDidStore testDidStore;

  TypedKeyPair typedKeyPair;


  @Before
  public void before() throws Exception {
    String didJson = load(JsonConvert.class.getResourceAsStream("sample_did.json"));
    decentralizedIdentifier = JsonConvert.toInstance(didJson, DecentralizedIdentifier.class);

    String keyText = load(JsonConvert.class.getResourceAsStream("private_key.txt"));
    byte[] keyBytes = Base64.getMimeDecoder().decode(keyText);
    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
    KeyFactory factory = KeyFactory.getInstance("EC");
    PrivateKey privateKey = factory.generatePrivate(keySpec);
    typedKeyPair = new TypedKeyPair(SigningAlgorithm.ES256, privateKey);
    typedKeyPair.setId(new DidId(URI.create("did:setl:qDjni0qJX3KHrvgn46JBEVYE#erGcvT")));

    StatusDeserializer.addTypeMapping("VerafiedHttpCheck", TestStatus.class);
    credential = JsonConvert.toInstance(load(JsonConvert.class.getResourceAsStream("sample_vc_1.json")), Credential.class);

    testDidStore = new TestDidStore();
    testDidStore.add(decentralizedIdentifier);
  }


  @Test(expected = UnacceptableDocumentException.class)
  public void testAlreadyExpired() throws Exception {
    credential.setProof(null);
    credential.setExpirationDate(CredentialConstants.getClock().instant().minus(Duration.ofDays(1)));
    ProofContext context = new ProofContext(new CanonicalJsonWithJws());
    CredentialApi.prove(context, credential, typedKeyPair);
  }


  @Test(expected = UnacceptableDocumentException.class)
  public void testBadContext() throws Exception {
    credential.setProof(null);
    credential.setContext(JsonProvider.provider().createValue("BigBen"));
    ProofContext context = new ProofContext(new CanonicalJsonWithJws());
    CredentialApi.prove(context, credential, typedKeyPair);
  }


  @Test(expected = UnacceptableDocumentException.class)
  public void testDidNotKnown() throws Exception {
    VerifyContext verifyContext = new VerifyContext(new TestDidStore());
    CredentialApi.verify(credential, verifyContext, null);
  }


  @Test
  public void testHappyPath() throws Exception {
    VerifyContext verifyContext = new VerifyContext(testDidStore);
    CredentialApi.verify(credential, verifyContext, null);

    ProofContext context = new ProofContext(new CanonicalJsonWithJws());
    credential.setProof(null);
    CredentialApi.prove(context, credential, typedKeyPair);

    verifyContext = new VerifyContext(testDidStore);
    CredentialApi.verify(credential, verifyContext, null);
    System.out.println(Canonical.cast(JsonConvert.toJson(credential)).toPrettyString());
  }


  @Test(expected = UnacceptableDocumentException.class)
  public void testIsRevoked() throws Exception {
    ProofContext context = new ProofContext(new CanonicalJsonWithJws());
    credential.setProof(null);
    CredentialApi.prove(context, credential, typedKeyPair);

    RevocationChecker checker = mock(RevocationChecker.class);
    when(checker.test(any(), any(), any())).thenReturn(true);

    VerifyContext verifyContext = new VerifyContext(testDidStore);
    CredentialApi.verify(credential, verifyContext, checker);
  }


  @Test(expected = UnacceptableDocumentException.class)
  public void testIssuedInFuture() throws Exception {
    credential.setProof(null);
    credential.setIssuanceDate(CredentialConstants.getClock().instant().plus(Duration.ofDays(1)));
    ProofContext context = new ProofContext(new CanonicalJsonWithJws());
    CredentialApi.prove(context, credential, typedKeyPair);
  }


  @Test(expected = UnacceptableDocumentException.class)
  public void testMissingContext() throws Exception {
    credential.setProof(null);
    credential.setContext(JsonValue.NULL);
    ProofContext context = new ProofContext(new CanonicalJsonWithJws());
    CredentialApi.prove(context, credential, typedKeyPair);
  }


  @Test(expected = UnacceptableDocumentException.class)
  public void testMissingId() throws Exception {
    credential.setProof(null);
    credential.setId(null);
    ProofContext context = new ProofContext(new CanonicalJsonWithJws());
    CredentialApi.prove(context, credential, typedKeyPair);
  }


  @Test(expected = UnacceptableDocumentException.class)
  public void testNoProof() throws UnacceptableDocumentException, DidStoreException {
    credential.setProof(null);
    VerifyContext verifyContext = new VerifyContext(testDidStore);
    CredentialApi.verify(credential, verifyContext, null);
  }


  @Test(expected = UnacceptableDocumentException.class)
  public void testWrongProof() throws UnacceptableDocumentException, DidStoreException {
    Proof proof = credential.getProof();
    credential.setProof(null);
    credential.setIssuanceDate(CredentialConstants.getClock().instant());
    credential.setProof(proof);
    VerifyContext verifyContext = new VerifyContext(testDidStore);
    CredentialApi.verify(credential, verifyContext, null);
  }

}