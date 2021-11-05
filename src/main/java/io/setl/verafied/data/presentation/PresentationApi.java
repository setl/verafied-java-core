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

package io.setl.verafied.data.presentation;

import java.security.GeneralSecurityException;

import io.setl.verafied.CredentialConstants;
import io.setl.verafied.UnacceptableDocumentException;
import io.setl.verafied.data.TypedKeyPair;
import io.setl.verafied.did.DidStoreException;
import io.setl.verafied.proof.ProofContext;
import io.setl.verafied.proof.ProvableApi;
import io.setl.verafied.proof.VerifyContext;

/**
 * Utility methods related to signing and verifying a verifiable presentation.
 *
 * @author Simon Greatrix on 11/11/2020.
 */
public class PresentationApi {

  private static final String PRESENTATION = "Presentation";


  /**
   * Attach a proof to a presentation.
   *
   * @param proofContext the proof context
   * @param presentation the presentation to attach a proof to
   * @param keyPair      the key pair to sign the credential with
   *
   * @throws GeneralSecurityException if a cryptographic failure occurs
   */
  public static void prove(
      ProofContext proofContext,
      Presentation presentation,
      TypedKeyPair keyPair
  ) throws GeneralSecurityException, UnacceptableDocumentException {
    verifyType(presentation);

    proofContext.getProver().attachProof(proofContext, presentation, keyPair);
  }


  /**
   * Verify the signature on a presentation. Does not verify the embedded credentials.
   *
   * @param presentation  the presentation
   * @param verifyContext the context for the signature verification
   *
   * @throws UnacceptableDocumentException if the document does not verify
   * @throws DidStoreException             if the signing DID cannot be retrieved
   */
  public static void verify(Presentation presentation, VerifyContext verifyContext) throws DidStoreException, UnacceptableDocumentException {
    verifyType(presentation);
    verifyProof(presentation, verifyContext);
  }


  /**
   * Verify the signature on a presentation. Does not verify the embedded credentials.
   *
   * @param presentation  the presentation
   * @param verifyContext the context for the signature verification
   */
  private static void verifyProof(Presentation presentation, VerifyContext verifyContext) throws DidStoreException, UnacceptableDocumentException {
    ProvableApi.verifyProof(presentation.getProof(), presentation, PRESENTATION, presentation.getId(), verifyContext);
  }


  /**
   * Verify that this credential correctly declares its type as "VerifiablePresentation" and has the W3C context.
   */
  private static void verifyType(Presentation presentation) throws UnacceptableDocumentException {
    ProvableApi.verifyContext(presentation.getContext(), PRESENTATION, presentation.getId());
    ProvableApi.verifyType(presentation.getType(), PRESENTATION, presentation.getId(), CredentialConstants.VERIFIABLE_PRESENTATION_TYPE);
  }


  private PresentationApi() {
    // hidden as this is a utility class
  }

}
