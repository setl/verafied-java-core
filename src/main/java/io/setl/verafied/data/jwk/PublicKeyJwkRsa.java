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

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Objects;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Representation of a JSON Web Key that uses a Rivest-Shamir-Adelman key.
 *
 * @author Simon Greatrix on 27/06/2020.
 */
public class PublicKeyJwkRsa extends PublicKeyJwk {

  private static String toBase64(BigInteger value) {
    byte[] bytes = value.toByteArray();
    if (value.bitLength() % 8 == 0 && bytes.length > 1 && bytes[0] == 0) {
      // Value is too long as it has a sign bit
      byte[] tmp = new byte[bytes.length - 1];
      System.arraycopy(bytes, 1, tmp, 0, tmp.length);
      bytes = tmp;
    }

    return Base64.getUrlEncoder().encodeToString(bytes);
  }


  private String exponent;

  private String modulus;


  /**
   * New instance.
   *
   * @param publicKey the RSA public key
   */
  public PublicKeyJwkRsa(PublicKey publicKey) {
    try {
      KeyFactory generator = KeyFactory.getInstance("RSA");
      RSAPublicKeySpec spec = generator.getKeySpec(publicKey, RSAPublicKeySpec.class);

      exponent = toBase64(spec.getPublicExponent());
      modulus = toBase64(spec.getModulus());
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new InternalError("RSA support is required");
    }
  }


  public PublicKeyJwkRsa() {
    // do nothing
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof PublicKeyJwkRsa)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    PublicKeyJwkRsa that = (PublicKeyJwkRsa) o;
    return exponent.equals(that.exponent) && modulus.equals(that.modulus);
  }


  @JsonProperty("e")
  public String getExponent() {
    return exponent;
  }


  @NotNull
  public KeyType getKeyType() {
    return KeyType.RSA;
  }


  @JsonProperty("n")
  public String getModulus() {
    return modulus;
  }


  @JsonIgnore
  @Override
  public PublicKey getPublicKey() throws InvalidKeySpecException {
    RSAPublicKeySpec spec = new RSAPublicKeySpec(
        new BigInteger(1, Base64.getUrlDecoder().decode(modulus)),
        new BigInteger(1, Base64.getUrlDecoder().decode(exponent))
    );

    try {
      return KeyFactory.getInstance("RSA").generatePublic(spec);
    } catch (NoSuchAlgorithmException e) {
      throw new InternalError("RSA support is required");
    }
  }


  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), exponent, modulus);
  }


  public void setExponent(String exponent) {
    this.exponent = exponent;
  }


  public void setModulus(String modulus) {
    this.modulus = modulus;
  }

}
