package joe.chloe.glimgfilter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import joe.chloe.R;
import joe.chloe.exoplayerfilter.FilterAdapter;
import joe.chloe.exoplayerfilter.FilterType;

public class FilterLayoutUtils {
    private Context mContext;
    private MagicDisplay mMagicDisplay;
    private ImgFilterAdapter mAdapter;

    private int position;
    private List<FilterInfo> filterInfos;
    private List<FilterInfo> favouriteFilterInfos;

    private FilterType mFilterType = FilterType.DEFAULT;

    public FilterLayoutUtils(Context context, MagicDisplay magicDisplay) {
        mContext = context;
        mMagicDisplay = magicDisplay;
    }

    public void init() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        RecyclerView mFilterListView = (RecyclerView) ((Activity) mContext).findViewById(R.id.filter_listView);
        mFilterListView.setLayoutManager(linearLayoutManager);

        mAdapter = new ImgFilterAdapter(mContext);
        mFilterListView.setAdapter(mAdapter);
        initFilterInfos();
        mAdapter.setFilterInfos(filterInfos);
        mAdapter.setOnFilterChangeListener(onFilterChangeListener);
    }

    public void init(View view) {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        RecyclerView mFilterListView = (RecyclerView) view.findViewById(R.id.filter_listView);
        mFilterListView.setLayoutManager(linearLayoutManager);

        mAdapter = new ImgFilterAdapter(mContext);
        mFilterListView.setAdapter(mAdapter);
        initFilterInfos();
        mAdapter.setFilterInfos(filterInfos);
        mAdapter.setOnFilterChangeListener(onFilterChangeListener);

        view.findViewById(R.id.btn_camera_closefilter).setVisibility(View.GONE);
    }

    private ImgFilterAdapter.onFilterChangeListener onFilterChangeListener = new ImgFilterAdapter.onFilterChangeListener() {

        @Override
        public void onFilterChanged(FilterType filterType, int position) {
            FilterType Type = filterInfos.get(position).getFilterType();
            FilterLayoutUtils.this.position = position;

            //设置滤镜
            mMagicDisplay.setFilter(filterType);
            mFilterType = filterType;


        }

    };

    private void initFilterInfos() {
        filterInfos = new ArrayList<FilterInfo>();
        //add original
        FilterInfo filterInfo = new FilterInfo();
        filterInfo.setFilterType(FilterType.DEFAULT);
        filterInfo.setSelected(true);
        filterInfos.add(filterInfo);

        //add Divider
        filterInfo = new FilterInfo();
        filterInfo.setFilterType(FilterType.DEFAULT);
        filterInfos.add(filterInfo);

        //addAll
//        for (int i = 1; i < FilterType.FILTER_COUNT; i++) {
//            filterInfo = new FilterInfo();
//            filterInfo.setFilterType(FilterType.DEFAULT + i);
//            filterInfos.add(filterInfo);
//        }
    }



    public FilterType getFilterType() {
        return mFilterType;
    }
}
