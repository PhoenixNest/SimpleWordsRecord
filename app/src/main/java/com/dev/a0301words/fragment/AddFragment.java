package com.dev.a0301words.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.dev.a0301words.R;
import com.dev.a0301words.databinding.FragmentAddBinding;
import com.dev.a0301words.db.Word;
import com.dev.a0301words.viewmodel.WordViewModel;

import org.jetbrains.annotations.NotNull;

public class AddFragment extends Fragment {

    private FragmentAddBinding binding;
    private WordViewModel wordViewModel;

    private InputMethodManager inputMethodManager;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add, container, false);
        binding.setLifecycleOwner(requireActivity());

        return binding.getRoot();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        wordViewModel = ViewModelProviders.of(requireActivity()).get(WordViewModel.class);

        binding.btnSubmit.setEnabled(false);

//        进入该界面时自动打开键盘并将焦点设置到EditText上
        binding.edtEn.requestFocus();
        inputMethodManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(binding.edtEn, 0);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String edtEn = binding.edtEn.getText().toString().trim();
                String edtZh = binding.edtZh.getText().toString().trim();

                binding.btnSubmit.setEnabled(!edtEn.isEmpty() && !edtZh.isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        binding.edtEn.addTextChangedListener(textWatcher);
        binding.edtZh.addTextChangedListener(textWatcher);

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String edtEn = binding.edtEn.getText().toString().trim();
                String edtZh = binding.edtZh.getText().toString().trim();

                Word word = new Word(edtEn, edtZh);
                wordViewModel.insertWords(word);

                Navigation.findNavController(v).navigateUp();

//                收起键盘
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });
    }
}
