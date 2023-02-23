package dagger;

import java.util.List;

public interface Command {
    enum Status {
        INVALID, HANDLED;
    }
    Status handleInput(List<String> input);

}
