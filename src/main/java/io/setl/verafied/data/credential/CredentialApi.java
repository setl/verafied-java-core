/* <notice>
 *
 *   SETL Blockchain
 *   Copyright (C) 2021 SETL Ltd
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License, version 3, as
 *   published by the Free Software Foundation.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * </notice>
 */

package io.setl.verafied.data.credential;

import static io.setl.verafied.CredentialConstants.logSafe;

import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.setl.verafied.CredentialConstants;
import io.setl.verafied.data.TypedKeyPair;
import io.setl.verafied.did.DidStoreException;
import io.setl.verafied.proof.ProofContext;
import io.setl.verafied.proof.ProvableApi;
import io.setl.verafied.proof.VerifyContext;
import io.setl.verafied.proof.VerifyOutput;
import io.setl.verafied.proof.VerifyType;
import io.setl.verafied.revocation.RevocationChecker;

/**
 * Verification of credentials.
 *
 * @author Simon Greatrix on 28/10/2020.
 */
public class CredentialApi {

  private static final String CREDENTIAL = "Credential";

  private static final Logger logger = LoggerFactory.getLogger(CredentialApi.class);


  /**
   * Attach a proof to a credential. The credential must have an ID.
   *
   * @param proofContext the proof context
   * @param credential   the credential to attach a proof to
   * @param keyPair      the key pair to sign the credential with
   *
   * @throws GeneralSecurityException if a cryptographic failure occurs
   */
  public static void prove(
      ProofContext proofContext,
      Credential credential,
      TypedKeyPair keyPair
  ) throws GeneralSecurityException {
    if (credential.getId() == null) {
      throw new IllegalArgumentException("Credential will not be signed as it has no ID");
    }
    AtomicReference<String> holder = new AtomicReference<>();
    if (!(verifyType(credential, holder) && verifyDates(credential, holder))) {
      throw new IllegalArgumentException("Credential cannot be signed as it is not valid: " + holder.get());
    }

    proofContext.getProver().attachProof(proofContext, credential, keyPair);
  }


  /**
   * Verify if this is a valid credential. This checks the following:
   * <ol>
   *   <li>It has the required JSON-LD context as the primary context.</li>
   *   <li>It lists VerifiableCredential as one of its types.</li>
   *   <li>It has not expired.</li>
   *   <li>It has not been revoked.</li>
   *   <li>It has a valid proof.</li>
   * </ol>
   */
  public static VerifyOutput verify(Credential credential, VerifyContext context, RevocationChecker revocationStore) throws DidStoreException {
    AtomicReference<String> holder = new AtomicReference<>();
    boolean isOk =
        verifyType(credential, holder)
            && verifyDates(credential, holder)
            && verifyStatus(credential, revocationStore, holder)
            && verifyProof(credential, context, holder);
    return isOk ? VerifyOutput.OK_CREDENTIAL : VerifyOutput.fail(holder.get(), VerifyType.CREDENTIAL);
  }


  /**
   * Verify that the issuance date is in the past and the expiration date is in the future.
   *
   * @return true if OK
   */
  private static boolean verifyDates(Credential credential, AtomicReference<String> holder) {
    Instant atTime = CredentialConstants.getClock().instant();

    if (credential.getExpirationDate() != null && credential.getExpirationDate().isBefore(atTime)) {
      // expiration date is before now, so expired
      String message = String.format("Credential %s NOT verified as it expired at %s and it is now %s",
          logSafe(credential.getId().toString()), credential.getExpirationDate(), atTime
      );
      holder.set(message);
      logger.debug(message);
      return false;
    }

    if (credential.getIssuanceDate() != null && credential.getIssuanceDate().isAfter(atTime)) {
      // issuance is after now, so not yet issued
      String message = String.format("Credential %s NOT verified as it will not be issued until %s and it is now %s",
          logSafe(credential.getId().toString()), credential.getIssuanceDate(), atTime
      );
      holder.set(message);
      logger.debug(message);
      return false;
    }

    return true;
  }


  /**
   * Verify that the cryptographic proof for this is correct.
   *
   * @return true if OK
   */
  private static boolean verifyProof(Credential credential, VerifyContext verifyContext, AtomicReference<String> holder) throws DidStoreException {
    return ProvableApi.verifyProof(credential.getProof(), credential, CREDENTIAL, credential.getId(), verifyContext, holder);
  }


  /**
   * Verify that this credential has not been revoked.
   *
   * @param revocationStore the revocation store.
   *
   * @return true if OK
   */
  private static boolean verifyStatus(Credential credential, RevocationChecker revocationStore, AtomicReference<String> holder) {
    if (credential.getCredentialStatus() != null && revocationStore != null
        && revocationStore.test(credential.getCredentialStatus().getType(), credential.getIssuer(), credential.getId())
    ) {
      // has been revoked
      String message = String.format("Credential %s NOT verified as it has been revoked", logSafe(credential.getId().toString()));
      holder.set(message);
      logger.debug(message);
      return false;
    }

    return true;
  }


  /**
   * Verify that this credential correctly declares its type as "VerifiableCredential" and has the W3C context.
   *
   * @return true if OK
   */
  private static boolean verifyType(Credential credential, AtomicReference<String> holder) {
    return
        ProvableApi.verifyContext(credential.getContext(), CREDENTIAL, credential.getId(), holder)
            && ProvableApi.verifyType(credential.getType(), CREDENTIAL, credential.getId(), holder, CredentialConstants.VERIFIABLE_CREDENTIAL_TYPE);
  }


  private CredentialApi() {
    // Hidden as this is a utility class.
  }

}
