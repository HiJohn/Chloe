package joe.chloe.glimgfilter;

import joe.chloe.exoplayerfilter.FilterType;

public class FilterInfo {
	private boolean bSelected = false;
	private FilterType filterType;
	private boolean bFavourite = false;
	
	public void setFilterType(FilterType id){
		this.filterType = id;
	}

	public FilterType getFilterType(){
		return this.filterType;
	}

	public boolean isSelected(){
		return bSelected;
	}

	public void setSelected(boolean bSelected){
		this.bSelected = bSelected;
	}


}