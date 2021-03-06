package cn.com.carit.platform.request.account;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotEmpty;

import cn.com.carit.common.Constants;

import com.rop.AbstractRopRequest;

/**
 * <pre>
 * 功能说明：账号相关请求模型基类
 * </pre>
 * @author <a href="mailto:xiegengcai@gmail.com">Gengcai Xie</a>
 * 2012-9-22
 */
public class AccountRequest extends AbstractRopRequest {
	
	@NotEmpty
    @Pattern(regexp = Constants.REGEXP_EMAIL)
	protected String email;

	public AccountRequest() {
	}

	public AccountRequest(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
    
}
