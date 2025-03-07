/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.util;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

/*
 * Simple class that extends the regular java.util.HashMap but overrides the
 * toString() method of the HashMap class to produce a JSON string instead
 */
public class JsonStringHashMap<K, V> extends LinkedHashMap<K, V> {
  // lgtm [java/inconsistent-equals-and-hashcode]
  private static final ObjectMapper mapper = new ObjectMapper()
      .registerModule(SerializationModule.getModule())
      .registerModule(new JodaModule());

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof Map)) {
      return false;
    }
    Map<?,?> other = (Map<?,?>) obj;
    if (this.size() != other.size()) {
      return false;
    }
    for (K key : this.keySet()) {
      if (this.get(key) == null ) {
        if (other.get(key) == null) {
          continue;
        } else {
          return false;
        }
      }
      if (this.get(key) instanceof byte[]) {
        if (other.get(key) instanceof byte[]) {
          return Arrays.equals((byte[]) this.get(key), (byte[]) other.get(key));
        } else {
          return false;
        }
      }
      if ( ! this.get(key).equals(other.get(key))) {
        return false;
      }
    }
    return true;
  }

  @Override
  public final String toString() {
    try {
      return mapper.writeValueAsString(this);
    } catch(JsonProcessingException e) {
      throw new IllegalStateException("Cannot serialize hash map to JSON string", e);
    }
  }
}
