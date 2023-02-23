package dagger;

import javax.inject.Inject;

final class LoginCommand extends SingleArgCommand {
    private final Database database;
    private final Outputter outputter;

    @Inject
    LoginCommand(Database database, Outputter outputter) {
        this.database = database;
        this.outputter = outputter;
    }

    @Override
    public Status handleArg(String username) {
        Database.Account account = database.getAccount(username);
        outputter.output(
                username + " is logged in with balance: " + account.balance());
        return Status.HANDLED;
    }
}

