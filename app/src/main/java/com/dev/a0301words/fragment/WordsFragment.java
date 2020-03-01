package com.dev.a0301words.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dev.a0301words.R;
import com.dev.a0301words.adapter.RVAdapter;
import com.dev.a0301words.databinding.FragmentWordsBinding;
import com.dev.a0301words.db.Word;
import com.dev.a0301words.viewmodel.WordViewModel;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WordsFragment extends Fragment {
    private static final String SP_NAME = "mySP";
    private static final String isUseCard = "is_use_card";

    private FragmentWordsBinding binding;
    private RVAdapter rvAdapter, rvCardAdapter;

    private WordViewModel wordViewModel;
    private LiveData<List<Word>> searchWordList;

    private List<Word> wordsList;

    private boolean undoAction = false;
    private DividerItemDecoration dividerItemDecoration;

    public WordsFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_words, container, false);
        binding.setLifecycleOwner(requireActivity());

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        wordViewModel = ViewModelProviders.of(requireActivity()).get(WordViewModel.class);

        rvAdapter = new RVAdapter(false, wordViewModel);
        rvCardAdapter = new RVAdapter(true, wordViewModel);
        binding.rvWords.setItemAnimator(new DefaultItemAnimator() {
            @Override
            public void onAnimationFinished(@NonNull RecyclerView.ViewHolder viewHolder) {
                super.onAnimationFinished(viewHolder);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) binding.rvWords.getLayoutManager();
                if (linearLayoutManager != null) {
                    int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                    int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
                    for (int i = firstVisibleItemPosition; i <= lastVisibleItemPosition; i++) {
                        RVAdapter.MyViewHolder holder = (RVAdapter.MyViewHolder) binding.rvWords.findViewHolderForAdapterPosition(i);
                        if (holder != null) {
                            holder.tv_id.setText(String.valueOf(i + 1));
                        }
                    }
                }

            }
        });

        binding.rvWords.setLayoutManager(new LinearLayoutManager(requireActivity()));
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        boolean viewType = sharedPreferences.getBoolean(isUseCard, false);
        dividerItemDecoration = new DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL);

        if (viewType) {
            binding.rvWords.setAdapter(rvCardAdapter);
        } else {
            binding.rvWords.addItemDecoration(dividerItemDecoration);
            binding.rvWords.setAdapter(rvAdapter);
        }

//        绑定RV左右滑删除
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                0,
//                ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                ItemTouchHelper.START | ItemTouchHelper.END) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//                Word wordFrom = wordsList.get(viewHolder.getAdapterPosition());
//                Word wordTo = wordsList.get(target.getAdapterPosition());
//
//                int id = wordFrom.getId();
//                wordFrom.setId(wordTo.getId());
//                wordTo.setId(id);
//
//                wordViewModel.updateWords(wordFrom, wordTo);
//                rvAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
//                rvCardAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final Word word = wordsList.get(viewHolder.getAdapterPosition());
                wordViewModel.deleteWords(word);

                Snackbar.make(requireActivity().findViewById(R.id.coor_words), "Delete A Words", Snackbar.LENGTH_SHORT)
                        .setAction("Cancel", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                undoAction = true;
                                wordViewModel.insertWords(word);
                            }
                        })
                        .show();
            }

            //            添加删除背景
            Drawable icon = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_delete_forever_black_24dp);
            ColorDrawable backgroundColor = new ColorDrawable(Color.LTGRAY);

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                View itemView = viewHolder.itemView;
                int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;

                int iconLeft, iconRight, iconTop, iconBottom;
                int backTop, backBottom, backLeft, backRight;
                backTop = itemView.getTop();
                backBottom = itemView.getBottom();
                iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                iconBottom = iconTop + icon.getIntrinsicHeight();

                if (dX > 0) {
                    backLeft = itemView.getLeft();
                    backRight = itemView.getLeft() + (int) dX;
                    backgroundColor.setBounds(backLeft, backTop, backRight, backBottom);
                    iconLeft = itemView.getLeft() + iconMargin;
                    iconRight = iconLeft + icon.getIntrinsicWidth();
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                } else if (dX < 0) {
                    backRight = itemView.getRight();
                    backLeft = itemView.getRight() + (int) dX;
                    backgroundColor.setBounds(backLeft, backTop, backRight, backBottom);
                    iconRight = itemView.getRight() - iconMargin;
                    iconLeft = iconRight - icon.getIntrinsicWidth();
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                } else {
                    backgroundColor.setBounds(0, 0, 0, 0);
                    icon.setBounds(0, 0, 0, 0);
                }
                backgroundColor.draw(c);
                icon.draw(c);
            }

        }).attachToRecyclerView(binding.rvWords);

//       init RVData
        searchWordList = wordViewModel.getLiveWordsList();
        searchWordList.observe(getViewLifecycleOwner(), new Observer<List<Word>>() {
            @Override
            public void onChanged(List<Word> words) {
                int temp = rvAdapter.getItemCount();
                wordsList = words;
                if (temp != words.size()) {
                    if (temp < words.size() && undoAction) {
                        binding.rvWords.smoothScrollBy(0, -200);
                    }
                    rvAdapter.submitList(words);
                    rvCardAdapter.submitList(words);
                }
            }
        });

        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_wordsFragment_to_addFragment);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);

//        搜索监听
        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setMaxWidth(700);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String inputWords = newText.trim();

                searchWordList.removeObservers(requireActivity());
                searchWordList = wordViewModel.findWords(inputWords);
                searchWordList.observe(getViewLifecycleOwner(), new Observer<List<Word>>() {
                    @Override
                    public void onChanged(List<Word> words) {
                        int temp = rvAdapter.getItemCount();

                        if (temp != words.size()) {
                            rvAdapter.submitList(words);
                            rvCardAdapter.submitList(words);
                        }
                    }
                });
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_clear:
                new AlertDialog.Builder(requireActivity())
                        .setTitle("Are You Sure ?")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                wordViewModel.deleteALlWords();
                            }
                        })
                        .create()
                        .show();
                break;

            case R.id.menu_switch_view:
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
                boolean viewType = sharedPreferences.getBoolean(isUseCard, false);
                SharedPreferences.Editor edit = sharedPreferences.edit();

                if (viewType) {
                    binding.rvWords.setAdapter(rvAdapter);
                    binding.rvWords.addItemDecoration(dividerItemDecoration);
                    edit.putBoolean(isUseCard, false);
                } else {
                    binding.rvWords.setAdapter(rvCardAdapter);
                    binding.rvWords.removeItemDecoration(dividerItemDecoration);
                    edit.putBoolean(isUseCard, true);
                }

                edit.apply();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onResume() {
//        在主界面的时候收起已打开的键盘
        InputMethodManager inputMethodManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        super.onResume();
    }
}
