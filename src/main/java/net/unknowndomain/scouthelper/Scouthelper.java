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

import net.unknowndomain.scouthelper.codecs.tree.TreeCodec;
import net.unknowndomain.scouthelper.codecs.TextCodec;
import net.unknowndomain.scouthelper.codecs.shift.ShiftCodec;
import net.unknowndomain.scouthelper.codecs.MorseCodec;

/**
 *
 * @author m.bignami
 */
public class Scouthelper {

    public static void main(String[] args) {
        TextCodec morse = new MorseCodec();
        String enc = morse.encodeText("Chocolate 123");
        System.out.println(enc);
        String nenc = enc.replaceAll("·", ".").substring(1);
        System.out.println(nenc);
        String dec = morse.decodeText(nenc);
        System.out.println(dec);
        String seed = ShiftCodec.seed(1, 0, 2, 8, ShiftCodec.Flags.MIRROR_LEFT);
        System.out.println(seed);
        TextCodec alg1 = ShiftCodec.compile(seed);
        System.out.println(alg1.encodeText("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
        seed = ShiftCodec.seed(1, 0, 2, 10, ShiftCodec.Flags.MIRROR_LEFT, ShiftCodec.Flags.VERTICAL_FLIP_POST_INIT);
        System.out.println(seed);
        alg1 = ShiftCodec.compile(seed);
        System.out.println(alg1.encodeText("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
        alg1 = TreeCodec.build(1, 5, 1, 10);
        System.out.println(alg1.encodeText("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
    }
}
