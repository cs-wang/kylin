/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.kylin.dimension;

import java.nio.ByteBuffer;

import org.apache.kylin.common.util.Bytes;
import org.apache.kylin.metadata.datatype.DataTypeSerializer;
import org.junit.Assert;
import org.junit.Test;

public class IntegerDimEncTest {

    @Test
    public void testConstructor() {
        try {
            new IntegerDimEnc(0);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // expect
        }
        try {
            new IntegerDimEnc(9);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // expect
        }
        new IntegerDimEnc(8);
    }

    @Test
    public void testNull() {
        for (int i = 1; i < 9; i++) {
            IntegerDimEnc enc = new IntegerDimEnc(i);

            byte[] buf = new byte[enc.getLengthOfEncoding()];
            enc.encode(null, 0, buf, 0);
            Assert.assertTrue(DimensionEncoding.isNull(buf, 0, buf.length));
            String decode = enc.decode(buf, 0, buf.length);
            Assert.assertEquals(null, decode);

            buf = new byte[enc.getLengthOfEncoding()];
            DataTypeSerializer<Object> ser = enc.asDataTypeSerializer();
            ser.serialize(null, ByteBuffer.wrap(buf));
            Assert.assertTrue(DimensionEncoding.isNull(buf, 0, buf.length));
            decode = (String) ser.deserialize(ByteBuffer.wrap(buf));
            Assert.assertEquals(null, decode);
        }
    }

    @Test
    public void testEncodeDecode() {
        IntegerDimEnc enc = new IntegerDimEnc(2);
        testEncodeDecode(enc, 0);
        testEncodeDecode(enc, 100);
        testEncodeDecode(enc, 10000);
        testEncodeDecode(enc, 32767);
        testEncodeDecode(enc, -100);
        testEncodeDecode(enc, -10000);
        testEncodeDecode(enc, -32767);
        try {
            testEncodeDecode(enc, 32768);
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals("expected:<32768> but was:<null>", e.getMessage());
        }
        try {
            testEncodeDecode(enc, -32768);
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals("expected:<-32768> but was:<null>", e.getMessage());
        }
    }

    @Test
    public void testEncodeDecode2() {
        IntegerDimEnc enc = new IntegerDimEnc(8);
        testEncodeDecode(enc, 0);
        testEncodeDecode(enc, 100);
        testEncodeDecode(enc, 10000);
        testEncodeDecode(enc, Long.MAX_VALUE);
        testEncodeDecode(enc, -100);
        testEncodeDecode(enc, -10000);
        testEncodeDecode(enc, -Long.MAX_VALUE);
        try {
            testEncodeDecode(enc, Long.MIN_VALUE);
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals("expected:<-9223372036854775808> but was:<null>", e.getMessage());
        }
    }

    private void testEncodeDecode(IntegerDimEnc enc, long value) {
        String valueStr = "" + value;
        byte[] buf = new byte[enc.getLengthOfEncoding()];
        byte[] bytes = Bytes.toBytes(valueStr);
        enc.encode(bytes, bytes.length, buf, 0);
        String decode = enc.decode(buf, 0, buf.length);
        Assert.assertEquals(valueStr, decode);
    }

    @Test
    public void testSerDes() {
        IntegerDimEnc enc = new IntegerDimEnc(2);
        testSerDes(enc, 0);
        testSerDes(enc, 100);
        testSerDes(enc, 10000);
        testSerDes(enc, 32767);
        testSerDes(enc, -100);
        testSerDes(enc, -10000);
        testSerDes(enc, -32767);
        try {
            testSerDes(enc, 32768);
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals("expected:<32768> but was:<null>", e.getMessage());
        }
        try {
            testSerDes(enc, -32768);
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals("expected:<-32768> but was:<null>", e.getMessage());
        }
    }

    private void testSerDes(IntegerDimEnc enc, long value) {
        DataTypeSerializer<Object> ser = enc.asDataTypeSerializer();
        byte[] buf = new byte[enc.getLengthOfEncoding()];
        String valueStr = "" + value;
        ser.serialize(valueStr, ByteBuffer.wrap(buf));
        String decode = (String) ser.deserialize(ByteBuffer.wrap(buf));
        Assert.assertEquals(valueStr, decode);
    }

}
