package ndrlabs.v7demo;

import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.ImageViewBitmapInfo;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import ndrlabs.v7demo.model.GImageSearch;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private List<GImageSearch> mList;
    private int mLayoutResId = R.layout.list_item_rv_grid;

    public CustomAdapter() {
        mList = new ArrayList<>();
    }

    public void addAll(@NonNull List<GImageSearch> list) {
        mList.addAll(list);
    }

    public void changeLayout(@LayoutRes int layoutResId) {
        mLayoutResId = layoutResId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(mLayoutResId, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

        GImageSearch item = mList.get(position);

        viewHolder.textView.setBackgroundColor(Color.TRANSPARENT);
        viewHolder.textView.setTextColor(Color.BLACK);

        Ion.with(viewHolder.imageView)
                .load(item.getUrl())
                .withBitmapInfo()
                .setCallback(new FutureCallback<ImageViewBitmapInfo>() {
                    @Override
                    public void onCompleted(Exception e, ImageViewBitmapInfo result) {
                        if (e != null
                                || result.getBitmapInfo() == null
                                || result.getBitmapInfo().bitmap == null) {
                            return;
                        }

                        Palette.generateAsync(result.getBitmapInfo().bitmap, new Palette.PaletteAsyncListener() {
                                    @Override
                                    public void onGenerated(Palette palette) {
                                        Palette.Swatch swatch = palette.getLightVibrantSwatch();
                                        if (swatch != null) {
                                            viewHolder.textView.setBackgroundColor(swatch.getRgb());
                                            viewHolder.textView.setTextColor(swatch.getTitleTextColor());
                                        }
                                    }
                                }

                        );
                    }
                });

        viewHolder.textView.setText(item.getTitle());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.image)
        ImageView imageView;

        @InjectView(android.R.id.text1)
        TextView textView;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.inject(this, v);
        }

        @OnClick(R.id.card_root)
        void onCardClick() {

        }
    }
}
