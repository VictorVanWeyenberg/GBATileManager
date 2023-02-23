package dagger;

import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;

@Module
public abstract class LoginCommandModule {
    @Binds
    @IntoMap
    @StringKey("login")
    abstract Command loginCommand(LoginCommand loginCommand);
}
