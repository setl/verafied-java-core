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

package io.setl.verafied.revocation;

import java.net.URI;

/**
 * An abstraction of a credential revocation store.
 *
 * @author Simon Greatrix on 28/07/2020.
 */
public interface RevocationStore {

  /**
   * Clean-up this second, archiving all revocations that are strictly before the specified time. An implementation may allows some grace period to allow for
   * safe testing of credentials that are very close to their natural expiry.
   *
   * @param epochSecond the earliest time to retain in the store.
   */
  void cleanUp(long epochSecond);


  /**
   * Convert a credential's URI into a token that can be incorporated into a URL for verifying the status of that credential against this store. The returned
   * value will be safe to include as a URI path component or a query component without further escaping. Therefore, the characters used should be limited to
   * those matching the <code>pchar</code> grammar rule production defined in <a href="https://datatracker.ietf.org/doc/html/rfc3986>RFC-3986</a>.
   *
   * <p>The allowed characters are:</p>
   * <ul>
   *   <li>"ALPHA" characters: "A" to "Z" and "a" to "z"
   *   <li>"DIGIT" characters: "0" to "9"
   *   <li>Additional characters from the "unreserved" category: "-", ".", "_", "~"
   *   <li>The "sub-delims" category excluding "&", ";" and "=": "!" / "$" / "'" / "(" / ")" / "*" / "+" / ","
   * </ul>
   *
   * @param id the ID of the credential
   *
   * @return the token.
   */
  String getCredentialToken(URI id);


  /**
   * Convert an issuer's URI into a token that can be incorporated into a URL for verifying the status of a credential. The token should conform to the same
   * rules as {@link #getCredentialToken(URI)}.
   *
   * @param id the issuer's ID
   *
   * @return the token
   */
  String getIssuerToken(URI id);


  /**
   * Test if a token matches the rules for a token.
   *
   * @param token the alleged token
   *
   * @return true if the input matches
   */
  boolean isLegalToken(String token);


  /**
   * Insert a revocation into the store.
   *
   * @param issuer       the issuer of the credential that is being revoked
   * @param id           the ID of the credential that is being revoked.
   * @param expirySecond when the revocation expires. This should be the same time or after the credential expires.
   */
  default void revoke(URI issuer, URI id, long expirySecond) {
    revoke(getIssuerToken(issuer), getCredentialToken(id), expirySecond);
  }


  /**
   * Insert a revocation into the store.
   *
   * @param issuerToken  the issuer of the credential that is being revoked
   * @param idToken      the ID of the credential that is being revoked.
   * @param expirySecond when the revocation expires. This should be the same time or after the credential expires.
   */
  void revoke(String issuerToken, String idToken, long expirySecond);


  /**
   * Test if a credential has been revoked.
   *
   * @param issuer the token for the issuer of the credential that may have been revoked
   * @param id     the token for the ID of the credential that may have been revoked.
   *
   * @return true if the credential is revoked.
   */
  default boolean test(URI issuer, URI id) {
    return test(getIssuerToken(issuer), getCredentialToken(id));
  }


  /**
   * Test if a credential has been revoked.
   *
   * @param issuerToken the token for the issuer of the credential that may have been revoked
   * @param idToken     the token for the ID of the credential that may have been revoked.
   *
   * @return true if the credential is revoked.
   */
  public abstract boolean test(String issuerToken, String idToken);

}
