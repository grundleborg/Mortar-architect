package com.mortarnav.presenter;

import android.os.Bundle;

import com.mortarnav.DaggerScope;
import com.mortarnav.stack.HomeStackScope;
import com.mortarnav.view.HomeSubcontentView;

import java.util.Random;

import javax.inject.Inject;

import autodagger.AutoComponent;
import mortar.ViewPresenter;
import architect.autostack.AutoStack;
import timber.log.Timber;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
@AutoStack(
        component = @AutoComponent(dependencies = HomeStackScope.Component.class)
)
@DaggerScope(HomeSubcontentPresenter.class)
public class HomeSubcontentPresenter extends ViewPresenter<HomeSubcontentView> {

    private final int random;

    @Inject
    public HomeSubcontentPresenter() {
        random = new Random().nextInt(100);
    }

    @Override
    protected void onLoad(Bundle savedInstanceState) {
        int r = savedInstanceState != null ? savedInstanceState.getInt("random") : random;
        getView().randomTextView.setText("Random " + r);
    }

    @Override
    protected void onSave(Bundle outState) {
        outState.putInt("random", random);
    }

    public void click() {
        Timber.d("Clicked on home subcontent");
    }
}
