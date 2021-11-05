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

import static io.setl.verafied.CredentialConstants.logSafe;

import java.security.GeneralSecurityException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;

import io.setl.verafied.CredentialConstants;
import io.setl.verafied.UnacceptableDocumentException;
import io.setl.verafied.data.JsonConvert;
import io.setl.verafied.data.Proof;
import io.setl.verafied.did.DidStoreException;

/**
 * Utility methods for verifying a document's proof.
 *
 * @author Simon Greatrix on 11/11/2020.
 */
public class ProvableApi {

  private static final String DOCUMENT_TYPE = "documentType";

  private static final String JSON_TYPE = "jsonType";


  /**
   * Get the data types given in a JSON-LD type specification.
   *
   * @param types either a string or an array of strings
   * @param type  the type of the document
   * @param id    the id of the document
   *
   * @return set of types
   *
   * @throws UnacceptableDocumentException if the type specification is missing or invalid
   */
  public static Set<String> getTypes(JsonValue types, String type, Object id) throws UnacceptableDocumentException {
    if (types == null) {
      String message = String.format("%s %s NOT verified as it does not specify any types", type, logSafe(String.valueOf(id)));
      throw new UnacceptableDocumentException("document_has_no_types", message, Map.of("id", id));
    }

    Set<String> typeSet;
    if (types.getValueType() == ValueType.STRING) {
      return Set.of(((JsonString) types).getString());
    }

    if (types.getValueType() != ValueType.ARRAY) {
      String message = String.format("%s %s NOT verified as its type specification is a %s", type, logSafe(String.valueOf(id)), types.getValueType());
      throw new UnacceptableDocumentException("document_bad_type_specifier", message,
          Map.of(DOCUMENT_TYPE, type, "id", id, JSON_TYPE, types.getValueType())
      );
    }

    JsonArray array = types.asJsonArray();
    typeSet = new HashSet<>();
    for (JsonValue jv : array) {
      if (jv == null) {
        // The SETL Canonical JSON provider will never return a null for a JsonArray member
        String message = String.format("%s %s NOT verified as its type specification contains a null", type, logSafe(String.valueOf(id)));
        throw new UnacceptableDocumentException("document_contains_null_type", message, Map.of(DOCUMENT_TYPE, type, "id", id));
      }

      if (jv.getValueType() != ValueType.STRING) {
        String message = String.format("%s %s NOT verified as its type specification contains a %s", type, logSafe(String.valueOf(id)), jv.getValueType());
        throw new UnacceptableDocumentException("document_bad_contained_type_specifier", message,
            Map.of(DOCUMENT_TYPE, type, "id", id, JSON_TYPE, jv.getValueType())
        );
      }

      typeSet.add(((JsonString) jv).getString());
    }

    return typeSet;
  }


  /**
   * Verify that this document correctly declares the W3C context.
   *
   * @throws UnacceptableDocumentException if the W3C context is missing or invalid
   */
  public static void verifyContext(JsonValue ctxtValue, String type, Object id) throws UnacceptableDocumentException {
    JsonString ctxtString = null;
    if (ctxtValue == null) {
      String message = String.format("%s %s does not specify an \"@context\" value", type, logSafe(String.valueOf(id)));
      throw new UnacceptableDocumentException("document_context_missing", message, Map.of(DOCUMENT_TYPE, type, "id", id));
    }

    if (ctxtValue.getValueType() == ValueType.STRING) {
      ctxtString = (JsonString) ctxtValue;
    } else if (ctxtValue.getValueType() == ValueType.ARRAY) {
      JsonArray ctxtArray = ctxtValue.asJsonArray();
      if (!ctxtArray.isEmpty()) {
        JsonValue t = ctxtArray.get(0);

        // We are only matching the W3 required context, which must be a string and which must come first
        if (t != null && t.getValueType() == ValueType.STRING) {
          ctxtString = (JsonString) t;
        }
      }
    } else {
      String message = String.format("%s %s does not specify a valid \"@context\" value", type, logSafe(String.valueOf(id)));
      throw new UnacceptableDocumentException("document_context_bad_type", message,
          Map.of(DOCUMENT_TYPE, type, "id", id, JSON_TYPE, ctxtValue.getValueType())
      );
    }

    if (ctxtString == null || !ctxtString.getString().equals(CredentialConstants.CREDENTIAL_CONTEXT)) {
      // W3C rules say that specific context must come first.
      String message = String.format("%s %s NOT verified as missing context: %s",
          type, logSafe(String.valueOf(id)), logSafe(ctxtValue.toString())
      );
      throw new UnacceptableDocumentException("document_context_w3c_must_be_first", message,
          Map.of(DOCUMENT_TYPE, type, "id", id, "context", ctxtValue)
      );
    }
  }


  /**
   * Verify that the cryptographic proof for this is correct.
   *
   * @throws UnacceptableDocumentException if the proof is invalid
   * @throws DidStoreException             if the signing DIDs cannot be accessed
   */
  public static void verifyProof(Proof myProof, Object document, String type, Object id, VerifyContext verifyContext)
      throws DidStoreException, UnacceptableDocumentException {
    // The input must contain a 'proof'
    if (myProof == null) {
      String message = String.format("%s %s has not been proved", type, logSafe(String.valueOf(id)));
      throw new UnacceptableDocumentException("document_no_proof", message, Map.of(DOCUMENT_TYPE, type, "id", id));
    }

    JsonObject input = (JsonObject) JsonConvert.toJson(document);
    CanonicalJsonWithJws verifier = new CanonicalJsonWithJws();
    try {
      verifier.verifyProof(verifyContext, input, myProof);
    } catch (GeneralSecurityException e) {
      // Proof object is invalid
      String message = String.format("%s %s proof did not verify", type, logSafe(String.valueOf(id)));
      throw new UnacceptableDocumentException(
          "document_proof_error", message,
          Map.of(DOCUMENT_TYPE, type, "id", id, "errorMessage", e.toString(), "error", e)
      );
    }
  }


  /**
   * Verify if a JSON-LD document specifies the required type.
   *
   * @param types the types on the document
   * @param type  the type of the document
   * @param id    the ID of the document
   * @param match the type to match
   *
   * @throws UnacceptableDocumentException if the document does not have the type
   */
  public static void verifyType(Set<String> types, String type, Object id, String match) throws UnacceptableDocumentException {
    if (types == null) {
      String message = String.format("%s %s NOT verified as it does not specify any types", type, logSafe(String.valueOf(id)));
      throw new UnacceptableDocumentException("document_type_is_null", message,
          Map.of(DOCUMENT_TYPE, type, "id", id, "requiredType", match)
      );
    }

    if (!types.contains(match)) {
      // Not typed
      String message = String.format("%s %s NOT verified as not correct type of \"%s\": %s",
          type, logSafe(String.valueOf(id)), match, logSafe(types.toString())
      );
      throw new UnacceptableDocumentException("document_type_missing", message,
          Map.of(DOCUMENT_TYPE, type, "id", id, "requiredType", match)
      );
    }
  }


  private ProvableApi() {
    // Hidden as this is a utility class
  }

}
