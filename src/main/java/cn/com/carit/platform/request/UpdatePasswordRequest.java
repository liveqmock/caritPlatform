package cn.com.carit.platform.request;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotEmpty;

import cn.com.carit.common.Constants;

import com.rop.AbstractRopRequest;

public class UpdatePasswordRequest extends AbstractRopRequest {

	@NotEmpty
	@Pattern(regexp = Constants.REGEXP_EMAIL)
	private String email;
	
	@NotEmpty
	@Pattern(regexp = Constants.REGEXP_PASSWORD)
	private String password;
	
	@NotEmpty
	@Pattern(regexp = Constants.REGEXP_PASSWORD)
	private String newPassword;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

}
