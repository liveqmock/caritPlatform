package cn.com.carit.platform.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.NotEmpty;

import com.rop.AbstractRopRequest;
/**
 * <p>
 * <b>功能说明：</b>
 * </p>
 * @author <a href="mailto:xiegengcai@gmail.com">Gengcai Xie</a>
 * @date 2012-9-25
 */
public class LocationUploadRequest extends AbstractRopRequest {
	
	@NotEmpty
	private String lists;
	
	@NotEmpty
    private String deviceId;
	
	@Min(value=1)
	@Max(value=Integer.MAX_VALUE)
	private int accountId;
	
	public String getLists() {
		return lists;
	}

	public void setLists(String lists) {
		this.lists = lists;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

}
