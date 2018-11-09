package joe.chloe.exoplayerfilter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import joe.chloe.R;


public class VideoFilterAdapter extends RecyclerView.Adapter<VideoFilterAdapter.FilterHolder> {

    private FilterClickListener mFilterClickListener;

    private ArrayList<FilterType> filterNames = new ArrayList<>();


    public void setFilterNames(ArrayList<FilterType> filterNames) {
        this.filterNames = filterNames;
    }

    public VideoFilterAdapter() {
    }

    public void setFilterSelectListener(FilterClickListener filterClickListener) {
        this.mFilterClickListener = filterClickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull FilterHolder holder, int position) {
//        ImageLoader.load(filter,imgFilter.filterType);
        final int pos = holder.getAdapterPosition();
        final FilterType type = filterNames.get(pos);
        holder.filterName.setText(type.name());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mFilterClickListener != null) {
                    mFilterClickListener.onFilterClicked(pos, type);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return filterNames.size();
    }

    @NonNull
    @Override
    public FilterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sv_layout_item_filter,parent,false);
        return new FilterHolder(view);
    }


    public class FilterHolder extends RecyclerView.ViewHolder {
        ImageView filter;
        TextView filterName;

        public FilterHolder(View itemView) {
            super(itemView);
            filter = itemView.findViewById(R.id.filter_img);
            filterName = itemView.findViewById(R.id.filter_name);
        }
    }

    public interface FilterClickListener {
        void onFilterClicked(int position, FilterType filterType);
    }

}
