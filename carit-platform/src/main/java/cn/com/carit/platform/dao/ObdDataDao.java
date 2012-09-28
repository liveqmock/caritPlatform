package cn.com.carit.platform.dao;

import java.util.List;

import cn.com.carit.Dao;

public interface ObdDataDao<ObdData> extends Dao<ObdData> {

	int batchAdd(final List<ObdData> dataList);
	
	/**
	 * 按照设备Id查询最新记录
	 * @param deviceId
	 * @return
	 */
	ObdData queryLastByDeviceId(String deviceId);
	
	/**
	 * 删除同一设备同一上传时间的重复数据（保留最后上传的）
	 */
	void deleteDuplicateData();
}
