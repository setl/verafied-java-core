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
import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonPointer;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.JsonWriter;

import io.setl.json.CJObject;
import io.setl.verafied.CredentialConstants;
import io.setl.verafied.data.Proof;
import io.setl.verafied.data.Provable;
import io.setl.verafied.data.TypedKeyPair;
import io.setl.verafied.data.jwk.SigningAlgorithm;
import io.setl.verafied.did.DidStoreException;

/**
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


  @Override
  public VerifyOutput verifyProof(VerifyContext context, JsonObject input, Proof proof)
      throws GeneralSecurityException, DidStoreException {
    // The only proof type we support is 'CanonicalJsonWithJws'
    if (!"CanonicalJsonWithJws".equals(proof.getType())) {
      return VerifyOutput.fail("Proof type is not \"CanonicalJsonWithJws\"", VerifyType.SIGNED_JSON);
    }

    // The proof must contain a "jws" value, which is not part of what was proved
    String jws = proof.get(String.class, "jws");
    if (jws == null || jws.isEmpty()) {
      return VerifyOutput.fail("Proof does not contain a \"jws\" value", VerifyType.SIGNED_JSON);
    }

    // Lets check the JWS value .. it must have a detached payload so ".." in the middle
    int dotDotIndex = jws.indexOf("..");
    if (dotDotIndex == -1) {
      return VerifyOutput.fail("JWS value is not <header>..<signature>", VerifyType.SIGNED_JSON);
    }

    // Extract the header and translate it from Base 64 URL
    byte[] b64Ascii = jws.substring(0, dotDotIndex).getBytes(StandardCharsets.US_ASCII);
    byte[] b64;
    try {
      b64 = Base64.getUrlDecoder().decode(b64Ascii);
    } catch (IllegalArgumentException e) {
      // It wasn't perfect Base 64 URL
      return VerifyOutput.fail("JWS header contains an invalid Base64-URL character", VerifyType.SIGNED_JSON);
    }

    // The header should be a valid Json Object
    JsonObject jsonObject;
    try (
        JsonReader reader = Json.createReader(new InputStreamReader(new ByteArrayInputStream(b64), StandardCharsets.UTF_8))
    ) {
      jsonObject = reader.readObject();
    } catch (JsonException e) {
      return VerifyOutput.fail("JWS header contains invalid JSON", VerifyType.SIGNED_JSON);
    }

    // For a detached payload, the header must specify "b64:false"
    if (!JsonValue.FALSE.equals(jsonObject.get("b64"))) {
      return VerifyOutput.fail("JWS header does not specify b64=false", VerifyType.SIGNED_JSON);
    }

    // The "alg" is required in the header.
    String headerAlg = jsonObject.getString("alg", null);
    if (headerAlg == null || headerAlg.isEmpty()) {
      return VerifyOutput.fail("JWS header does not specify an 'alg'", VerifyType.SIGNED_JSON);
    }

    // The "alg" must be a known algorithm.
    SigningAlgorithm inputAlg;
    try {
      inputAlg = SigningAlgorithm.get(headerAlg);
    } catch (IllegalArgumentException e) {
      return VerifyOutput.fail("JWS header does not specify a valid 'alg'", VerifyType.SIGNED_JSON);
    }
    if (inputAlg == SigningAlgorithm.NONE) {
      // "NONE" is not a valid value
      return VerifyOutput.fail("JWS header specifies NONE for 'alg'", VerifyType.SIGNED_JSON);
    }
    context.setAlgorithm(inputAlg);
    // JWS header is OK.

    // Grab the signature value
    try {
      context.setAllegedSignature(Base64.getUrlDecoder().decode(jws.substring(dotDotIndex + 2)));
    } catch (IllegalArgumentException e) {
      return VerifyOutput.fail("JWS Signature contains an invalid Base64-URL character", VerifyType.SIGNED_JSON);
    }

    // The proof should specify a verification method which is known to us.
    try {
      context.findVerificationMethod(proof);
    } catch (VerifyOutputException failure) {
      return failure.getVerifyOutput();
    }

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

    return context.verify();
  }

}
