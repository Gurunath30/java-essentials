package com.threshold.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.threshold.pojo.*;

/**
 * Hotel an helper class for implementing RoomTypeDAO methods
 */
public class Hotel {
	public static RoomTypeDAO getRoomTypeDAO() {
		return new RoomTypeDAOImp();
	}

	private static class RoomTypeDAOImp implements RoomTypeDAO {
		private static final String PATH = "F:/HMSFiles/";
		private static final String FILE_NAME = "RoomType.csv";

		/**
		 * Writes data to a csv file
		 * 
		 * @param roomType
		 * @return boolean
		 */
		private OperationStatus writeCsv(RoomType roomType) {
			if (!new File(PATH).exists()) {
				return new OperationStatus(false, "Given file path doesn't exists");
			}
			FileWriter fileWriter = null;
			try {
				fileWriter = new FileWriter(PATH + FILE_NAME, true);
				if (new File(PATH + FILE_NAME).length() == 0) {
					fileWriter.append("name, numOfPer, description, order\n");
				}
				fileWriter.append(roomType.getName());
				fileWriter.append(",");
				fileWriter.append(String.valueOf(roomType.getNumOfPer()));
				fileWriter.append(",");
				fileWriter.append(roomType.getDescription());
				fileWriter.append(",");
				fileWriter.append(String.valueOf(roomType.getOrder()));
				fileWriter.append("\n");
			} catch (IOException exception) {
				exception.printStackTrace();
			} finally {
				try {
					fileWriter.flush();
					fileWriter.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return new OperationStatus(true, "A record is added to file");
		}

		private List<RoomType> readCsv() {
			if (new File(PATH + FILE_NAME).length() == 0) {
				return null;
			}
			BufferedReader reader = null;
			List<RoomType> roomTypes = null;
			try {
				roomTypes = new ArrayList<RoomType>();
				String line = "";
				reader = new BufferedReader(new FileReader(PATH + FILE_NAME));
				reader.readLine();
				while ((line = reader.readLine()) != null) {
					String[] data = line.split(",");
					if (data.length > 0) {
						roomTypes.add(
								new RoomType(data[0], Integer.parseInt(data[1]), data[2], Integer.parseInt(data[3])));
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				try {
					reader.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return roomTypes;
		}

		private boolean validateName(String name) {
			List<RoomType> roomTypes = readCsv();
			if (roomTypes == null) {
				return false;
			}
			for (RoomType roomType : roomTypes) {
				if (roomType.getName().equalsIgnoreCase(name)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public OperationStatus add(RoomType roomType) {
			if (roomType == null) {
				return new OperationStatus(false, "RoomType instance is assigned with null");
			} else if (roomType.getNumOfPer() <= 0 || roomType.getOrder() < 0) {
				return new OperationStatus(false, "Check RoomType instance is having invalid kind of data");
			} else if (validateName(roomType.getName())) {
				return new OperationStatus(false, "Given name of the room type is already exists");
			}
			return writeCsv(roomType);
		}

		@Override
		public RoomType get(String name) {
			List<RoomType> roomTypes = readCsv();
			if (roomTypes == null || name == null || name == "") {
				return null;
			}
			for (RoomType roomType : roomTypes) {
				if (roomType.getName().equalsIgnoreCase(name)) {
					return roomType;
				}
			}
			return null;
		}

		@Override
		public OperationStatus update(RoomType roomType) {
			return null;
		}

		@Override
		public OperationStatus delete(String name) {
			if (name == null || name == "") {
				return new OperationStatus(false, "Given name is invalid");
			}
			List<RoomType> roomTypes = readCsv();
			if (roomTypes == null) {
				return new OperationStatus(false, "File doesn't contains any data");
			}
			for (RoomType type : roomTypes) {
				if (type.getName().equalsIgnoreCase(name)) {
					roomTypes.remove(type);
					break;
				}
			}
			return null;

		}

		@Override
		public List<RoomType> list(String searchKey, String orderByKey, String orderByMode) {
			return null;
		}

	}

}
