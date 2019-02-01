package com.demo.playground.nestedscroll;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.playground.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by liyongan on 19/1/31.
 */

public class TabFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tab, container, false);
        RecyclerView mRecyclerView = root.findViewById(R.id.recycle_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        String[] data = new String[]{
                "0",
                "1",
                "2",
                "3",
                "4",
                "5",
                "6",
                "7",
                "8",
                "9",
                "0",
                "1",
                "2",
                "3",
                "4",
                "5",
                "6",
                "7",
                "8",
                "9",
                "0",
                "1",
                "2",
                "3",
                "4",
                "5",
                "6",
                "7",
                "8",
                "9",
        };
        mRecyclerView.setAdapter(new TestRecycleViewAdapter(getContext(), new ArrayList<>(Arrays.asList(data))));
        return root;
    }

    public class TestRecycleViewAdapter extends RecyclerView.Adapter<TestRecycleViewAdapter.ViewHolderA> {
        private Context mContext;
        private List<String> mList;

        public TestRecycleViewAdapter(Context context, List<String> list) {
            mContext = context;
            mList = list;
        }

        @Override
        public ViewHolderA onCreateViewHolder(ViewGroup parent, int viewType) {
            //此处动态加载ViewHolder的布局文件并返回holder
            View view = LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_1, parent, false);
            ViewHolderA holderA = new ViewHolderA(view);
            return holderA;
        }

        @Override
        public void onBindViewHolder(ViewHolderA holder, int position) {
            //此处设置Item中view的数据
            holder.mTextView.setText(mList.get(position));
            holder.mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "hello", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            //生成的item的数量
            return mList.size();
        }

        //Item的ViewHolder以及item内部布局控件的id绑定
        class ViewHolderA extends RecyclerView.ViewHolder{

            TextView mTextView;
            public ViewHolderA(View itemView) {
                super(itemView);
                mTextView = (TextView) itemView;
            }
        }
    }
}
