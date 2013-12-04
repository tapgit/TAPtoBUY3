
package com.gui.taptobuy.Entities;

public class Bid 
{
	private int user_id, product_id;
	private double amount;
	String BidderUserName;
	///prueba
	
	public Bid(int placerId, double bidPrice, int productId, String BidderUsername){
		super();
		this.user_id = placerId;
		this.product_id = productId;
		this.amount = bidPrice;
		this.BidderUserName = BidderUsername;
	}


	public int getUser_id() {
		return user_id;
	}


	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}


	public int getProduct_id() {
		return product_id;
	}


	public void setProduct_id(int product_id) {
		this.product_id = product_id;
	}


	public double getAmount() {
		return amount;
	}


	public void setAmount(double amount) {
		this.amount = amount;
	}


	public String getBidderUserName() {
		return BidderUserName;
	}


	public void setBidderUserName(String bidderUserName) {
		BidderUserName = bidderUserName;
	}


}

