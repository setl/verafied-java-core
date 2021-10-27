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
import java.util.Base64;
import java.util.Map;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECPoint;

/**
 * @author Simon Greatrix on 27/06/2020.
 */
public class PublicKeyJwkEc extends PublicKeyJwk {

  private static final Map<String, ECNamedCurveParameterSpec> STANDARD_CURVES = Map.of(
      "P-256", ECNamedCurveTable.getParameterSpec("P-256"),
      "P-384", ECNamedCurveTable.getParameterSpec("P-384"),
      "P-521", ECNamedCurveTable.getParameterSpec("P-521"),
      "secp256k1", ECNamedCurveTable.getParameterSpec("secp256k1")
  );


  private static String toBase64(BigInteger value, int size) {
    byte[] bytes = value.toByteArray();
    if (bytes.length < size) {
      // Value is too short as it has a leading zero.
      byte[] tmp = new byte[size];
      int offset = size - bytes.length;
      System.arraycopy(bytes, 0, tmp, offset, bytes.length);
      bytes = tmp;
    } else if (bytes.length > size) {
      // Value is too long as it has a sign bit
      byte[] tmp = new byte[size];
      int offset = bytes.length - size;
      System.arraycopy(bytes, offset, tmp, 0, size);
      bytes = tmp;
    }

    return Base64.getUrlEncoder().encodeToString(bytes);
  }


  private String curve;

  private String x;

  private String y;


  public PublicKeyJwkEc() {
    // do nothing
  }


  /**
   * New instance.
   *
   * @param curve     the name of the elliptic curve
   * @param publicKey the public key
   */
  public PublicKeyJwkEc(String curve, PublicKey publicKey) {
    this.curve = curve;
    try {
      KeyFactory generator = KeyFactory.getInstance("EC");
      ECPublicKeySpec spec = generator.getKeySpec(publicKey, ECPublicKeySpec.class);
      ECPoint point = spec.getQ();
      int keySize = (point.getCurve().getFieldSize() + 7) / 8;
      x = toBase64(point.getAffineXCoord().toBigInteger(), keySize);
      y = toBase64(point.getAffineYCoord().toBigInteger(), keySize);
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new InternalError("Elliptic Curve support is required");
    }
  }


  @JsonProperty("crv")
  public String getCurve() {
    return curve;
  }


  @NotNull
  public KeyType getKeyType() {
    return KeyType.EC;
  }


  @JsonIgnore
  @Override
  public PublicKey getPublicKey() throws InvalidKeySpecException {
    ECNamedCurveParameterSpec params = STANDARD_CURVES.get(curve);
    ECPoint point = params.getCurve().createPoint(
        new BigInteger(1, Base64.getUrlDecoder().decode(x)),
        new BigInteger(1, Base64.getUrlDecoder().decode(y))
    );
    ECPublicKeySpec spec = new ECPublicKeySpec(point, params);
    try {
      return KeyFactory.getInstance("EC").generatePublic(spec);
    } catch (NoSuchAlgorithmException e) {
      throw new InternalError("Elliptic Curve support is required");
    }
  }


  @JsonProperty("x")
  public String getX() {
    return x;
  }


  @JsonProperty("y")
  public String getY() {
    return y;
  }


  public void setCurve(String curve) {
    this.curve = curve;
  }


  public void setX(String x) {
    this.x = x;
  }


  public void setY(String y) {
    this.y = y;
  }

}
