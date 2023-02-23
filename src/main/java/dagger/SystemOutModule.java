package dagger;

@Module
public class SystemOutModule {
    @Provides
    static Outputter textOutputter() {
        return System.out::println;
    }
}