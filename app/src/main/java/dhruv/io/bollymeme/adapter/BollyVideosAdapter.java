package dhruv.io.bollymeme.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import dhruv.io.bollymeme.R;
import dhruv.io.bollymeme.model.BollyVideos;

/**
 * Created by Prithvi on 22-Apr-17.
 */

public class BollyVideosAdapter extends RecyclerView.Adapter<BollyVideosAdapter.ViewHolder> {

    private Context context;
    private BollyVideosOnClickInterface clickHandler;
    private ArrayList<BollyVideos> bollyVideosArrayList;

    private LayoutInflater layoutInflater;

    public BollyVideosAdapter(Context context, ArrayList<BollyVideos> bollyVideosArrayList, BollyVideosOnClickInterface vh){

        this.context = context;
        this.bollyVideosArrayList = bollyVideosArrayList;
        this.clickHandler = vh;

        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.holder_bolly_videos, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        BollyVideos currentVideo = bollyVideosArrayList.get(position);

        Glide.with(context)
                .load(currentVideo.getThumbnailURL())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .into(holder.imageThumbnail);

        holder.textVideoName.setText(currentVideo.getName());
    }

    @Override
    public int getItemCount() {
        return bollyVideosArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.ivThumbnail)
        ImageView imageThumbnail;
        @BindView(R.id.tvVideoName)
        TextView textVideoName;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickHandler.onClick(bollyVideosArrayList.get(getAdapterPosition()), this);
        }
    }

    public interface BollyVideosOnClickInterface{
        void onClick(BollyVideos bollyVideos, RecyclerView.ViewHolder vh);
    }
}
