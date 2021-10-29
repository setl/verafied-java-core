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

package io.setl.verafied.proof;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonPointer;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.JsonWriter;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import io.setl.json.CJObject;
import io.setl.verafied.CredentialConstants;
import io.setl.verafied.UnacceptableDocumentException;
import io.setl.verafied.data.Proof;
import io.setl.verafied.data.Provable;
import io.setl.verafied.data.TypedKeyPair;
import io.setl.verafied.data.jwk.SigningAlgorithm;
import io.setl.verafied.did.DidStoreException;

/**
 * Implementation of the "Canonical JSON with JWS" signature method.
 *
 * @author Simon Greatrix on 02/10/2020.
 */
public class CanonicalJsonWithJws implements Prover {

  private static final JsonPointer JWS_POINTER = CredentialConstants.JSON_PROVIDER.createPointer("/proof/jws");


  @Override
  public void attachProof(ProofContext context, Provable input, TypedKeyPair keyPair) throws GeneralSecurityException {
    Proof proof = input.getProof();
    if (proof == null) {
      proof = new Proof();
    }

    // Set the standard proof fields
    proof.remove("jws");
    proof.setType("CanonicalJsonWithJws");
    proof.setCreated(Instant.now().truncatedTo(ChronoUnit.SECONDS));
    proof.setVerificationMethod(context.getDidWithKey().getUri());
    proof.set("salt", JsonSalt.create());
    input.setProof(proof);

    JsonObject toSign = input.asJson();

    // Create the JWS header. See RFC 7797 for how the bytes-to-sign is defined.
    JsonObjectBuilder headerBuilder = Json.createObjectBuilder();
    headerBuilder.add("alg", keyPair.getAlgorithm().getJwkName())
        .add("b64", false)
        .add("crit", Json.createArrayBuilder().add("b64"));
    String headerText = headerBuilder.build().toString();
    String header = Base64.getUrlEncoder().encodeToString(headerText.getBytes(UTF_8));
    byte[] headerBytes = header.getBytes(UTF_8);

    ByteArrayOutputStream signingBuffer = new ByteArrayOutputStream();
    signingBuffer.write(headerBytes, 0, header.length());
    signingBuffer.write('.');
    JsonWriter jsonWriter = CredentialConstants.JSON_PROVIDER.createWriter(signingBuffer);
    jsonWriter.write(toSign);
    jsonWriter.close();
    context.setBytesToSign(signingBuffer.toByteArray());

    context.sign(keyPair);

    String jws = header + ".." + Base64.getUrlEncoder().encodeToString(context.getSignatureValue());
    proof.set("jws", jws);
    input.setProof(proof);
  }


  @SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
  @Override
  public void verifyProof(VerifyContext context, JsonObject input, Proof proof)
      throws GeneralSecurityException, DidStoreException, UnacceptableDocumentException {
    // The only proof type we support is 'CanonicalJsonWithJws'
    if (!"CanonicalJsonWithJws".equals(proof.getType())) {
      throw new UnacceptableDocumentException(
          "proof_incorrect_type",
          "Proof type is not \"CanonicalJsonWithJws\""
      );
    }

    // The proof must contain a "jws" value, which is not part of what was proved
    String jws = proof.get(String.class, "jws");
    if (jws == null || jws.isEmpty()) {
      throw new UnacceptableDocumentException("proof_missing_jws", "Proof does not contain a \"jws\" value");
    }

    // Lets check the JWS value .. it must have a detached payload so ".." in the middle
    int dotDotIndex = jws.indexOf("..");
    if (dotDotIndex == -1) {
      throw new UnacceptableDocumentException(
          "proof_jws_not_detached",
          "JWS value is not <header>..<signature>"
      );
    }

    // Extract the header and translate it from Base 64 URL
    byte[] b64Ascii = jws.substring(0, dotDotIndex).getBytes(StandardCharsets.US_ASCII);
    byte[] b64;
    try {
      b64 = Base64.getUrlDecoder().decode(b64Ascii);
    } catch (IllegalArgumentException e) {
      // It wasn't perfect Base 64 URL
      throw new UnacceptableDocumentException("proof_jws_header_bad_base64", "JWS header contains an invalid Base64-URL character",
          Map.of("header", jws.substring(0, dotDotIndex))
      );
    }

    // The header should be a valid Json Object
    JsonObject jsonObject;
    try (
        JsonReader reader = Json.createReader(new InputStreamReader(new ByteArrayInputStream(b64), StandardCharsets.UTF_8))
    ) {
      jsonObject = reader.readObject();
    } catch (JsonException e) {
      throw new UnacceptableDocumentException("proof_jws_header_bad_json", "JWS header contains invalid JSON",
          Map.of("badJson", new String(b64, UTF_8))
      );
    }

    // For a detached payload, the header must specify "b64:false"
    if (!JsonValue.FALSE.equals(jsonObject.get("b64"))) {
      throw new UnacceptableDocumentException("proof_jws_header_missing_b64", "JWS header does not specify b64=false");
    }

    // The "alg" is required in the header.
    String headerAlg = jsonObject.getString("alg", null);
    if (headerAlg == null || headerAlg.isEmpty()) {
      throw new UnacceptableDocumentException("proof_jws_header_missing_alg", "JWS header does not specify an 'alg'");
    }

    // The "alg" must be a known algorithm.
    SigningAlgorithm inputAlg;
    try {
      inputAlg = SigningAlgorithm.get(headerAlg);
    } catch (IllegalArgumentException e) {
      throw new UnacceptableDocumentException("proof_jws_header_invalid_alg", "JWS header does not specify a valid 'alg'",
          Map.of("alg", headerAlg)
      );
    }
    if (inputAlg == SigningAlgorithm.NONE) {
      // "NONE" is not a valid value
      throw new UnacceptableDocumentException("proof_jws_header_alg_is_none", "JWS header does not specify NONE for 'alg'",
          Map.of("alg", headerAlg)
      );
    }
    context.setAlgorithm(inputAlg);
    // JWS header is OK.

    // Grab the signature value
    try {
      context.setAllegedSignature(Base64.getUrlDecoder().decode(jws.substring(dotDotIndex + 2)));
    } catch (IllegalArgumentException e) {
      throw new UnacceptableDocumentException("proof_jws_signature_bad_base64", "JWS Signature contains an invalid Base64-URL character",
          Map.of("signature", jws.substring(dotDotIndex + 2))
      );
    }

    // The proof should specify a verification method which is known to us.
    context.findVerificationMethod(proof);

    // create a canonical copy of the input without the jws value
    CJObject jsonInput = JWS_POINTER.remove(new CJObject(input));

    // ready to verify the signature. See RFC 7797 for how the bytes-to-sign is defined.
    ByteArrayOutputStream signingBuffer = new ByteArrayOutputStream();
    signingBuffer.write(b64Ascii, 0, b64Ascii.length);
    signingBuffer.write('.');
    JsonWriter jsonWriter = CredentialConstants.JSON_PROVIDER.createWriter(signingBuffer);
    jsonWriter.write(jsonInput);
    jsonWriter.close();
    byte[] toSignBytes = signingBuffer.toByteArray();
    context.setBytesToSign(toSignBytes);

    // Verify the signature
    context.verify();
  }

}
