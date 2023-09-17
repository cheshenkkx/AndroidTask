package com.example.androidtask.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.androidtask.R;
import com.example.androidtask.adapters.ShareListAdapter;
import com.example.androidtask.network.RetrofitClient;
import com.example.androidtask.network.service.PhotoService;
import com.example.androidtask.response.BaseResponse;
import com.example.androidtask.response.Data;
import com.example.androidtask.response.Records;
import com.example.androidtask.response.UserInfo;
import com.example.androidtask.response.sharelist_item;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShareListFragment extends Fragment {
    private View sharelistView;
    private static List<Records> recordlist = new ArrayList<>();
    private RecyclerView rv_sharelist;
    private ShareListAdapter adapter;
    private PhotoService photoService = RetrofitClient.getInstance().getService(PhotoService.class);
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(sharelistView == null){
            sharelistView = inflater.inflate(R.layout.fragment_share_list, container,false);
            initData();
        }
        return sharelistView;
    }

    private void initData() {
        photoService.getShare(null, 50, "1").enqueue(new Callback<BaseResponse<Data<Records>>>() {
            @Override
            public void onResponse(Call<BaseResponse<Data<Records>>> call, Response<BaseResponse<Data<Records>>> response) {
                recordlist = response.body().getData().getRecords();
                //获取头像
                ArrayList<sharelist_item> data = new ArrayList<>();
                for(int i=0; i<response.body().getData().getSize();i++){
                    sharelist_item item = new sharelist_item();
                    item.record = recordlist.get(i);
                    photoService.getUserByName(item.record.getUsername()).enqueue(new Callback<BaseResponse<UserInfo>>() {
                        @Override
                        public void onResponse(Call<BaseResponse<UserInfo>> call, Response<BaseResponse<UserInfo>> response) {
                            item.profileUrl = response.body().getData().getAvatar();
                        }

                        @Override
                        public void onFailure(Call<BaseResponse<UserInfo>> call, Throwable t) {
                            Toast.makeText(getContext(),t.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                    data.add(item);
                }

                rv_sharelist = sharelistView.findViewById(R.id.shareList);
                adapter = new ShareListAdapter(getActivity(),data);
//                rv_sharelist.setLayoutManager(new LinearLayoutManager(LinearLayoutManager.HORIZONTAL,false));
                rv_sharelist.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
                rv_sharelist.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<BaseResponse<Data<Records>>> call, Throwable t) {
                Toast.makeText(getContext(),t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}