import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/*
 * Copyright 2002-2014 the original author or authors.
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

/**
 * 
 * @author xiemalin
 * 
 */
public class FindTopElements2 {
    private static final int ARRAY_LENGTH = 32; // big array length

    public static void main(String[] args) {
        FindTopElements2 fte = new FindTopElements2(ARRAY_LENGTH + 1); // Get a array which is not in order and elements
                                                                       // are not duplicate
        int[] array = getShuffledArray(ARRAY_LENGTH); // Find top 100 elements and print them by desc order in the
                                                      // console
        long start = System.currentTimeMillis();
        fte.findTop100(array);
        long end = System.currentTimeMillis();
        System.out.println("Costs " + (end - start) + "ms");
        
    }

    private final int[] bitmap;
    private final int size;

    public FindTopElements2(final int size) {
        this.size = size;
        int len = ((size % 32) == 0) ? size / 32 : size / 32 + 1;
        System.out.println(len);
        this.bitmap = new int[len];
    }

    private static int index(final int number) {
        return number / 32;
    }

    private static int position(final int number) {
        return number % 32;
    }

    private void adjustBitMap(final int index, final int position) {
        int bit = bitmap[index] | (1 << position);
        bitmap[index] = bit;
    }

    public void add(int[] numArr) {
        for (int i = 0; i < numArr.length; i++) {
            add(numArr[i]);
        }
    }

    public void add(int number) {
        adjustBitMap(index(number), position(number));
    }

    public boolean getIndex(final int index) {
        if (index > size) {
            return false;
        }
        int bit = (bitmap[index(index)] >> position(index)) & 0x0001;
        return (bit == 1);
    }

    private void findTop100(int[] arr) {
        System.out.println("Start to compute.");
        add(arr);
        System.out.println(Arrays.toString(bitmap));
        int[] result = new int[100];
        int index = 0;
        for (int i = bitmap.length - 1; i >= 0; i--) {
            for (int j = 31; j >= 0; j--) {
                if (((bitmap[i] >> j) & 0x0001) == 1) {
                    
                    System.out.println("---" + bitmap[i] + "------" + Integer.toString(i) + "-----" + Integer.toBinaryString((bitmap[i] >> j)));
                    
                    if (index == result.length) {
                        break;
                    }
                    result[index++] = ((i) * 32) + j;
                            
                           
                    System.out.println("---" + ( ((i) * 32) + j)+ "------");
                }
            }
            if (index == result.length) {
                break;
            }
        }
        for (int j = 0; j < result.length; j++) {
            System.out.println(result[j]);
        }
        System.out.println("Finish to output result.");
    }

    /** * Get shuffled int array * * @return array not in order and elements are not duplicate */
    private static int[] getShuffledArray(int len) {
        System.out.println("Start to generate test array... this may take several seconds.");
        List<Integer> list = new ArrayList<Integer>(len);
        for (int i = 10; i < len+9; i++) {
            list.add(i);
        }
        
        list.add(200);
        
        Collections.shuffle(list);
        int[] ret = new int[len];
        for (int i = 0; i < len; i++) {
            ret[i] = list.get(i);
        }
        return ret;
    }
}
