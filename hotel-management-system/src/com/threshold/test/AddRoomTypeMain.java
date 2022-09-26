package com.threshold.test;

import com.threshold.pojo.OperationStatus;
import com.threshold.pojo.RoomType;
import com.threshold.util.Hotel;
import com.threshold.util.RoomTypeDAO;

public class AddRoomTypeMain {
	public static void main(String[] args) {
		RoomTypeDAO roomDao = Hotel.getRoomTypeDAO();
		OperationStatus status = roomDao.add(new RoomType("Double", 3, "Hall and bedroom", 6));
		System.out.println(status);
	}
}
