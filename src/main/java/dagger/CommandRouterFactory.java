package dagger;

import javax.inject.Singleton;

@Singleton
@Component(modules = {
        DepositCommandModule.class,
        HelloWorldModule.class,
        LoginCommandModule.class,
        SystemOutModule.class })
public interface CommandRouterFactory {
    CommandRouter router();
}
