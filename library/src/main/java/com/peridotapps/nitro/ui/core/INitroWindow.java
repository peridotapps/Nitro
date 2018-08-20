package com.peridotapps.nitro.ui.core;

import android.support.annotation.MenuRes;
import android.support.annotation.Nullable;

import com.peridotapps.nitro.ui.view.NitroProgressView;

public interface INitroWindow extends INitroView {

    void attachLayout();

    @MenuRes
    Integer getMenuResourceId();

    @Nullable
    default NitroProgressView getProgressView() {
        return null;
    }

    default void showProgressView() {
        if (getProgressView() != null) {
            getProgressView().showProgressView();
        }
    }

    default void hideProgressView() {
        if (getProgressView() != null) {
            getProgressView().hideProgressView();
        }
    }

}
