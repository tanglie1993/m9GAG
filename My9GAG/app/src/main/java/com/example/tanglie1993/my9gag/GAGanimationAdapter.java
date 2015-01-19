package com.example.tanglie1993.my9gag;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.nhaarman.listviewanimations.appearance.AnimationAdapter;

/**
 * Created by tanglie1993 on 2015/1/19.
 */
public class GAGanimationAdapter extends AnimationAdapter {

    public GAGanimationAdapter(BaseAdapter baseAdapter) {
        super(baseAdapter);
    }

    @Override
    public Animator[] getAnimators(ViewGroup parent, View view) {
        Animator bottomInAnimator = ObjectAnimator.ofFloat(view, "translationY", 500, 0);
        Animator rightInAnimator = ObjectAnimator.ofFloat(view, "translationX", parent.getWidth(), 0);
        return new Animator[] { bottomInAnimator, rightInAnimator };
    }

}