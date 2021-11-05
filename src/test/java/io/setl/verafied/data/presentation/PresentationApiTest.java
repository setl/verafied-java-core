package io.setl.verafied.data.presentation;

import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import io.setl.verafied.UnacceptableDocumentException;
import io.setl.verafied.data.JsonConvert;
import io.setl.verafied.data.Proof;
import io.setl.verafied.data.TypedKeyPair;
import io.setl.verafied.data.credential.CredentialApiTest;
import io.setl.verafied.data.credential.StatusDeserializer;
import io.setl.verafied.data.credential.TestDidStore;
import io.setl.verafied.data.credential.TestStatus;
import io.setl.verafied.data.jwk.SigningAlgorithm;
import io.setl.verafied.did.DecentralizedIdentifier;
import io.setl.verafied.did.DidId;
import io.setl.verafied.did.VerificationMethod;
import io.setl.verafied.proof.CanonicalJsonWithJws;
import io.setl.verafied.proof.ProofContext;
import io.setl.verafied.proof.VerifyContext;

/**
 * @author Simon Greatrix on 04/11/2021.
 */
public class PresentationApiTest {

  DecentralizedIdentifier decentralizedIdentifier1;

  DecentralizedIdentifier decentralizedIdentifier2;

  Presentation presentation;

  TestDidStore testDidStore;

  TypedKeyPair typedKeyPair1;

  TypedKeyPair typedKeyPair2;


  @Before
  public void before() throws Exception {
    String didJson = CredentialApiTest.load(JsonConvert.class.getResourceAsStream("sample_did.json"));
    decentralizedIdentifier1 = JsonConvert.toInstance(didJson, DecentralizedIdentifier.class);

    String keyText = CredentialApiTest.load(JsonConvert.class.getResourceAsStream("private_key.txt"));
    byte[] keyBytes = Base64.getMimeDecoder().decode(keyText);
    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
    KeyFactory factory = KeyFactory.getInstance("EC");
    PrivateKey privateKey = factory.generatePrivate(keySpec);
    typedKeyPair1 = new TypedKeyPair(SigningAlgorithm.ES256, privateKey);
    typedKeyPair1.setId(new DidId(URI.create("did:setl:qDjni0qJX3KHrvgn46JBEVYE#erGcvT")));

    didJson = CredentialApiTest.load(JsonConvert.class.getResourceAsStream("sample_did_2.json"));
    decentralizedIdentifier2 = JsonConvert.toInstance(didJson, DecentralizedIdentifier.class);

    keyText = CredentialApiTest.load(JsonConvert.class.getResourceAsStream("private_key_2.txt"));
    keyBytes = Base64.getMimeDecoder().decode(keyText);
    keySpec = new PKCS8EncodedKeySpec(keyBytes);
    factory = KeyFactory.getInstance("RSA");
    privateKey = factory.generatePrivate(keySpec);
    typedKeyPair2 = new TypedKeyPair(SigningAlgorithm.PS256, privateKey);
    typedKeyPair2.setId(new DidId(URI.create("did:setl:ygWIJJP5sGErqskusG853bZV#s1FLxF")));

    StatusDeserializer.addTypeMapping("VerafiedHttpCheck", TestStatus.class);

    presentation = JsonConvert.toInstance(CredentialApiTest.load(JsonConvert.class.getResourceAsStream("sample_vp_1.json")), Presentation.class);

    testDidStore = new TestDidStore();
    testDidStore.add(decentralizedIdentifier1);
    testDidStore.add(decentralizedIdentifier2);
  }


  @Test
  public void testHappyPath() throws Exception {
    PresentationApi.verify(presentation, new VerifyContext(testDidStore));

    ProofContext context = new ProofContext(new CanonicalJsonWithJws());
    presentation.setProof(null);
    PresentationApi.prove(context, presentation, typedKeyPair2);

    PresentationApi.verify(presentation, new VerifyContext(testDidStore));

    assertTrue(presentation.isValidType());
  }


