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

import java.security.PublicKey;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

/**
 * @author Simon Greatrix on 27/06/2020.
 */
public class PublicKeyJwkFactory {

  private static volatile Map<ASN1ObjectIdentifier, BiFunction<PublicKey, SubjectPublicKeyInfo, PublicKeyJwk>> MAKERS;


  /**
   * New instance for a public key.
   *
   * @param publicKey the public key
   *
   * @return the JWK representation
   */
  public static PublicKeyJwk from(PublicKey publicKey) {
    byte[] encoded = publicKey.getEncoded();
    SubjectPublicKeyInfo pki = SubjectPublicKeyInfo.getInstance(encoded);
    AlgorithmIdentifier algorithmIdentifier = pki.getAlgorithm();
    ASN1ObjectIdentifier algorithmOID = algorithmIdentifier.getAlgorithm();

    BiFunction<PublicKey, SubjectPublicKeyInfo, PublicKeyJwk> maker = MAKERS.get(algorithmOID);
    if (maker == null) {
      // Try to match via a parameter. This is required for Elliptic Curves.
      ASN1Encodable parameter = algorithmIdentifier.getParameters();
      if (parameter instanceof ASN1ObjectIdentifier) {
        maker = MAKERS.get(parameter);
      }
    }

    if (maker != null) {
      return maker.apply(publicKey, pki);
    }

    throw new IllegalArgumentException("Unknown algorithm identifier: " + algorithmOID);
  }


  /**
   * Register a new factory for converting a public key to a <code>PublicKeyJwk</code> instance based upon its ASN1 Object Identifier. Note that if there is no
   * match on the algorithm itself, and the algorithm specifies an OID as its single parameter, then a second match is attempted on that parameter value. This
   * is how elliptic curves are matched.
   *
   * @param oid   the OID to match
   * @param maker the function that converts either the public key, or the subject public key info, to a suitable instance.
   */
  public static synchronized void setFactory(ASN1ObjectIdentifier oid, BiFunction<PublicKey, SubjectPublicKeyInfo, PublicKeyJwk> maker) {
    HashMap<ASN1ObjectIdentifier, BiFunction<PublicKey, SubjectPublicKeyInfo, PublicKeyJwk>> map = new HashMap<>(MAKERS);
    map.put(oid, maker);
    MAKERS = Collections.unmodifiableMap(map);
  }


  static {
    HashMap<ASN1ObjectIdentifier, BiFunction<PublicKey, SubjectPublicKeyInfo, PublicKeyJwk>> map = new HashMap<>();
    map.put(EdECObjectIdentifiers.id_Ed25519, (pk, spki) -> new PublicKeyJwkOkp("Ed25519", spki));
    map.put(EdECObjectIdentifiers.id_Ed448, (pk, spki) -> new PublicKeyJwkOkp("Ed448", spki));
    map.put(PKCSObjectIdentifiers.rsaEncryption, (pk, spki) -> new PublicKeyJwkRsa(pk));
    map.put(SECObjectIdentifiers.secp256r1, (pk, spki) -> new PublicKeyJwkEc("P-256", pk));
    map.put(SECObjectIdentifiers.secp256k1, (pk, spki) -> new PublicKeyJwkEc("secp256k1", pk));
    map.put(SECObjectIdentifiers.secp384r1, (pk, spki) -> new PublicKeyJwkEc("P-384", pk));
    map.put(SECObjectIdentifiers.secp521r1, (pk, spki) -> new PublicKeyJwkEc("P-521", pk));

    MAKERS = Collections.unmodifiableMap(map);
  }

}
