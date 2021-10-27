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

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.Hidden;
import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

/**
 * @author Simon Greatrix on 27/06/2020.
 */
public class PublicKeyJwkOkp extends PublicKeyJwk {

  private String curve;

  private String x;


  public PublicKeyJwkOkp(String curve, SubjectPublicKeyInfo pki) {
    this.curve = curve;
    x = Base64.getUrlEncoder().encodeToString(pki.getPublicKeyData().getOctets());
  }


  public PublicKeyJwkOkp(String curve, byte[] octets) {
    this.curve = curve;
    x = Base64.getUrlEncoder().encodeToString(octets);
  }


  public PublicKeyJwkOkp() {
    // do nothing
  }


  @JsonProperty("crv")
  public String getCurve() {
    return curve;
  }


  @JsonIgnore
  @Hidden
  @NotNull
  public KeyType getKeyType() {
    return KeyType.OKP;
  }


  @Override
  public PublicKey getPublicKey() throws InvalidKeySpecException {
    byte[] octets = Base64.getUrlDecoder().decode(x);
    try {
      if ("Ed25519".equals(curve)) {
        SubjectPublicKeyInfo info = new SubjectPublicKeyInfo(new AlgorithmIdentifier(EdECObjectIdentifiers.id_Ed25519), octets);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(info.getEncoded());
        KeyFactory factory = KeyFactory.getInstance("Ed25519");
        return factory.generatePublic(spec);
      }

      if ("Ed448".equals(curve)) {
        SubjectPublicKeyInfo info = new SubjectPublicKeyInfo(new AlgorithmIdentifier(EdECObjectIdentifiers.id_Ed448), octets);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(info.getEncoded());
        KeyFactory factory = KeyFactory.getInstance("Ed448");
        return factory.generatePublic(spec);
      }
    } catch (IOException ioe) {
      throw new InvalidKeySpecException();
    } catch (NoSuchAlgorithmException e) {
      throw new InvalidKeySpecException("Unknown curve: " + curve, e);
    }

    throw new InvalidKeySpecException("Unknown curve: " + logSafe(curve));
  }


  @JsonProperty("x")
  public String getX() {
    return x;
  }


  public void setCurve(String curve) {
    this.curve = curve;
  }


  public void setX(String x) {
    this.x = x;
  }

}
