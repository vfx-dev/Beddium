/*
 * Beddium
 *
 * Copyright (C) 2025 Ven, FalsePattern
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, only version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.ventooth.beddium.modules.TerrainRendering.command;

import com.ventooth.beddium.modules.TerrainRendering.CeleritasWorldRenderer;
import org.embeddedt.embeddium.impl.render.chunk.terrain.TerrainRenderPass;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class TogglePassCommand extends CommandBase {
    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getCommandName() {
        return "celeritas_toggle_pass";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/celeritas_toggle_pass [pass_name]";
    }

    private static Stream<TerrainRenderPass> getAllPasses() {
        var renderer = CeleritasWorldRenderer.instanceNullable();

        if (renderer == null) {
            return Stream.empty();
        }

        return renderer.getRenderPassConfiguration().getAllKnownRenderPasses();
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender iCommandSender, String[] args) {
        return new ArrayList<>(getAllPasses().map(TerrainRenderPass::name).toList());
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.addChatMessage(new ChatComponentText("Pass name must be provided"));
            return;
        }

        Optional<TerrainRenderPass> foundPass = getAllPasses().filter(pass -> pass.name().equals(args[0])).findFirst();
        if (foundPass.isPresent()) {
            CeleritasWorldRenderer.instance().getRenderSectionManager().toggleRenderingForTerrainPass(foundPass.get());
        } else {
            sender.addChatMessage(new ChatComponentText("Pass " + args[0] + " not found"));
        }
    }

    @Override
    public int compareTo(Object iCommand) {
        return this.getCommandName().compareTo(((ICommand) iCommand).getCommandName());
    }
}
