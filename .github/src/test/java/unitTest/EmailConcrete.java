package unitTest;

import java.util.Map;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;

public class EmailConcrete extends Email {

	@Override
	public Email setMsg(String msg) throws EmailException {
		return null;
	}

	public Map<String, String> getHeader() {
		return this.headers;
	}

	public String getContenType() {
		return this.contentType;
	}

}