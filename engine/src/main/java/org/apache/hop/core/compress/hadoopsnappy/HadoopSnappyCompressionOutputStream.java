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
 */

package org.apache.hop.core.compress.hadoopsnappy;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import org.apache.hop.core.compress.CompressionOutputStream;
import org.apache.hop.core.compress.ICompressionProvider;

public class HadoopSnappyCompressionOutputStream extends CompressionOutputStream {

  public HadoopSnappyCompressionOutputStream(OutputStream out, ICompressionProvider provider)
      throws IOException {
    super(getDelegate(out), provider);
  }

  private static OutputStream getDelegate(OutputStream out) throws IOException {
    try {
      return getSnappyOutputStream(out);
    } catch (Exception e) {
      throw new IOException(e);
    }
  }

  /**
   * Gets an OutputStream that uses the snappy codec and wraps the supplied base output stream.
   *
   * @param out the base output stream to wrap around
   * @return a OutputStream that uses the Snappy codec
   * @throws Exception if snappy is not available or an error occurs during reflection
   */
  public static OutputStream getSnappyOutputStream(OutputStream out) throws Exception {
    return getSnappyOutputStream(
        HadoopSnappyCompressionProvider.IO_COMPRESSION_CODEC_SNAPPY_DEFAULT_BUFFERSIZE, out);
  }

  /**
   * Gets an OutputStream that uses the snappy codec and wraps the supplied base output stream.
   *
   * @param bufferSize the buffer size for the codec to use (in bytes)
   * @param out the base output stream to wrap around
   * @return a OutputStream that uses the Snappy codec
   * @throws Exception if snappy is not available or an error occurs during reflection
   */
  public static OutputStream getSnappyOutputStream(int bufferSize, OutputStream out)
      throws Exception {
    if (!HadoopSnappyCompressionProvider.isHadoopSnappyAvailable()) {
      throw new Exception("Hadoop-snappy does not seem to be available");
    }

    Object snappyShim = HadoopSnappyCompressionProvider.getActiveSnappyShim();
    Method getSnappyOutputStream =
        snappyShim.getClass().getMethod("getSnappyOutputStream", int.class, OutputStream.class);
    return (OutputStream) getSnappyOutputStream.invoke(snappyShim, bufferSize, out);
  }
}
