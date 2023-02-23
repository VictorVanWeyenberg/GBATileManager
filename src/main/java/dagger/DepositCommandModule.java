package dagger;

import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;

@Module
public abstract class DepositCommandModule {
    @Binds
    @IntoMap
    @StringKey("deposit")
    abstract Command depositCommand(DepositCommand depositCommand);
}
