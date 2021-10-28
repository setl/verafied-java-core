package io.setl.verafied.data.credential;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Set;
import javax.json.Json;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import io.setl.verafied.CredentialConstants;
import io.setl.verafied.data.Proof;

/**
 * @author Simon Greatrix on 28/10/2021.
 */
public class CredentialTest {

  @BeforeClass
  public static void fixClock() {
    CredentialConstants.setClock(Clock.fixed(Instant.ofEpochSecond(1635432538L), ZoneId.of("Canada/Eastern")));
  }


  @AfterClass
  public static void restoreClock() {
    CredentialConstants.setClock(Clock.systemUTC());
  }


  @Test(expected = IllegalStateException.class)
  public void cantSetWhenProved() {
    Credential credential = new Credential();
    credential.setProof(new Proof());
    credential.setId(URI.create("ref:abc"));
  }


  @Test
  public void create() {
    Credential credential = new Credential();
    credential.setIssuer(URI.create("did:test:1234"));
    credential.setIssuanceDate(Instant.ofEpochSecond(1635440000L));
    credential.setExpirationDate(Instant.ofEpochSecond(1635450000L));
    credential.setCredentialSubject(Json.createObjectBuilder().add("x", 1).build());

    assertEquals(
        "{\"@context\":[\"https://www.w3.org/2018/credentials/v1\"],\"credentialSubject\":{\"x\":1},\"expirationDate\":\"2021-10-28T19:40:00Z\",\"issuanceDate\":\"2021-10-28T16:53:20Z\",\"issuer\":\"did:test:1234\",\"type\":[\"VerifiableCredential\"]}",
        credential.asJson().toString()
    );
  }


  @Test(expected = IllegalArgumentException.class)
  public void setBadType() {
    Credential credential = new Credential();
    credential.setType(Set.of("Boot"));
  }


  @Test
  public void setType() {
    Credential credential = new Credential();
    assertEquals(credential.getType(), Set.of(CredentialConstants.VERIFIABLE_CREDENTIAL_TYPE));

    credential.setType(null);
    assertEquals(credential.getType(), Set.of(CredentialConstants.VERIFIABLE_CREDENTIAL_TYPE));

    credential.setType(Set.of());
    assertEquals(credential.getType(), Set.of(CredentialConstants.VERIFIABLE_CREDENTIAL_TYPE));

    credential.setType(Set.of(CredentialConstants.VERIFIABLE_CREDENTIAL_TYPE, "Boot"));
    assertEquals(credential.getType(), Set.of(CredentialConstants.VERIFIABLE_CREDENTIAL_TYPE, "Boot"));
  }

}