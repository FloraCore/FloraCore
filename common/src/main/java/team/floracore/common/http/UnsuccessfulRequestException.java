package team.floracore.common.http;

import lombok.Getter;
import okhttp3.Response;

@Getter
public class UnsuccessfulRequestException extends Exception {

	private final Response response;

	public UnsuccessfulRequestException(Response response) {
		super("Request was unsuccessful: " + response.code() + " - " + response.message());
		this.response = response;
	}

}
