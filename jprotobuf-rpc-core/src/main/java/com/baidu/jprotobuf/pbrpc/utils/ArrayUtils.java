/*
 * Copyright 2002-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baidu.jprotobuf.pbrpc.utils;

/**
 * <p>
 * Operations on arrays, primitive arrays (like <code>int[]</code>) and
 * primitive wrapper arrays (like <code>Integer[]</code>).
 * </p>
 * 
 * <p>
 * This class tries to handle <code>null</code> input gracefully. An exception
 * will not be thrown for a <code>null</code> array input. However, an Object
 * array that contains a <code>null</code> element may throw an exception. Each
 * method documents its behaviour.
 * </p>
 * 
 * @author xiemalin
 * @since 2.10
 */
public class ArrayUtils {
    
    /**
     * An empty immutable <code>byte</code> array.
     */
    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    /**
     * <p>
     * Produces a new <code>byte</code> array containing the elements between
     * the start and end indices.
     * </p>
     * 
     * <p>
     * The start index is inclusive, the end index exclusive. Null array input
     * produces null output.
     * </p>
     * 
     * @param array
     *            the array
     * @param startIndexInclusive
     *            the starting index. Undervalue (&lt;0) is promoted to 0,
     *            overvalue (&gt;array.length) results in an empty array.
     * @param endIndexExclusive
     *            elements up to endIndex-1 are present in the returned
     *            subarray. Undervalue (&lt; startIndex) produces empty array,
     *            overvalue (&gt;array.length) is demoted to array length.
     * @return a new array containing the elements between the start and end
     *         indices.
     * @since 2.1
     */
    public static byte[] subarray(byte[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        int newSize = endIndexExclusive - startIndexInclusive;
        if (newSize <= 0) {
            return EMPTY_BYTE_ARRAY;
        }

        byte[] subarray = new byte[newSize];
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }
}
