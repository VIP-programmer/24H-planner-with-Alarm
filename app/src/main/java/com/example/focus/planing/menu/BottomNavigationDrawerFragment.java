package com.example.focus.planing.menu;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.focus.R;
import com.example.focus.Settings;
import com.example.focus.Support;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BottomNavigationDrawerFragment extends BottomSheetDialogFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_bottomsheet,container,false);
        NavigationView navigationView=view.findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.Setting:
                        Intent intent =new Intent(getContext(), Settings.class);
                        startActivity(intent);
                        break;
                    case R.id.Support:
                        Intent intent1 =new Intent(getContext(), Support.class);
                        startActivity(intent1);
                        break;
                }
                return true;
            }
        });
        return view;
    }
}
