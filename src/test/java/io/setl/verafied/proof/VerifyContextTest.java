package io.setl.verafied.proof;

import java.net.URI;

import org.junit.Test;

import io.setl.verafied.UnacceptableDocumentException;
import io.setl.verafied.data.Proof;
import io.setl.verafied.data.credential.TestDidStore;
import io.setl.verafied.did.DidStoreException;

/**
 * @author Simon Greatrix on 05/11/2021.
 */
public class VerifyContextTest {

  TestDidStore didStore = new TestDidStore();

  VerifyContext context = new VerifyContext(didStore);


  @Test(expected = IllegalStateException.class)
  public void algorithmNotSet() {
    context.getAlgorithm();
  }


  @Test(expected = UnacceptableDocumentException.class)
  public void badVerificationMethod() throws DidStoreException, UnacceptableDocumentException {
    Proof proof = new Proof();
    proof.setVerificationMethod(URI.create("ftp://localhost"));
    context.findVerificationMethod(proof);
  }


  @Test(expected = IllegalStateException.class)
  public void methodNotSet() {
    context.getVerificationMethod();
  }


  @Test(expected = UnacceptableDocumentException.class)
  public void noVerificationMethod() throws DidStoreException, UnacceptableDocumentException {
    Proof proof = new Proof();
    context.findVerificationMethod(proof);
  }


  @Test(expected = IllegalStateException.class)
  public void signatureNotSet() {
    context.getAllegedSignature();
  }

}