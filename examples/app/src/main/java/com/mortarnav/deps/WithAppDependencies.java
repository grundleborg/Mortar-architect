package architect.examples.simple_app.deps;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
public interface WithAppDependencies {

    RestClient restClient();

    UserManager userManager();
}