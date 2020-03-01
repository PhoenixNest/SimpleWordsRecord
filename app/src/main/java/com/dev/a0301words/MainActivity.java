package com.dev.a0301words;

import android.content.Context;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.dev.a0301words.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setLifecycleOwner(this);

        navController = Navigation.findNavController(findViewById(R.id.fragment));
        NavigationUI.setupActionBarWithNavController(this, navController);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean onSupportNavigateUp() {
//        在主界面的时候收起已打开的键盘
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(findViewById(R.id.fragment).getWindowToken(), 0);
        return navController.navigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        navController.navigateUp();
    }

}
