package com.gui.taptobuy.Entities;

public class ProductReport {
private String product, soldAmount, prodRevenue;

public ProductReport(String product, String soldAmount, String prodRevenue) {
	super();
	this.product = product;
	this.soldAmount = soldAmount;
	this.prodRevenue = prodRevenue;
}

public String getProduct() {
	return product;
}

public String getSoldAmount() {
	return soldAmount;
}

public String getProdRevenue() {
	return prodRevenue;
}
}
