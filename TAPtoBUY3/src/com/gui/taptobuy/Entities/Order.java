package com.gui.taptobuy.Entities;

public class Order {
	private int id;
	private String date;
	private String sellerUsername;
	private String buyerUsername;
	private String shippingAddressStr;
	private String paymentMethod; //CreditCard for purchased order & Paypal email for sold order
	private double paidPrice;
	private double shippingPrice;
	private MyHistoryProduct[] products;
	public Order(int id, String date, String sellerUsername,
			String buyerUsername, String shippingAddressStr,
			String paymentMethod, double paidPrice, double shippingPrice,
			MyHistoryProduct[] products) {
		super();
		this.id = id;
		this.date = date;
		this.sellerUsername = sellerUsername;
		this.buyerUsername = buyerUsername;
		this.shippingAddressStr = shippingAddressStr;
		this.paymentMethod = paymentMethod;
		this.paidPrice = paidPrice;
		this.shippingPrice = shippingPrice;
		this.products = products;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getSellerUsername() {
		return sellerUsername;
	}
	public void setSellerUsername(String sellerUsername) {
		this.sellerUsername = sellerUsername;
	}
	public String getBuyerUsername() {
		return buyerUsername;
	}
	public void setBuyerUsername(String buyerUsername) {
		this.buyerUsername = buyerUsername;
	}
	public String getShippingAddressStr() {
		return shippingAddressStr;
	}
	public void setShippingAddressStr(String shippingAddressStr) {
		this.shippingAddressStr = shippingAddressStr;
	}
	public String getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	public double getPaidPrice() {
		return paidPrice;
	}
	public void setPaidPrice(double paidPrice) {
		this.paidPrice = paidPrice;
	}
	public double getShippingPrice() {
		return shippingPrice;
	}
	public void setShippingPrice(double shippingPrice) {
		this.shippingPrice = shippingPrice;
	}
	public MyHistoryProduct[] getProducts() {
		return products;
	}
	public void setProducts(MyHistoryProduct[] products) {
		this.products = products;
	}
	
}
