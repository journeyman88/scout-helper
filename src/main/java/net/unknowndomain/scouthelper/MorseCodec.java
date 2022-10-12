/*
 * Copyright 2022 Marco Bignami.
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

/**
 *
 * @author m.bignami
 */
public class MorseCodec implements TextCodec
{
    private static final BidiMap<String, String> CODEC_MAP = new DualHashBidiMap<>();
    
    static
    {
        CODEC_MAP.put("A", "·-");
        CODEC_MAP.put("B", "-···");
        CODEC_MAP.put("C", "-·-·");
        CODEC_MAP.put("D", "-··");
        CODEC_MAP.put("E", "·");
        CODEC_MAP.put("F", "··-·");
        CODEC_MAP.put("G", "·⎻");
        CODEC_MAP.put("H", "····");
        CODEC_MAP.put("I", "··");
        CODEC_MAP.put("J", "·---");
        CODEC_MAP.put("K", "-·-");
        CODEC_MAP.put("L", "·-··");
        CODEC_MAP.put("M", "--");
        CODEC_MAP.put("N", "-·");
        CODEC_MAP.put("O", "---");
        CODEC_MAP.put("P", "·--·");
        CODEC_MAP.put("Q", "--·-");
        CODEC_MAP.put("R", "·-·");
        CODEC_MAP.put("S", "···");
        CODEC_MAP.put("T", "-");
        CODEC_MAP.put("U", "··-");
        CODEC_MAP.put("V", "···-");
        CODEC_MAP.put("W", "·--");
        CODEC_MAP.put("X", "-··-");
        CODEC_MAP.put("Y", "-·--");
        CODEC_MAP.put("Z", "--··");
        CODEC_MAP.put("CH","----");
        CODEC_MAP.put("0", "-----");
        CODEC_MAP.put("1", "·----");
        CODEC_MAP.put("2", "··---");
        CODEC_MAP.put("3", "···--");
        CODEC_MAP.put("4", "····-");
        CODEC_MAP.put("5", "·····");
        CODEC_MAP.put("6", "-····");
        CODEC_MAP.put("7", "--···");
        CODEC_MAP.put("8", "---··");
        CODEC_MAP.put("9", "----·");
    }

    @Override
    public String encodeText(String text)
    {
        StringBuilder encoded = new StringBuilder();
        Pattern validGroups = Pattern.compile("(ch|\\w|\\s)", Pattern.CASE_INSENSITIVE);
        Matcher match = validGroups.matcher(text);
        while(match.find())
        {
            String tmp = match.group().toUpperCase();
            encoded.append("/");
            if (!tmp.matches("\\s"))
            {
                encoded.append(CODEC_MAP.get(tmp));
            }
        }
        encoded.append("/");
        return encoded.toString();
    }

    @Override
    public String decodeText(String text)
    {
        StringBuilder decoded = new StringBuilder();
        Pattern validGroups = Pattern.compile("([·-]+|(\\/\\/))", Pattern.CASE_INSENSITIVE);
        Matcher match = validGroups.matcher(text.replaceAll("\\.", "·"));
        while(match.find())
        {
            String tmp = match.group().replaceAll("/", "");
            if (tmp.isBlank())
            {
                decoded.append(" ");
            }
            else
            {
                decoded.append(CODEC_MAP.getKey(tmp));
            }
        }
        return decoded.toString();
    }
    
}
