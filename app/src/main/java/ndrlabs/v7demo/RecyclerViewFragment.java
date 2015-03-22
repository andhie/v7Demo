package ndrlabs.v7demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ndrlabs.v7demo.model.GImageSearch;

/**
 * Created by andhie on 3/22/15.
 */
public class RecyclerViewFragment extends Fragment {

    public static Fragment newInstance() {
        return new RecyclerViewFragment();
    }

    @InjectView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    private CustomAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rv, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        setHasOptionsMenu(true);

        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        mAdapter = new CustomAdapter();
        mAdapter.changeLayout(R.layout.list_item_rv_grid);
        mRecyclerView.setAdapter(mAdapter);

        getData();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_stag_grid:
                mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
                mAdapter.changeLayout(R.layout.list_item_rv_grid);
                mAdapter.notifyDataSetChanged();
                return true;

            case R.id.action_grid:
                mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                mAdapter.changeLayout(R.layout.list_item_rv_grid);
                mAdapter.notifyDataSetChanged();
                return true;

            case R.id.action_linearlayout:
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                mAdapter.changeLayout(R.layout.list_item_rv_linear);
                mAdapter.notifyDataSetChanged();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getData() {
        if (mAdapter.getItemCount() > 20) return;

        String url = String.format("https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=%s&start=%d&imgsz=medium", "cat", mAdapter.getItemCount());
        Ion.with(this)
                .load(url)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        try {
                            if (e != null) {
                                throw e;
                            }
                            // find the results and populate
                            JsonArray results = result.getAsJsonObject("responseData").getAsJsonArray("results");
                            List<GImageSearch> list = new Gson().fromJson(results, new TypeToken<List<GImageSearch>>() {
                            }.getType());

                            final int startSize = mAdapter.getItemCount();
                            mAdapter.addAll(list);
                            mAdapter.notifyItemRangeInserted(startSize, mAdapter.getItemCount());

                            // auto load more data
                            getData();

                        } catch (Exception ex) {
                            // toast any error we encounter (google image search has an API throttling limit that sometimes gets hit)
                            ex.printStackTrace();
                        }

                    }
                });
    }

}
