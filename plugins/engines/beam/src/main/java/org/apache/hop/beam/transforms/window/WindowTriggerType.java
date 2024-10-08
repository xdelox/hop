/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.hop.beam.transforms.window;

@SuppressWarnings("java:S115")
public enum WindowTriggerType {
  None("No triggering"),
  RepeatedlyForeverAfterWatermarkPastEndOfWindow(
      "Repeatedly forever: after watermark, past the end of the window");

  private String description;

  WindowTriggerType(String description) {
    this.description = description;
  }

  public static final String[] getDescriptions() {
    String[] descriptions = new String[values().length];
    for (int i = 0; i < descriptions.length; i++) {
      descriptions[i] = values()[i].description;
    }
    return descriptions;
  }

  public static final WindowTriggerType findDescription(String description) {
    for (WindowTriggerType type : values()) {
      if (type.description.equalsIgnoreCase(description)) {
        return type;
      }
    }
    return WindowTriggerType.None;
  }

  /**
   * Gets description
   *
   * @return value of description
   */
  public String getDescription() {
    return description;
  }
}
