package io.setl.verafied.proof;

import java.security.GeneralSecurityException;
import javax.json.JsonValue;

import org.junit.Test;

import io.setl.verafied.UnacceptableDocumentException;
import io.setl.verafied.data.Proof;
import io.setl.verafied.data.credential.TestDidStore;
import io.setl.verafied.did.DidStoreException;

/**
 * @author Simon Greatrix on 04/11/2021.
 */
public class CanonicalJsonWithJwsTest {

  @Test(expected = UnacceptableDocumentException.class)
  public void testBadHeaderB64() throws GeneralSecurityException, DidStoreException, UnacceptableDocumentException {
    Proof proof = new Proof();
    proof.setType("CanonicalJsonWithJws");
    proof.set("jws", "!!!!..abcd");
    CanonicalJsonWithJws jws = new CanonicalJsonWithJws();
    jws.verifyProof(new VerifyContext(new TestDidStore()), JsonValue.EMPTY_JSON_OBJECT, proof);
  }


  @Test(expected = UnacceptableDocumentException.class)
  public void testBadHeaderBadAlg1() throws GeneralSecurityException, DidStoreException, UnacceptableDocumentException {
    Proof proof = new Proof();
    proof.setType("CanonicalJsonWithJws");
    proof.set("jws", "eyJiNjQiOmZhbHNlfQ..SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c");
    CanonicalJsonWithJws jws = new CanonicalJsonWithJws();
    jws.verifyProof(new VerifyContext(new TestDidStore()), JsonValue.EMPTY_JSON_OBJECT, proof);
  }


  @Test(expected = UnacceptableDocumentException.class)
  public void testBadHeaderBadAlg2() throws GeneralSecurityException, DidStoreException, UnacceptableDocumentException {
    Proof proof = new Proof();
    proof.setType("CanonicalJsonWithJws");
    proof.set("jws", "eyJhbGciOiJIUzI1NiIsImI2NCI6ZmFsc2V9..SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c");
    CanonicalJsonWithJws jws = new CanonicalJsonWithJws();
    jws.verifyProof(new VerifyContext(new TestDidStore()), JsonValue.EMPTY_JSON_OBJECT, proof);
  }


  @Test(expected = UnacceptableDocumentException.class)

  public void testBadHeaderBadAlg3() throws GeneralSecurityException, DidStoreException, UnacceptableDocumentException {
    Proof proof = new Proof();
    proof.setType("CanonicalJsonWithJws");
    proof.set("jws", "eyJiNjQiOmZhbHNlLCJhbGciOiJOT05FIn0..SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c");
    CanonicalJsonWithJws jws = new CanonicalJsonWithJws();
    jws.verifyProof(new VerifyContext(new TestDidStore()), JsonValue.EMPTY_JSON_OBJECT, proof);
  }


  @Test(expected = UnacceptableDocumentException.class)
  public void testBadHeaderJson() throws GeneralSecurityException, DidStoreException, UnacceptableDocumentException {
    Proof proof = new Proof();
    proof.setType("CanonicalJsonWithJws");
    proof.set("jws", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpX..SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c");
    CanonicalJsonWithJws jws = new CanonicalJsonWithJws();
    jws.verifyProof(new VerifyContext(new TestDidStore()), JsonValue.EMPTY_JSON_OBJECT, proof);
  }


  @Test(expected = UnacceptableDocumentException.class)
  public void testBadHeaderNoB4() throws GeneralSecurityException, DidStoreException, UnacceptableDocumentException {
    Proof proof = new Proof();
    proof.setType("CanonicalJsonWithJws");
    proof.set("jws", "eyJhbGciOiJIUzI1NiIsImI2NCI6MX0..SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c");
    CanonicalJsonWithJws jws = new CanonicalJsonWithJws();
    jws.verifyProof(new VerifyContext(new TestDidStore()), JsonValue.EMPTY_JSON_OBJECT, proof);
  }


  @Test(expected = UnacceptableDocumentException.class)
  public void testBadSignatureB64() throws GeneralSecurityException, DidStoreException, UnacceptableDocumentException {
    Proof proof = new Proof();
    proof.setType("CanonicalJsonWithJws");
    proof.set("jws", "eyJiNjQiOmZhbHNlLCJhbGciOiJFUzI1NiJ9..!!!!");
    CanonicalJsonWithJws jws = new CanonicalJsonWithJws();
    jws.verifyProof(new VerifyContext(new TestDidStore()), JsonValue.EMPTY_JSON_OBJECT, proof);
  }


  @Test(expected = UnacceptableDocumentException.class)
  public void testNoDotDot() throws GeneralSecurityException, DidStoreException, UnacceptableDocumentException {
    Proof proof = new Proof();
    proof.setType("CanonicalJsonWithJws");
    proof.set("jws", "a.b.c");
    CanonicalJsonWithJws jws = new CanonicalJsonWithJws();
    jws.verifyProof(new VerifyContext(new TestDidStore()), JsonValue.EMPTY_JSON_OBJECT, proof);
  }


  @Test(expected = UnacceptableDocumentException.class)
  public void testNoJws() throws GeneralSecurityException, DidStoreException, UnacceptableDocumentException {
    Proof proof = new Proof();
    proof.setType("CanonicalJsonWithJws");
    CanonicalJsonWithJws jws = new CanonicalJsonWithJws();
    jws.verifyProof(new VerifyContext(new TestDidStore()), JsonValue.EMPTY_JSON_OBJECT, proof);
  }


  @Test(expected = UnacceptableDocumentException.class)
  public void testTypeMisMatch() throws GeneralSecurityException, DidStoreException, UnacceptableDocumentException {
    Proof proof = new Proof();
    proof.setType("SomeWeirdThing");
    CanonicalJsonWithJws jws = new CanonicalJsonWithJws();
    jws.verifyProof(new VerifyContext(new TestDidStore()), JsonValue.EMPTY_JSON_OBJECT, proof);
  }

}