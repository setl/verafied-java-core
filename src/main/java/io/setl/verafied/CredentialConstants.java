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

package io.setl.verafied;


import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.time.Clock;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import javax.json.spi.JsonProvider;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * @author Simon Greatrix on 14/10/2020.
 */
public class CredentialConstants {

  /** The required context for a verifiable credential. */
  public static final String CREDENTIAL_CONTEXT = "https://www.w3.org/2018/credentials/v1";

  /** Name of the SETL revocation mechanism. */
  public static final String HTTP_STATUS_CHECK = "HttpStatusCheck";

  /** JSON API provider. */
  public static final JsonProvider JSON_PROVIDER = JsonProvider.provider();

  /** The type used to identify the Verifiable Credential container. */
  public static final String VERIFIABLE_CREDENTIAL_TYPE = "VerifiableCredential";

  /** The type used to identify the Verifiable Presentation container. */
  public static final String VERIFIABLE_PRESENTATION_TYPE = "VerifiablePresentation";

  private static final AtomicReference<SecureRandom> SECURE_RANDOM = new AtomicReference<>();

  /** Clock for calculating current time. This is settable for testing. */
  private static Clock clock = Clock.systemUTC();

  private static Function<String, String> logSafe = Function.identity();


  /**
   * Get the clock used to generate timestamps. By default, this will be the system UTC clock.
   *
   * @return the clock
   */
  public static Clock getClock() {
    return clock;
  }


  /**
   * Get the function used to render externally supplied text safe for use in log files.
   *
   * @return the function
   *
   * @see #logSafe(String)
   */
  public static Function<String, String> getLogSafe() {
    return logSafe;
  }


  public static SecureRandom getSecureRandom() {
    SecureRandom sr = SECURE_RANDOM.get();
    if (sr == null) {
      try {
        sr = SecureRandom.getInstanceStrong();
      } catch (NoSuchAlgorithmException e) {
        sr = new SecureRandom();
      }
      SECURE_RANDOM.compareAndSet(null, sr);
    }

    return sr;
  }


  /**
   * Initialise the constants.
   */
  public static void initialise() {
    // do nothing - we are simply ensuring the static initialisation block has run
  }


  /**
   * Apply the function that makes external text safe for inclusion in error messages and log files.
   *
   * @param text the untrusted text
   *
   * @return the text but made safe
   */
  public static String logSafe(String text) {
    return logSafe.apply(text);
  }


  /**
   * Set the clock.
   *
   * @param clock the new clock
   */
  public static void setClock(Clock clock) {
    CredentialConstants.clock = Objects.requireNonNull(clock);
  }


  /**
   * Set the function to make external text safe.
   *
   * @param logSafe the function
   */
  public static void setLogSafe(Function<String, String> logSafe) {
    CredentialConstants.logSafe = Objects.requireNonNull(logSafe);
  }


  /**
   * Set the secure random number generator used in creating signatures.
   *
   * @param secureRandom the random number generator
   */
  public static void setSecureRandom(SecureRandom secureRandom) {
    SECURE_RANDOM.set(Objects.requireNonNull(secureRandom));
  }


  static {
    // Ensure the bouncy castle provider is available.
    Provider provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
    if (provider == null) {
      Security.addProvider(new BouncyCastleProvider());
    }
  }
}
