/*
 * Copyright (c) 2018 superblaubeere27
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package cn.hanabi.command.commands;

import cn.hanabi.command.Command;
import cn.hanabi.command.CommandException;
import cn.hanabi.events.EventKey;
import cn.hanabi.modules.Mod;
import cn.hanabi.modules.ModManager;
import cn.hanabi.utils.game.ChatUtils;
import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class BindCommand extends Command {
    private boolean active = false;
    @Nullable
    private Mod currentModule = null;

    public BindCommand() {
        super("bind");

        EventManager.register(this);
    }

    @Override
    public void run(String alias, @NotNull String[] args) {
        if (args.length == 0) {
            throw new CommandException("Usage: ." + alias + " <module> [<none/show>]");
        }

        Mod mod = ModManager.getModule(args[0], false);

        if (mod == null) throw new CommandException("The module '" + args[0] + "' does not exist");

        if (args.length > 1) {
            if (args[1].equalsIgnoreCase("none")) {
                mod.setKeybind(Keyboard.KEY_NONE);
                ChatUtils.success(ChatUtils.SECONDARY_COLOR + mod.getName() + ChatUtils.PRIMARY_COLOR + " was bound to " + ChatUtils.SECONDARY_COLOR + "NONE");
            } else if (args[1].equalsIgnoreCase("show")) {
                ChatUtils.success(ChatUtils.SECONDARY_COLOR + mod.getName() + ChatUtils.PRIMARY_COLOR + " is bound to " + ChatUtils.SECONDARY_COLOR + Keyboard.getKeyName(mod.getKeybind()));
            } else {
                int key = Keyboard.getKeyIndex(args[1].toUpperCase());
                mod.setKeybind(key);
                ChatUtils.success(ChatUtils.SECONDARY_COLOR + mod.getName() + ChatUtils.PRIMARY_COLOR + " was bound to " + ChatUtils.SECONDARY_COLOR + Keyboard.getKeyName(key));
            }

            return;
        }

        active = true;
        currentModule = mod;

        ChatUtils.info("Please press a key");
    }

    @Override
    public List<String> autocomplete(int arg, String[] args) {
        String prefix = "";
        boolean flag = false;

        if (arg == 0 || args.length == 0) {
            flag = true;
        } else if (arg == 1) {
            flag = true;
            prefix = args[0];
        }

        if (flag) {
            String finalPrefix = prefix;
            return ModManager.getModules().stream().map(Mod::getName).filter(name -> name.toLowerCase().startsWith(finalPrefix)).collect(Collectors.toList());
        } else if (arg == 2) {
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add("none");
            arrayList.add("show");
            return arrayList;
        } else return new ArrayList<>();
    }

    @EventTarget
    public void onKey(@NotNull EventKey event) {
        if (active) {
            currentModule.setKeybind(event.getKey());

            ChatUtils.success(ChatUtils.SECONDARY_COLOR + currentModule.getName() + ChatUtils.PRIMARY_COLOR + " was bound to " + ChatUtils.SECONDARY_COLOR + Keyboard.getKeyName(event.getKey()));

            active = false;
            currentModule = null;
        }
    }
}
