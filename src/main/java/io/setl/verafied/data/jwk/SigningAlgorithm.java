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

package io.setl.verafied.data.jwk;

import static io.setl.verafied.CredentialConstants.logSafe;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.RSAKeyGenParameterSpec;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.bouncycastle.jcajce.spec.EdDSAParameterSpec;

import io.setl.verafied.CredentialConstants;

/**
 * See https://www.iana.org/assignments/jose/jose.xhtml#web-signature-encryption-algorithms
 *
 * @author Simon Greatrix on 02/07/2020.
 */
public enum SigningAlgorithm {

  RS256("RS256", "RSASSA-PKCS-v1_5 using SHA-256", KeyType.RSA,
      "RSA", new RSAKeyGenParameterSpec(3072, BigInteger.valueOf(0x10001)),
      "SHA256WITHRSA"
  ),
  RS384("RS384", "RSASSA-PKCS-v1_5 using SHA-384", KeyType.RSA,
      "RSA", new RSAKeyGenParameterSpec(7680, BigInteger.valueOf(0x10001)),
      "SHA384WITHRSA"
  ),
  RS512("RS512", "RSASSA-PKCS-v1_5 using SHA-512", KeyType.RSA,
      "RSA", new RSAKeyGenParameterSpec(15360, BigInteger.valueOf(0x10001)),
      "SHA512WITHRSA"
  ),
  PS256("PS256", "RSASSA-PSS using SHA-256 and MGF1 with SHA-256", KeyType.RSA,
      "RSA", new RSAKeyGenParameterSpec(3072, BigInteger.valueOf(0x10001)),
      "SHA256WITHRSAANDMGF1"
  ),
  PS384("PS384", "RSASSA-PSS using SHA-384 and MGF1 with SHA-384", KeyType.RSA,
      "RSA", new RSAKeyGenParameterSpec(7680, BigInteger.valueOf(0x10001)),
      "SHA384WITHRSAANDMGF1"
  ),
  PS512("PS512", "RSASSA-PSS using SHA-512 and MGF1 with SHA-512", KeyType.RSA,
      "RSA", new RSAKeyGenParameterSpec(15360, BigInteger.valueOf(0x10001)),
      "SHA512WITHRSAANDMGF1"
  ),
  ES256K("ES256K", "ECDSA using secp256k1 and SHA-256", KeyType.EC,
      "EC", new ECGenParameterSpec("secp256k1"),
      "SHA256WITHECDSA"
  ),
  ES256("ES256", "ECDSA using P-256 and SHA-256", KeyType.EC,
      "EC", new ECGenParameterSpec("secp256r1"),
      "SHA256WITHECDSA"
  ),
  ES384("ES384", "ECDSA using P-384 and SHA-384", KeyType.EC,
      "EC", new ECGenParameterSpec("secp384r1"),
      "SHA384WITHECDSA"
  ),
  ES512("ES512", "ECDSA using P-521 and SHA-512", KeyType.EC,
      "EC", new ECGenParameterSpec("secp521r1"),
      "SHA512WITHECDSA"
  ),
  ED25519("Ed25519", "EdDSA signature algorithm with Curve25519", KeyType.OKP,
      "ED25519", new EdDSAParameterSpec("Ed25519"),
      "ED25519"
  ),
  ED448("Ed448", "EdDSA signature algorithm with Curve448", KeyType.OKP,
      "ED448", new EdDSAParameterSpec("Ed448"),
      "ED448"
  ),
  NONE("NONE", "An invalid or deleted entry", null, null, null, null);


  /**
   * Get the signing algorithm as identified by its JWK name.
   *
   * @param jwkName the JWK name
   *
   * @return the intance
   *
   * @throws IllegalArgumentException if the JWK name is not recognised
   */
  @JsonCreator
  public static SigningAlgorithm get(String jwkName) {
    for (SigningAlgorithm algorithm : SigningAlgorithm.values()) {
      if (algorithm.getJwkName().equalsIgnoreCase(jwkName)) {
        return algorithm;
      }
    }
    throw new IllegalArgumentException("Unknown signature algorithm: " + logSafe(jwkName));
  }

  static {
    CredentialConstants.initialise();
  }

  private final String description;

  private final String generatorName;

  private final AlgorithmParameterSpec generatorSpec;

  private final String jwkName;

  private final KeyType keyType;

  private final String signingName;


  SigningAlgorithm(
      String jwkName, String description, KeyType keyType,
      String generatorName, AlgorithmParameterSpec generatorSpec,
      String signingName
  ) {
    this.jwkName = jwkName;
    this.description = description;
    this.keyType = keyType;
    this.generatorName = generatorName;
    this.generatorSpec = generatorSpec;
    this.signingName = signingName;
  }


  /**
   * Create a key pair appropriate to this signing algorithm.
   *
   * @return a new key pair
   */
  public KeyPair createKeyPair() {
    try {
      KeyPairGenerator generator = KeyPairGenerator.getInstance(generatorName);
      generator.initialize(generatorSpec, CredentialConstants.getSecureRandom());
      return generator.generateKeyPair();
    } catch (GeneralSecurityException e) {
      // The name and spec are hard-coded, so should never fail
      throw new InternalError("Cryptographic failure", e);
    }
  }


  /**
   * Create a Signature instance to sign or verify a signature for this algorithm.
   *
   * @return the Signature instance
   */
  public Signature createSignature() {
    try {
      return Signature.getInstance(signingName);
    } catch (NoSuchAlgorithmException e) {
      // The signing name is hard-coded, so should never fail
      throw new InternalError("Cryptographic failure", e);
    }
  }


  public String getDescription() {
    return description;
  }


  public String getGeneratorName() {
    return generatorName;
  }


  public AlgorithmParameterSpec getGeneratorSpec() {
    return generatorSpec;
  }


  @JsonValue
  public String getJwkName() {
    return jwkName;
  }


  public KeyType getKeyType() {
    return keyType;
  }


  public String getSigningName() {
    return signingName;
  }

}
