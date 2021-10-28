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

import java.util.concurrent.atomic.AtomicReference;

import io.setl.verafied.CredentialConstants;
import io.setl.verafied.did.DidStoreException;
import io.setl.verafied.proof.ProvableApi;
import io.setl.verafied.proof.VerifyContext;
import io.setl.verafied.proof.VerifyOutput;
import io.setl.verafied.proof.VerifyType;

/**
 * Utility methods related to signing and verifying a verifiable presentation.
 *
 * @author Simon Greatrix on 11/11/2020.
 */
public class PresentationApi {

  private static final String PRESENTATION = "Presentation";


  /**
   * Verify the signature on a presentation. Does not verify the embedded credentials.
   *
   * @param presentation  the presentation
   * @param verifyContext the context for the signature verification
   *
   * @return result of the verification
   */
  public static VerifyOutput verify(Presentation presentation, VerifyContext verifyContext) throws DidStoreException {
    AtomicReference<String> holder = new AtomicReference<>();
    boolean isOk =
        verifyType(presentation, holder)
            && verifyProof(presentation, verifyContext, holder);
    return isOk ? VerifyOutput.OK_CREDENTIAL : VerifyOutput.fail(holder.get(), VerifyType.CREDENTIAL);
  }


  /**
   * Verify the signature on a presentation. Does not verify the embedded credentials.
   *
   * @param presentation  the presentation
   * @param verifyContext the context for the signature verification
   *
   * @return result of the verification
   */
  private static boolean verifyProof(Presentation presentation, VerifyContext verifyContext, AtomicReference<String> holder) throws DidStoreException {
    return ProvableApi.verifyProof(presentation.getProof(), presentation, PRESENTATION, presentation.getId(), verifyContext, holder);

  }


  /**
   * Verify that this credential correctly declares its type as "VerifiablePresentation" and has the W3C context.
   *
   * @return true if OK
   */
  private static boolean verifyType(Presentation presentation, AtomicReference<String> holder) {
    return
        ProvableApi.verifyContext(presentation.getContext(), PRESENTATION, presentation.getId(), holder)
            && ProvableApi.verifyType(presentation.getType(), PRESENTATION, presentation.getId(), holder, CredentialConstants.VERIFIABLE_PRESENTATION_TYPE);
  }


  private PresentationApi() {
    // hidden as this is a utility class
  }

}
