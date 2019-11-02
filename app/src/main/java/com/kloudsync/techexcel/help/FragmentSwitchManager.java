package com.kloudsync.techexcel.help;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class FragmentSwitchManager implements View.OnClickListener {

    private FragmentManager fragmentManager;
    private Fragment currentFragment;
    private View[] currentSelectedView;
    private View[] clickableViews;
    private List<View[]> selectedViews;
    private Class<? extends Fragment>[] fragments;
    private Bundle[] bundles;
    private int containerId;

    public FragmentSwitchManager(FragmentManager fragmentManager, int containerId) {
        super();
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;
    }

    @Override
    public void onClick(View view) {
        changeFragment(view);
    }


    public void setClickableViews(View... clickableViews) {
        this.clickableViews = clickableViews;
        for (View view : clickableViews) {
            view.setOnClickListener(this);
        }
    }

    public void setSelectedViews(List<View[]> selectedViews) {
        this.selectedViews = selectedViews;
    }

    public FragmentSwitchManager addSelectedViews(View... views) {
        if (selectedViews == null) {
            selectedViews = new ArrayList<View[]>();
        }
        selectedViews.add(views);
        return this;
    }

    public void setFragments(Class<? extends Fragment>... fragments) {
        this.fragments = fragments;
    }

    public void setBundles(Bundle... bundles) {
        this.bundles = bundles;
    }

    public void changeFragment(View v) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(String.valueOf(v.getId()));
        for (int i = 0; i < clickableViews.length; i++) {
            if (v.getId() == clickableViews[i].getId()) {
                if (fragment == null) {
                    if (currentFragment != null) {
                        fragmentTransaction.hide(currentFragment);
                        for (View view : currentSelectedView) {
                            view.setSelected(false);
                        }
                    }
                    try {
                        fragment = fragments[i].newInstance();
                        if (bundles != null && bundles[i] != null) {
                            fragment.setArguments(bundles[i]);
                        }

                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    fragmentTransaction.add(containerId, fragment, String.valueOf(clickableViews[i].getId()));
                } else if (fragment == currentFragment) {

                } else {
                    fragmentTransaction.hide(currentFragment);
                    for (View view : currentSelectedView) {
                        view.setSelected(false);
                    }
                    fragmentTransaction.show(fragment);
                }

                fragmentTransaction.commit();
                currentFragment = fragment;
                for (View view : selectedViews.get(i)) {
                    view.setSelected(true);
                }
                currentSelectedView = selectedViews.get(i);
                break;
            }
        }
    }


}

