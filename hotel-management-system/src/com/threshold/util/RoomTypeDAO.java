package com.threshold.util;

import java.util.List;

import com.threshold.pojo.OperationStatus;
import com.threshold.pojo.RoomType;

/**
 * RoomTypeDAO (RoomType Data Access Object) interface which contains the
 * methods, so that using or with the help of these methods Receptionist/Admin
 * can access, add, search, display or can remove the Room category from the
 * hotel based on the input given to the methods.
 */
public interface RoomTypeDAO {
	/**
	 * Will create a file for the first entry and then adds record. If file already
	 * exists new record has to append. This will add RoomType data to file and
	 * return true if it is successfully added to file with a message. Otherwise it
	 * will return false with a reason as a message. Will return false when RoomType
	 * is null. Will return false when RoomType instance is having invalid kind of
	 * data. Will return false for file IO Exception with clear message.
	 * 
	 * @param roomType
	 * @return OperationStatus
	 */
	OperationStatus add(RoomType roomType);

	/**
	 * This will return a RoomType by processing a file based on given name. Will
	 * return null when name if it is invalid or doesn’t exists in the file.
	 * 
	 * @param name name of the RoomType to be returned
	 * 
	 * @return RoomType
	 */
	RoomType get(String name);

	/**
	 * This will update an existing RoomType features with a given features by
	 * processing a file based on name attribute provided in roomType instance. This
	 * will update RoomType in a file and return true if it is successfully updated
	 * to file. Otherwise it will return false with a reason as a message. Will
	 * return false when roomType is null. Will return false when name if it is
	 * invalid or doesn’t exists in file.
	 * 
	 * @param roomType
	 * 
	 * @return OperationStatus
	 * 
	 */
	OperationStatus update(RoomType roomType);

	/**
	 * Based on given name of roomType, file is processed to find a RoomType and if
	 * it is found then it is deleted from the file. Otherwise it will return false
	 * with a reason as a message. Will return false when name if it is invalid or
	 * doesn’t exists.
	 * 
	 * @param name name of the RoomType to be deleted
	 * 
	 * @return OperationStatus
	 * 
	 */
	OperationStatus delete(String name);

	/**
	 * This method will retrieve the RoomType corresponding to the given searchKey
	 * from a file and that will be added to the List<RoomType> and then list is
	 * sorted accordingly with a given order. Will return List<RoomType> with
	 * default order if the given order is invalid or null. Will return null when
	 * searchKey if it is null or doesn’t exists in file.
	 * 
	 * @param searchKey
	 * 
	 * @param orderByKey
	 * 
	 * @param orderByMode
	 * 
	 * @return List<RoomType>
	 */
	List<RoomType> list(String searchKey, String orderByKey, String orderByMode);
}
