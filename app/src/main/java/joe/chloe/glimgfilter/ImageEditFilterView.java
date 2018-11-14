package joe.chloe.glimgfilter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import joe.chloe.R;
import joe.chloe.exoplayerfilter.FilterType;

public class ImageEditFilterView extends ImageEditFragment {

    private FilterLayoutUtils mFilterLayoutUtils;

    public ImageEditFilterView() {

    }

    public static ImageEditFilterView getInstance() {
        return new ImageEditFilterView();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_edit_filter, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mFilterLayoutUtils = new FilterLayoutUtils(getActivity(), mMagicDisplay);
        mFilterLayoutUtils.init(getView());
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden)
            mFilterLayoutUtils.init(getView());
    }

    @Override
    protected boolean isChanged() {
        return mFilterLayoutUtils.getFilterType() != FilterType.DEFAULT;
    }
}
