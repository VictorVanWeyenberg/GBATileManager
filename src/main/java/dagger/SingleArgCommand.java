package dagger;

import java.util.List;

/** Abstract command that accepts a single argument. */
abstract class SingleArgCommand implements Command {

    @Override
    public final Status handleInput(List<String> input) {
        return input.size() == 1 ? handleArg(input.get(0)) : Status.INVALID;
    }

    /** Handles the single argument to the command. */
    protected abstract Status handleArg(String arg);
}
