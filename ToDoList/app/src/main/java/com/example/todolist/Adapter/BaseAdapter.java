/*
 * Copyright 2017 Yan Zhenjie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haibin.TimeManager.Adapter;
import android.content.Context;
import android.view.LayoutInflater;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

import com.haibin.TimeManager.Todo.Todo;

public abstract class BaseAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private final LayoutInflater mInflater;
    public BaseAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
    }
    public LayoutInflater getInflater() {
        return mInflater;
    }
    //继承baseadapter的主要原因是调用这个函数，可以检测到adapter内内容的变化
    public abstract void notifyDataSetChanged(List<Todo> dataList);
}
