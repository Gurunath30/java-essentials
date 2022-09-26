package com.threshold.test;

import com.threshold.util.Hotel;
import com.threshold.util.RoomTypeDAO;

public class GetRoomTypeMain {
	public static void main(String[] args) {
		RoomTypeDAO roomDao = Hotel.getRoomTypeDAO();
		System.out.println(roomDao.get("double"));
	}
}
