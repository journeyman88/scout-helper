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
package net.unknowndomain.scouthelper.commands;

import java.util.Optional;
import net.unknowndomain.scouthelper.codecs.MorseCodec;
import net.unknowndomain.scouthelper.codecs.TextCodec;
import net.unknowndomain.scouthelper.codecs.shift.ShiftCodec;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author m.bignami
 */
public class ScoutHelperCommands implements SlashCommandCreateListener
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ScoutHelperCommands.class);
    private static final TextCodec MORSE_CODEC = new MorseCodec();

    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event)
    {
        SlashCommandInteraction interaction = event.getSlashCommandInteraction();
        String commandName = interaction.getCommandName();
        Optional<String> operation = interaction.getOptions().get(0).getStringValue();
        Optional<String> message = interaction.getOptions().get(0).getOptionStringValueByName("text");
        TextCodec selectedCodec = null;
        OperationMode mode = null;
        if (operation.isPresent())
        {
            OperationMode.valueOf(operation.get().toUpperCase());
        }
        String text = "";
        if (message.isPresent())
        {
            text = message.get();
        }
        if ("morse".equalsIgnoreCase(commandName))
        {
            selectedCodec = MORSE_CODEC;
        }
        if ("cipher".equalsIgnoreCase(commandName))
        {
            Optional<String> seed = interaction.getOptions().get(0).getOptionStringValueByName("seed");
            if (seed.isPresent())
            {
                selectedCodec = ShiftCodec.compile(seed.get());
            }
        }
        
        String result = "";
        if (selectedCodec != null)
        {
            if (mode == OperationMode.ENCODE)
            {
                result = selectedCodec.encodeText(text);
            }
            if (mode == OperationMode.DECODE)
            {
                result = selectedCodec.decodeText(text);
            }
        }
        InteractionImmediateResponseBuilder responder = interaction.createImmediateResponder();
        responder.append(result);
        responder.respond();
    }
}
