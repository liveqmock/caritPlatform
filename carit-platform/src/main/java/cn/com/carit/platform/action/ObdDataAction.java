package cn.com.carit.platform.action;

import java.util.List;

import cn.com.carit.Action;
import cn.com.carit.common.utils.JsonPage;
import cn.com.carit.platform.request.SearchObdDataRequest;

public interface ObdDataAction<ObdData> extends Action<ObdData> {
	
	int batchAdd(final List<ObdData> dataList);
	
	/**
	 * 按照设备Id查询最新记录
	 * @param deviceId
	 * @param accountId
	 * @return
	 */
	ObdData queryNewestData(String deviceId, int accountId);
	
	JsonPage<ObdData> query(SearchObdDataRequest request);
	
	List<ObdData> queryCurrentDataByAccount(int accountId);
}
