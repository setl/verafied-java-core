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
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>;.
 *
 * </notice>
 */

package io.setl.verafied.data.credential;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * A Jackson deserializer for credential status definitions. No such mechanisms are defined in this library implementers must register their implementation here
 * so that the credential can be read successfully.
 *
 * @author Simon Greatrix on 27/10/2021.
 */
public class StatusDeserializer extends StdDeserializer<CredentialStatus> {

  private static ConcurrentMap<String, Class<? extends CredentialStatus>> typeNameMapping = new ConcurrentHashMap<>();


  public static void addTypeMapping(String typeName, Class<? extends CredentialStatus> typeClass) {
    typeNameMapping.put(typeName, typeClass);
  }


  /**
   * Set the type mappings. The provided map replaces the existing one, so further modification of it will be reflected here.
   *
   * @param mappings the new mappings
   */
  @SuppressFBWarnings("EI_EXPOSE_STATIC_REP2") // deliberate
  public static void setTypeMappings(ConcurrentMap<String, Class<? extends CredentialStatus>> mappings) {
    typeNameMapping = mappings;
  }


  public StatusDeserializer() {
    super(CredentialStatus.class);
  }


  @Override
  public CredentialStatus deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    JsonNode treeNode = p.readValueAsTree();

    if (treeNode.has("type")) {
      String typeName = treeNode.get("type").asText();
      Class<? extends CredentialStatus> typeClass = typeNameMapping.get(typeName);
      if (typeClass == null) {
        throw InvalidTypeIdException.from(p, "BaseTransaction must specify a valid 'txType' property", ctxt.constructType(CredentialStatus.class), typeName);
      }
      return p.getCodec().treeToValue(treeNode, typeClass);
    }

    throw InvalidTypeIdException.from(p, "CredentialStatus must specify a valid 'type' property", ctxt.constructType(CredentialStatus.class), null);
  }

}
