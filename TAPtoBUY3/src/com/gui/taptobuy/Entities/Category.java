package com.gui.taptobuy.Entities;

public class Category {
	private String name;
	private int catId;
	private boolean hasSubCategories;
	public Category(String name, int catId, boolean hasSubCategories) {
		super();
		this.name = name;
		this.catId = catId;
		this.hasSubCategories = hasSubCategories;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getCatId() {
		return catId;
	}
	public void setCatId(int catId) {
		this.catId = catId;
	}
	public boolean hasSubCategories() {
		return hasSubCategories;
	}
	public void setHasSubCategories(boolean hasSubCategories) {
		this.hasSubCategories = hasSubCategories;
	}
}
