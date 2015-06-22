package com.mortarnav;

import com.mortarnav.path.HomePath;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import autodagger.AutoComponent;
import autodagger.AutoInjector;
import butterknife.ButterKnife;
import butterknife.InjectView;
import dagger.Provides;
import mortar.MortarScope;
import architect.StackPath;
import architect.Navigator;
import architect.NavigatorView;
import architect.Transition;
import architect.autostack.DaggerService;
import architect.commons.ArchitectActivity;
import architect.transition.Config;
import architect.transition.HorizontalScreenTransition;

/**
 * Root activity example, using ArchitectActivity base class
 *
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
@AutoComponent(
        dependencies = App2.class,
        modules = MainActivity2.NavigatorModule.class
)
@AutoInjector
@DaggerScope(MainActivity2.class)
public class MainActivity2 extends ArchitectActivity {

    @Inject
    List<Transition> navigatorTransitions;

    @InjectView(R.id.navigator_container)
    protected NavigatorView containerView;

    @Override
    protected void createContentView() {
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
    }

    @Override
    protected void configureScope(MortarScope.Builder builder, MortarScope parentScope) {
        MainActivity2Component component = DaggerMainActivity2Component.builder()
                .app2Component(parentScope.<App2Component>getService(DaggerService.SERVICE_NAME))
                .navigatorModule(new NavigatorModule())
                .build();
        component.inject(this);

        builder.withService(DaggerService.SERVICE_NAME, component);
    }

    @Override
    protected void configureNavigator(Navigator navigator) {
        navigator.transitions().register(navigatorTransitions);
    }

    @Override
    protected NavigatorView getNavigatorView() {
        return containerView;
    }

    @Override
    protected StackPath getInitialPath() {
        return new HomePath("Initial home");
    }

    @dagger.Module
    public static class NavigatorModule {

        @Provides
        @DaggerScope(MainActivity2.class)
        public List<Transition> providesTransitions() {
            List<Transition> transitions = new ArrayList<>();
            transitions.add(Transition.defaultTransition(new HorizontalScreenTransition(new Config().duration(300))));

            return transitions;
        }
    }
}