  @Test(expected = UnacceptableDocumentException.class)
  public void testIncorrectSignature() throws Exception {
    Proof proof = presentation.getProof();
    proof.set(
        "jws",
        "eyJhbGciOiJQUzI1NiIsImI2NCI6ZmFsc2UsImNyaXQiOlsiYjY0Il19..Ac7z71gGqL2NWCm-OMCgrmPudu0OAY8q7hcOOd_k6B5DaEWB7yX5SzGurqCk6tPZKCbvfAmU8s4ZHFIIZcTytDefiM4UgRvNB_LANtRch8c2KJxWkA2lghmXlrm056glMAzqxKbmLWtbewN-ZFYV1RdrnxEUZOgliyboCFOAPz3NWhQU6F_G2O0mx9tLjnhHBuZe1QFXdEi_U-_Bg9ILFEiMLrb0cQZdXtD-CSaZUeQ3eQlyVWoHvWDNwmWLHIiGKYE2CVBrrLnmDUJBTMrF-wy5EnHOthXujQMS_g3eI7jv4-OmVq2uiugqCrQbdBXtnxeoW9i2clG5SE9K2Uulef4qgN9pKMPpdwnYcDuOhaS8swx3MPefOoKz84IgyeUf1nXCSyALkj0hJiDSW6LGICQrgWfkT6n_lDHqupzjj6IvVmrt-38Pop9q7SiCFXKALk0PC_qpV51zfi85_bacSDhlLyL68rO-1VPwk3H-ez-Sg8Mu8g8NAPVbtvMEHLcT"
    );
    presentation.setProof(proof);
    PresentationApi.verify(presentation, new VerifyContext(testDidStore));
  }


  @Test(expected = UnacceptableDocumentException.class)
  public void testNoMatchingVerificationMethod() throws Exception {
    // Verification method cannot be found
    List<VerificationMethod> list = decentralizedIdentifier1.getVerificationMethod();
    list.remove(0);
    decentralizedIdentifier1.setVerificationMethod(list);

    list = decentralizedIdentifier2.getVerificationMethod();
    list.remove(0);
    decentralizedIdentifier2.setVerificationMethod(list);

    PresentationApi.verify(presentation, new VerifyContext(testDidStore));
  }


  @Test(expected = UnacceptableDocumentException.class)
  public void testNoProof() throws Exception {
    presentation.setProof(null);
    PresentationApi.verify(presentation, new VerifyContext(testDidStore));
  }


  @Test(expected = UnacceptableDocumentException.class)
  public void testVerificationMethodIsWrongType() throws Exception {
    Proof proof = presentation.getProof();
    proof.set(
        "jws",
        "eyJhbGciOiJFZDQ0OCIsImI2NCI6ZmFsc2UsImNyaXQiOlsiYjY0Il19..Ac7z71gGqL2NWCm-OMCgrmPudu0OAY8q7hcOOd_k6B5DaEWB7yX5SzGurqCk6tPZKCbvfAmU8s4ZHFIIZcTytDefiM4UgRvNB_LANtRch8c2KJxWkA2lghmXlrm056glMAzqxKbmLWtbewN-ZFYV1RdrnxEUZOgliyboCFOAPz3NWhQU6F_G2O0mx9tLjnhHBuZe1QFXdEi_U-_Bg9ILFEiMLrb0cQZdXtD-CSaZUeQ3eQlyVWoHvWDNwmWLHIiGKYE2CVBrrLnmDUJBTMrF-wy5EnHOthXujQMS_g3eI7jv4-OmVq2uiugqCrQbdBXtnxeoW9i2clG5SE9K2Uulef4qgN9pKMPpdwnYcDuOhaS8swx3MPefOoKz84IgyeUf1nXCSyALkj0hJiDSW6LGICQrgWfkT6n_lDHqupzjj6IvVmrt-38Pop9q7SiCFXKALk0PC_qpV51zfi85_bacSDhlLyL68rO-1VPwk3H-ez-Sg8Mu8g8NAPVbtvMEHLct"
    );
    presentation.setProof(proof);
    PresentationApi.verify(presentation, new VerifyContext(testDidStore));
  }

}