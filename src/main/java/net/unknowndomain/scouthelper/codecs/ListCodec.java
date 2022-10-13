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
package net.unknowndomain.scouthelper.codecs;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author journeyman
 */
public abstract class ListCodec implements TextCodec
{
    protected static final List<String> NORMAL_ALPHABET = Collections.unmodifiableList(Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"));
    private final BidiMap<String, String> codecMap;
    private final Pattern validPlainInput;
    private final Pattern validEncodedInput;
    private final String separator;
    private final boolean caseSensitive;
    
    protected ListCodec(List<String> encodedAlphabet)
    {
        this(NORMAL_ALPHABET, encodedAlphabet, '/', false);
    }
    
    protected ListCodec(List<String> decodedAlphabet, List<String> encodedAlphabet)
    {
        this(decodedAlphabet, encodedAlphabet, '/', false);
    }
    
    protected ListCodec(List<String> decodedAlphabet, List<String> encodedAlphabet, Character separator, boolean caseSensitive)
    {
        if ((decodedAlphabet == null) || (encodedAlphabet == null) || (decodedAlphabet.size() != encodedAlphabet.size()))
        {
            throw new RuntimeException();
        }
        
        this.separator = "" + separator;
        this.codecMap = new DualHashBidiMap<>();
        StringBuilder plainPattern = new StringBuilder("(\\s");
        StringBuilder encodedPattern = new StringBuilder("(").append(Pattern.quote(this.separator + this.separator));
        for (int i=0; i<decodedAlphabet.size(); i++)
        {
            plainPattern.append("|").append(decodedAlphabet.get(i));
            encodedPattern.append("|").append(encodedAlphabet.get(i));
            this.codecMap.put(decodedAlphabet.get(i), encodedAlphabet.get(i));
        }
        plainPattern.append(")");
        encodedPattern.append(")");
        int flag = Pattern.CASE_INSENSITIVE;
        this.caseSensitive = caseSensitive;
        if (this.caseSensitive)
        {
            flag = 0;
        }
        validPlainInput = Pattern.compile(plainPattern.toString(), flag);
        validEncodedInput = Pattern.compile(encodedPattern.toString());
    }
    
    protected ListCodec(BidiMap<String, String> codecMap, String plainRegex, String codedRegex, Character separator, boolean caseSensitive)
    {
        if ((codecMap == null) || (separator == null) || StringUtils.isAnyBlank(plainRegex, codedRegex))
        {
            throw new RuntimeException();
        }
        this.separator = "" + separator;
        this.codecMap = codecMap;
        int flag = Pattern.CASE_INSENSITIVE;
        this.caseSensitive = caseSensitive;
        if (this.caseSensitive)
        {
            flag = 0;
        }
        validPlainInput = Pattern.compile(plainRegex, flag);
        validEncodedInput = Pattern.compile(codedRegex);
    }
    
    protected String encoderPreprocessor(String text)
    {
        return text;
    }
    protected String decoderPreprocessor(String text)
    {
        return text;
    }
    
    @Override
    public final String encodeText(String text)
    {
        StringBuilder encoded = new StringBuilder();
        Matcher match = validPlainInput.matcher(encoderPreprocessor(text));
        while(match.find())
        {
            String tmp = match.group();
            if (!this.caseSensitive)
            {
                tmp = tmp.toUpperCase();
            }
            encoded.append(this.separator);
            if (!tmp.matches("\\s"))
            {
                encoded.append(codecMap.get(tmp));
            }
        }
        encoded.append("/");
        return encoded.toString();
    }

    @Override
    public final String decodeText(String text)
    {
        StringBuilder decoded = new StringBuilder();
        Matcher match = validEncodedInput.matcher(decoderPreprocessor(text));
        while(match.find())
        {
            String tmp = match.group().replaceAll(this.separator, "");
            if (tmp.isBlank())
            {
                decoded.append(" ");
            }
            else
            {
                decoded.append(codecMap.getKey(tmp));
            }
        }
        return decoded.toString();
    }
    
}
