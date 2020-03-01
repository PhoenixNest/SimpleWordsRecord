package com.dev.a0301words.adapter;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.dev.a0301words.R;
import com.dev.a0301words.db.Word;
import com.dev.a0301words.viewmodel.WordViewModel;

public class RVAdapter extends ListAdapter<Word, RVAdapter.MyViewHolder> {

    private boolean isUseCard;
    private WordViewModel wordViewModel;

    public RVAdapter(boolean isUseCard, WordViewModel wordViewModel) {
        super(new DiffUtil.ItemCallback<Word>() {
            @Override
            public boolean areItemsTheSame(@NonNull Word oldItem, @NonNull Word newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Word oldItem, @NonNull Word newItem) {
                return (oldItem.getWord().equals(newItem.getWord())
                        && oldItem.getChineseMeaning().equals(newItem.getChineseMeaning())
                        && oldItem.isChineseInvisible() == newItem.isChineseInvisible());
            }
        });

        this.isUseCard = isUseCard;
        this.wordViewModel = wordViewModel;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (isUseCard) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_card_new, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_new, parent, false);
        }

        final MyViewHolder holder = new MyViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://m.youdao.com/dict?le=eng&q=" + holder.tv_en.getText());
                holder.itemView.getContext().startActivity(new Intent(Intent.ACTION_VIEW).setData(uri));
            }
        });

        holder.switch_zh_enable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Word word = (Word) holder.itemView.getTag(R.id.word_for_view_holder);
                if (isChecked) {
                    holder.tv_zh.setVisibility(View.GONE);
                    word.setChineseInvisible(true);
                    wordViewModel.updateWords(word);
                } else {
                    holder.tv_zh.setVisibility(View.VISIBLE);
                    word.setChineseInvisible(false);
                    wordViewModel.updateWords(word);
                }
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final Word word = getItem(position);
        holder.itemView.setTag(R.id.word_for_view_holder, word);

        holder.tv_id.setText(String.valueOf(position + 1));
        holder.tv_en.setText(word.getWord());
        holder.tv_zh.setText(word.getChineseMeaning());

        if (word.isChineseInvisible()) {
            holder.tv_zh.setVisibility(View.GONE);
            holder.switch_zh_enable.setChecked(true);
        } else {
            holder.tv_zh.setVisibility(View.VISIBLE);
            holder.switch_zh_enable.setChecked(false);
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull MyViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.tv_id.setText(String.valueOf(holder.getAdapterPosition() + 1));
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_id, tv_en, tv_zh;
        Switch switch_zh_enable;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_id = itemView.findViewById(R.id.tv_id);
            tv_en = itemView.findViewById(R.id.tv_en);
            tv_zh = itemView.findViewById(R.id.tv_zh);
            switch_zh_enable = itemView.findViewById(R.id.switch_zh_enable);
        }
    }
}
