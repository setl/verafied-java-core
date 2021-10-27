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
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.setl.verafied.CredentialConstants;
import io.setl.verafied.data.JsonConvert;
import io.setl.verafied.data.Proof;
import io.setl.verafied.did.DidStoreException;

/**
 * @author Simon Greatrix on 11/11/2020.
 */
public class ProvableApi {

  private static final Logger logger = LoggerFactory.getLogger(ProvableApi.class);


  /**
   * Get the data types given in a JSON-LD type specification.
   *
   * @param types  either a string or an array of strings
   * @param type   the type of the document
   * @param id     the id of the document
   * @param holder holder of error messages
   *
   * @return set of types, or null on failure
   */
  public static Set<String> getTypes(JsonValue types, String type, Object id, AtomicReference<String> holder) {
    if (types == null) {
      String message = String.format("%s %s NOT verified as it does not specify any types", type, logSafe(String.valueOf(id)));
      holder.set(message);
      logger.debug(message);
      return null;
    }

    Set<String> typeSet;
    if (types.getValueType() == ValueType.STRING) {
      return Set.of(((JsonString) types).getString());
    }

    if (types.getValueType() != ValueType.ARRAY) {
      String message = String.format("%s %s NOT verified as its type specification is a %s", type, logSafe(String.valueOf(id)), types.getValueType());
      holder.set(message);
      logger.debug(message);
      return null;
    }

    JsonArray array = types.asJsonArray();
    typeSet = new HashSet<>();
    for (JsonValue jv : array) {
      if (jv == null) {
        String message = String.format("%s %s NOT verified as its type specification contains a null", type, logSafe(String.valueOf(id)));
        holder.set(message);
        logger.debug(message);
        return null;
      }

      if (jv.getValueType() != ValueType.STRING) {
        String message = String.format("%s %s NOT verified as its type specification contains a %s", type, logSafe(String.valueOf(id)), jv.getValueType());
        holder.set(message);
        logger.debug(message);
        return null;
      }

      typeSet.add(((JsonString) jv).getString());
    }

    return typeSet;
  }


  /**
   * Verify that this credential correctly declares its type as "VerifiableCredential" and has the W3C context.
   *
   * @return true if OK
   */
  public static boolean verifyContext(JsonValue ctxtValue, String type, Object id, AtomicReference<String> holder) {
    JsonString ctxtString = null;
    if (ctxtValue == null) {
      String message = String.format("%s %s does not specify an \"@context\" value", type, logSafe(String.valueOf(id)));
      holder.set(message);
      logger.debug(message);
      return false;
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
      holder.set(message);
      logger.debug(message);
      return false;
    }

    if (ctxtString == null || !ctxtString.getString().equals(CredentialConstants.CREDENTIAL_CONTEXT)) {
      // W3C rules say that specific context must come first.
      String message = String.format("%s %s NOT verified as missing context: %s",
          type, logSafe(String.valueOf(id)), logSafe(ctxtValue.toString())
      );
      holder.set(message);
      logger.debug(message);
      return false;
    }

    return true;
  }


  /**
   * Verify that the cryptographic proof for this is correct.
   *
   * @return true if OK
   */
  public static boolean verifyProof(Proof myProof, Object document, String type, Object id, VerifyContext verifyContext, AtomicReference<String> holder)
      throws DidStoreException {
    // The input must contain a 'proof'
    if (myProof == null) {
      String message = String.format("%s %s has not been proved", type, logSafe(String.valueOf(id)));
      holder.set(message);
      logger.debug(message);
      return false;
    }

    JsonObject input = (JsonObject) JsonConvert.toJson(document);
    CanonicalJsonWithJws verifier = new CanonicalJsonWithJws();
    try {
      VerifyOutput verifyOutput = verifier.verifyProof(verifyContext, input, myProof);
      if (!verifyOutput.isOk()) {
        String message = String.format("%s %s has a bad signature: %s", type, logSafe(String.valueOf(id)), verifyOutput.getDetail());
        holder.set(message);
        logger.debug(message);
        return false;
      }
    } catch (GeneralSecurityException e) {
      // Proof object is invalid
      String message = String.format("%s %s proof did not verify", type, logSafe(String.valueOf(id)));
      holder.set(message);
      logger.debug(message, e);
      return false;
    }

    return true;
  }


  /**
   * Verify if a JSON-LD document specifies one of the required types.
   *
   * @param types  the types on the document
   * @param type   the type of the document
   * @param id     the ID of the document
   * @param holder holder for error messages
   * @param match  the type to match
   *
   * @return true if the type matches, false otherwise and the holder will be updated as to why
   */
  public static boolean verifyType(Set<String> types, String type, Object id, AtomicReference<String> holder, String match) {
    if (types == null) {
      String message = String.format("%s %s NOT verified as it does not specify any types", type, logSafe(String.valueOf(id)));
      holder.set(message);
      logger.debug(message);
      return false;
    }

    if (!types.contains(match)) {
      // Not typed
      String message = String.format("%s %s NOT verified as not correct type of \"%s\": %s",
          type, logSafe(String.valueOf(id)), match, logSafe(types.toString())
      );
      holder.set(message);
      logger.debug(message);
      return false;
    }

    return true;
  }

}
