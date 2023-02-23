package dagger;

import java.util.*;
import java.util.stream.Collectors;

import dagger.Command.Status;

import javax.inject.Inject;

public final class CommandRouter {
    private final Map<String, Command> commands;

    @Inject CommandRouter(Map<String, Command> commands) {
        this.commands = commands;
    }

    Command.Status route(String input ) {
        List<String> splitInput = split(input);
        if (splitInput.isEmpty()) {
            return invalidCommand(input);
        }

        String commandKey = splitInput.get(0);
        Command command = commands.get(commandKey);
        if (command == null) {
            return invalidCommand(input);
        }

        Status status =
                command.handleInput(splitInput.subList(1, splitInput.size()));
        if (status == Status.INVALID) {
            System.out.println(commandKey + ": invalid arguments");
        }
        return status;
    }

    private Status invalidCommand(String input) {
        System.out.println(
                String.format("couldn't understand \"%s\". please try again.", input));
        return Status.INVALID;
    }

    private static List<String> split(String string) {
        return Arrays.stream(string.split(" ")).collect(Collectors.toList());
    }
}
