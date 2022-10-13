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
package net.unknowndomain.scouthelper.codecs.shift;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.unknowndomain.scouthelper.codecs.ListCodec;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author journeyman
 */
public class ShiftCodec extends ListCodec
{
    private static final Character ID = 'S';
    private static final Pattern VALID_CODEC = Pattern.compile("^"+ ID + "(?<start>([0-9]|[a-f]){2})(?<shift>[0-9]{2})(?<vert>[0-3])(?<horz>[0-7])(?<format>[0-4])(?<base>[0-9]{1,2})$", Pattern.CASE_INSENSITIVE);
    
    public enum Flags
    {
        VERTICAL_FLIP_POST_INIT,
        VERTICAL_FLIP_POST_SHIFT,
        INVERT_CHARACTERS,
        MIRROR_LEFT,
        MIRROR_RIGHT
    }
    
    private ShiftCodec(List<String> encodedAlphabet)
    {
        super(encodedAlphabet);
    }
    
    public static ShiftCodec compile(String seed)
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
        Set<Flags> flags = new HashSet<>();
        if (verticalFlip >= 2)
        {
            flags.add(Flags.VERTICAL_FLIP_POST_INIT);
            verticalFlip -= 2;
        }
        if (verticalFlip >= 1)
        {
            flags.add(Flags.VERTICAL_FLIP_POST_SHIFT);
            verticalFlip -= 1;
        }
        if (horizontal >= 4)
        {
            flags.add(Flags.MIRROR_LEFT);
            horizontal -= 4;
        }
        if (horizontal >= 2)
        {
            flags.add(Flags.MIRROR_RIGHT);
            horizontal -= 2;
        }
        if (horizontal >= 1)
        {
            flags.add(Flags.INVERT_CHARACTERS);
            horizontal -= 1;
        }
        
        return build(baseValue, leftShift, format, numericRadix, flags);
    }
    
    public static String seed(int baseValue, int leftShift, int format, int numericRadix, Flags ... flags)
    {
        Set<Flags> mod = new HashSet<>();
        mod.addAll(Arrays.asList(flags));
        return seed(baseValue, leftShift, format, numericRadix, mod);
    }
    
    public static String seed(int baseValue, int leftShift, int format, int numericRadix, Set<Flags> flags)
    {
        StringBuilder seeder = new StringBuilder();
        seeder.append(ID);
        if (baseValue < 0)
        {
            baseValue = 0;
        }
        if (baseValue > 255)
        {
            baseValue = 255;
        }
        seeder.append(StringUtils.leftPad(Integer.toString(baseValue, 16), 2, '0'));
        if (leftShift < -26)
        {
            leftShift = leftShift % 26;
        }
        if (leftShift < 0)
        {
            leftShift = 26 + leftShift;
        }
        if (leftShift >= 26)
        {
            leftShift = leftShift % 26;
        }
        seeder.append(StringUtils.leftPad(Integer.toString(leftShift, 10), 2, '0'));
        int verticalFlip = 0;
        int horizontal = 0;
        if (flags.contains(Flags.VERTICAL_FLIP_POST_INIT))
        {
            verticalFlip += 2;
        }
        if (flags.contains(Flags.VERTICAL_FLIP_POST_SHIFT))
        {
            verticalFlip += 1;
        }
        seeder.append(Integer.toString(verticalFlip, 8));
        if (flags.contains(Flags.MIRROR_LEFT))
        {
            horizontal += 4;
        }
        if (flags.contains(Flags.MIRROR_RIGHT))
        {
            horizontal += 2;
        }
        if (flags.contains(Flags.INVERT_CHARACTERS))
        {
            horizontal += 1;
        }
        seeder.append(Integer.toString(horizontal, 8));
        if (format < 0)
        {
            format = 0;
        }
        if (format > 4)
        {
            format = 4;
        }
        seeder.append(Integer.toString(format, 10));
        if (numericRadix < 2)
        {
            numericRadix = 2;
        }
        if (numericRadix > 36)
        {
            numericRadix = 36;
        }
        seeder.append(Integer.toString(numericRadix, 10));
        return seeder.toString().toUpperCase();
    }
    
    public static ShiftCodec build(int baseValue, int leftShift, int format, int numericRadix, Flags ... flags)
    {
        Set<Flags> mod = new HashSet<>();
        mod.addAll(Arrays.asList(flags));
        return build(baseValue, leftShift, format, numericRadix, mod);
    }
    
    public static ShiftCodec build(int baseValue, int leftShift, int format, int numericRadix, Set<Flags> flags)
    {
        List<String> listone = new LinkedList<>();
        String test = Integer.toString(baseValue+25, numericRadix);
        if (test.length() > format)
        {
            format = test.length();
        }
        for (String s : NORMAL_ALPHABET )
        {
            String toInsert = StringUtils.leftPad(Integer.toString(baseValue++, numericRadix), format, '0').toUpperCase();
            if (flags.contains(Flags.VERTICAL_FLIP_POST_INIT))
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
        for (idx = 0; idx < leftShift; idx++)
        {
            String tmp = listone.remove(listone.size()-1);
            listone.add(0, tmp);
        }
        if (flags.contains(Flags.VERTICAL_FLIP_POST_SHIFT))
        {
            List<String> tempList = new LinkedList<>();
            for (String t : listone)
            {
                tempList.add(0, t);
            }
            listone = tempList;
        }
        if (flags.contains(Flags.INVERT_CHARACTERS) || flags.contains(Flags.MIRROR_LEFT) || flags.contains(Flags.MIRROR_LEFT))
        {
            List<String> tempList = new ArrayList<>(listone.size());
            for (String original : listone)
            {
                StringBuilder sb = new StringBuilder(original);
                String mirrored = sb.reverse().toString();
                sb = new StringBuilder();
                String insert = original;
                if (flags.contains(Flags.INVERT_CHARACTERS))
                {
                    insert = mirrored;
                    mirrored = original;
                }
                if (flags.contains(Flags.MIRROR_LEFT))
                {
                    sb.append(mirrored);
                }
                sb.append(insert);
                if (flags.contains(Flags.MIRROR_RIGHT))
                {
                    sb.append(mirrored);
                }
                tempList.add(sb.toString());
            }
            listone = tempList;
        }
        
        return new ShiftCodec(listone);
    }
    
}
