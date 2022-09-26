package com.threshold.pojo;

public class RoomType {
	private String name;
	private int numOfPer;
	private String description;
	private int order;
	public RoomType(String name, int numOfPer, String description, int order) {
		super();
		this.name = name;
		this.numOfPer = numOfPer;
		this.description = description;
		this.order = order;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getNumOfPer() {
		return numOfPer;
	}
	public void setNumOfPer(int numOfPer) {
		this.numOfPer = numOfPer;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	@Override
	public String toString() {
		return "RoomType [name=" + name + ", numOfPer=" + numOfPer + ", description=" + description + ", order=" + order
				+ "]";
	}

}
