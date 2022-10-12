/*
 * Copyright 2022 journeyman.
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
package net.unknowndomain.scouthelper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author journeyman
 */
public class AlgebricCodec extends ListCodec
{
    private static final Pattern VALID_CODEC = Pattern.compile("^A(?<start>([0-9]|[a-f]){2})(?<shift>[0-9]{2})(?<vert>[0-2])(?<horz>[0-3])(?<format>[1-4])(?<base>[0-9]{1,2})$", Pattern.CASE_INSENSITIVE);
    
    private AlgebricCodec(List<String> encodedAlphabet)
    {
        super(encodedAlphabet);
    }
    
    public static AlgebricCodec compile(String seed)
    {
        if (seed == null)
        {
            throw new RuntimeException();
        }
        Matcher matcher = VALID_CODEC.matcher(seed);
        if (!matcher.find())
        {
            throw new RuntimeException();
        }
        int baseValue = Integer.parseInt(matcher.group("start"), 16);
        int leftShift = Integer.parseInt(matcher.group("shift"));
        int verticalFlip = Integer.parseInt(matcher.group("vert"));
        int horizontal = Integer.parseInt(matcher.group("horz"));
        int format = Integer.parseInt(matcher.group("format"));
        int numericRadix = Integer.parseInt(matcher.group("base"));
        System.out.printf("start: %d shift: %d vert: %d horz: %d format: %d base: %d\n", baseValue, leftShift, verticalFlip, horizontal, format, numericRadix);
        List<String> listone = new LinkedList<>();
        String test = Integer.toString(baseValue+25, numericRadix);
        if (test.length() > format)
        {
            format = test.length();
        }
        for (String s : NORMAL_ALPHABET )
        {
            String toInsert = StringUtils.leftPad(Integer.toString(baseValue++, numericRadix), format, '0').toUpperCase();
            if (verticalFlip == 2)
            {
                listone.add(0, toInsert);
            }
            else
            {
                listone.add(toInsert);
            }
        }
        int idx;
        leftShift = leftShift % 26;
        System.out.println("eff. shift: "+ leftShift);
        for (idx = 0; idx < leftShift; idx++)
        {
            String tmp = listone.remove(listone.size()-1);
            listone.add(0, tmp);
        }
        if (verticalFlip == 1)
        {
            List<String> tempList = new LinkedList<>();
            for (String t : listone)
            {
                tempList.add(0, t);
            }
            listone = tempList;
        }
        if (horizontal != 0)
        {
            List<String> tempList = new ArrayList<>(listone.size());
            for (String original : listone)
            {
                StringBuilder sb = new StringBuilder(original);
                String mirrored = sb.reverse().toString();
                switch(horizontal)
                {
                    case 1:
                        tempList.add(mirrored);
                        break;
                    case 2:
                        tempList.add(mirrored + original);
                        break;
                    case 3:
                        tempList.add(original + mirrored);
                        break;
                }
            }
            listone = tempList;
        }
        
        return new AlgebricCodec(listone);
    }
    
}
