package com.example.recipebook.home;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.recipebook.R;
/**
 * HealthyFragment is a static fragment that displays information and images about
 * having a healthy and balanced diet. It is loaded at main activity when the health option
 * of BottomNavigationView is clicked.
 * @author MarioG
 */
public class HealthyFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_healthy, container, false);
    }
}