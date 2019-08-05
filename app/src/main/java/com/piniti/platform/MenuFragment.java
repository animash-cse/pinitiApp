package com.piniti.platform;


import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.piniti.platform.Activity.AllPeople;


/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment {

    View view;
    private ImageView people;
    public MenuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_menu, container, false);

        people = (ImageView) view.findViewById(R.id.people);
        people.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AllPeople.class);
                startActivity(intent);
            }
        });

        // in Activity Context
        final ImageView fabIcon = new ImageView(getActivity()); // Create an icon
        fabIcon.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_action_fab));
        fabIcon.setColorFilter(Color.BLUE, PorterDuff.Mode.MULTIPLY);

        FloatingActionButton actionButton = new FloatingActionButton.Builder(getActivity())
                .setContentView(fabIcon)
                .setPosition(FloatingActionButton.POSITION_BOTTOM_RIGHT)
                .build();
        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(getActivity());
// repeat many times:
        ImageView itemIcon = new ImageView(getActivity());
        ImageView itemIcon1 = new ImageView(getActivity());
        ImageView itemIcon2 = new ImageView(getActivity());
        ImageView itemIcon3 = new ImageView(getActivity());
        itemIcon.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_menu_camera));
        itemIcon1.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_menu_share));
        itemIcon2.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_menu_send));
        itemIcon3.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_menu_manage));

        final FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(getActivity())
                .addSubActionView(itemBuilder.setContentView(itemIcon).build())
                .addSubActionView(itemBuilder.setContentView(itemIcon1).build())
                .addSubActionView(itemBuilder.setContentView(itemIcon2).build())
                .addSubActionView(itemBuilder.setContentView(itemIcon3).build())
                .attachTo(actionButton)
                .build();

        actionMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu floatingActionMenu) {
                fabIcon.setRotation(0);
                PropertyValuesHolder propertyValuesHolder = PropertyValuesHolder.ofFloat(View.ROTATION, 45);
                ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(fabIcon, propertyValuesHolder);
                animator.start();
            }

            @Override
            public void onMenuClosed(FloatingActionMenu floatingActionMenu) {
                fabIcon.setRotation(45);
                PropertyValuesHolder propertyValuesHolder = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(fabIcon, propertyValuesHolder);
                animator.start();
            }
        });
//        SubActionButton button1 = itemBuilder.setContentView(itemIcon).build();
        return view;

    }



}
