package joe.chloe.glimgfilter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import joe.chloe.R;
import joe.chloe.exoplayerfilter.FilterType;


public class ImgFilterAdapter extends RecyclerView.Adapter<ImgFilterAdapter.FilterHolder> {

    private LayoutInflater mInflater;
    private int lastSelected = 0;
    private Context context;
    private List<FilterInfo> filterInfos;

    public ImgFilterAdapter(Context context) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemCount() {
        return filterInfos.size();
    }

//    @Override
//    public int getItemViewType(int position) {
//        return filterInfos.get(position).getFilterType();
//    }

    @Override
    public void onBindViewHolder(FilterHolder holder, int position) {

        int pos = holder.getAdapterPosition();

        if (filterInfos.get(pos).getFilterType() != FilterType.DEFAULT) {
//            holder.thumbImage.setImageResource(FilterTypeHelper.FilterType2Thumb(filterInfos.get(pos).getFilterType()));
//            holder.filterName.setText(FilterTypeHelper.FilterType2Name(filterInfos.get(pos).getFilterType()));
//            holder.filterName.setBackgroundColor(context.getResources().getColor(
//                    FilterTypeHelper.FilterType2Color(filterInfos.get(pos).getFilterType())));
//            if (filterInfos.get(pos).isSelected()) {
//                holder.thumbSelected.setVisibility(View.VISIBLE);
//                holder.thumbSelected_bg.setBackgroundColor(context.getResources().getColor(
//                        FilterTypeHelper.FilterType2Color(filterInfos.get(pos).getFilterType())));
//                holder.thumbSelected_bg.setAlpha(0.7f);
//            } else {
//                holder.thumbSelected.setVisibility(View.GONE);
//            }
            holder.filterRoot.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onFilterChangeListener != null && filterInfos.get(pos).getFilterType() != FilterType.DEFAULT
                            && pos != lastSelected
                            && !filterInfos.get(pos).isSelected()) {
                        filterInfos.get(lastSelected).setSelected(false);
                        filterInfos.get(pos).setSelected(true);
                        notifyItemChanged(lastSelected);
                        notifyItemChanged(pos);
                        lastSelected = pos;

                        onFilterChangeListener.onFilterChanged(filterInfos.get(pos).getFilterType(), pos);
                    }
                }
            });
        }
    }

    @Override
    public FilterHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
            View view = mInflater.inflate(R.layout.filter_item_layout,
                    arg0, false);
            FilterHolder viewHolder = new FilterHolder(view);
//            viewHolder.thumbImage = (ImageView) view
//                    .findViewById(R.id.filter_thumb_image);
//            viewHolder.filterName = (TextView) view
//                    .findViewById(R.id.filter_thumb_name);
//            viewHolder.filterRoot = (FrameLayout) view
//                    .findViewById(R.id.filter_root);
//            viewHolder.thumbSelected = (FrameLayout) view
//                    .findViewById(R.id.filter_thumb_selected);
//            viewHolder.filterFavourite = (FrameLayout) view.
//                    findViewById(R.id.filter_thumb_favorite_layout);
//            viewHolder.thumbSelected_bg = (View) view.
//                    findViewById(R.id.filter_thumb_selected_bg);
            return viewHolder;
    }

    public void setLastSelected(int arg) {
        lastSelected = arg;
    }

    public int getLastSelected() {
        return lastSelected;
    }

    public void setFilterInfos(List<FilterInfo> filterInfos) {
        this.filterInfos = filterInfos;
        notifyDataSetChanged();
    }

    class FilterHolder extends ViewHolder {
        ImageView thumbImage;
        TextView filterName;
        FrameLayout thumbSelected;
        FrameLayout filterRoot;
        FrameLayout filterFavourite;
        View thumbSelected_bg;

        public FilterHolder(View itemView) {
            super(itemView);
        }
    }

    public interface onFilterChangeListener {
        void onFilterChanged(FilterType filterType, int position);
    }

    private onFilterChangeListener onFilterChangeListener;

    public void setOnFilterChangeListener(onFilterChangeListener onFilterChangeListener) {
        this.onFilterChangeListener = onFilterChangeListener;
    }
}
