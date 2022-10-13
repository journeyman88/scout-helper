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
package net.unknowndomain.scouthelper.daemon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import net.unknowndomain.scouthelper.commands.ScoutHelperCommands;
import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.interaction.ApplicationCommandBuilder;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.util.logging.ExceptionLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author journeyman
 */
public class ScoutDaemon implements Daemon
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ScoutDaemon.class);
    
    private ScoutConfig scoutConfig;
    private List<DiscordApi> shards;
    
    public ScoutDaemon()
    {
        
    }
    
    public ScoutDaemon(ScoutConfig scoutConfig)
    {
        this.scoutConfig = scoutConfig;
        this.shards = Collections.synchronizedList(new LinkedList<>());
    }
    
    @Override
    public void init(DaemonContext dc) throws DaemonInitException, Exception
    {
        scoutConfig = null;
        shards = Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    public void start() throws Exception
    {
        DiscordApiBuilder apiBuilder = new DiscordApiBuilder();
        apiBuilder.setToken(scoutConfig.getDiscordToken());
        apiBuilder.addListener(new ScoutHelperCommands());
        apiBuilder.setRecommendedTotalShards().join();
        apiBuilder.loginAllShards().forEach(shardFuture -> shardFuture.thenAccept(
                api -> {
                    LOGGER.info(api.createBotInvite());
                    if (shards.isEmpty())
                    {
                        shards.add(api);
//                        List<ApplicationCommandBuilder> commands = new LinkedList<>();
//                        commands.add(setupExprCommand());
//                        api.bulkOverwriteGlobalApplicationCommands(commands).join();
                    }
                    else
                    {
                        shards.add(api);
                    }
                }
            ).exceptionally(ExceptionLogger.get())
        );
    }

    @Override
    public void stop() throws Exception
    {
        shards.forEach(api ->
        {
            api.disconnect();
        });
    }

    @Override
    public void destroy()
    {
        scoutConfig = null;
        shards = null;
    }
    
}
