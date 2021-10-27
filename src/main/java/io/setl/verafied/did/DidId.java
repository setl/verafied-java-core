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

package io.setl.verafied.did;

import java.net.URI;

/**
 * Process DID URLs. This class assumes the DID URL is well-formed.
 *
 * @author Simon Greatrix on 23/10/2020.
 */
public class DidId {

  /** The fragment. */
  private final String fragment;

  /** The DID ID. */
  private final String id;

  /** The DID method. */
  private final String method;

  /** The URL path, if any. */
  private final String path;

  /** The URL query part, if any. */
  private final String query;

  /** The URI. */
  private final URI uri;


  public DidId(String method, String id, String path, String query, String fragment) {
    this.fragment = fragment;
    this.id = id;
    this.method = method;
    this.path = path;
    this.query = query;
    StringBuilder uriText = new StringBuilder("did:").append(method).append(':').append(id);
    if (path != null) {
      uriText.append(path);
    }
    if (query != null) {
      uriText.append('?').append(query);
    }
    if (fragment != null) {
      uriText.append('#').append(fragment);
    }
    uri = URI.create(uriText.toString());
  }


  /**
   * New instance.
   *
   * @param uri the DID URL as a URI
   */
  public DidId(URI uri) {
    fragment = uri.getRawFragment();
    String part = uri.getRawSchemeSpecificPart();
    int queryStart = part.indexOf('?');
    if (queryStart != -1) {
      query = part.substring(queryStart + 1);
      part = part.substring(0, queryStart);
    } else {
      query = null;
    }

    int pathStart = part.indexOf('/');
    if (pathStart != -1) {
      path = part.substring(pathStart);
      part = part.substring(0, pathStart);
    } else {
      path = null;
    }

    int methodEnd = part.indexOf(':');
    method = part.substring(0, methodEnd);
    id = part.substring(methodEnd + 1);

    this.uri = uri;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    return (o instanceof DidId) && uri.equals(((DidId) o).uri);
  }


  /**
   * Get the fragment. This does not include the initial '#'.
   *
   * @return the fragment
   */
  public String getFragment() {
    return fragment;
  }


  /**
   * Get the full DID URI including path, query and fragment parts.
   *
   * @return the DID URI
   */
  public URI getUri() {
    return uri;
  }


  public String getId() {
    return id;
  }


  public String getMethod() {
    return method;
  }


  /**
   * Get the path, from the initial '/'.
   *
   * @return the path
   */
  public String getPath() {
    return path;
  }


  /**
   * Get the query part of the URL. This does not include the initial '?'.
   *
   * @return the query
   */
  public String getQuery() {
    return query;
  }


  @Override
  public int hashCode() {
    return uri.hashCode();
  }


  public String toString() {
    return uri.toString();
  }


  public DidId withoutFragment() {
    if (fragment == null) {
      return this;
    }
    return new DidId(method, id, path, query, null);
  }

}
