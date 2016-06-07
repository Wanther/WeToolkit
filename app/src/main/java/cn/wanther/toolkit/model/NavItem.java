package cn.wanther.toolkit.model;



public class NavItem {
	
	private int nameId;
	private int iconId;
	
	public NavItem(){}
	
	public NavItem(int nameId, int iconId){
		this.nameId = nameId;
		this.iconId = iconId;
	}
	
	public int getNameId() {
		return nameId;
	}

	public void setNameId(int nameId) {
		this.nameId = nameId;
	}

	public int getIconId() {
		return iconId;
	}
	public void setIconId(int iconId) {
		this.iconId = iconId;
	}
	
}
