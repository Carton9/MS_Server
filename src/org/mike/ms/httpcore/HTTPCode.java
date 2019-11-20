/**
 * 
 */
package org.mike.ms.httpcore;

/**
 * @author c
 *
 */
public enum HTTPCode{
	CONTINUE(100,"Continue"),
	SWITCHING_PROTOCOLS(101,"Switching_Protocols"),
	OK(200,"OK"),
	CREATED(201,"Created"),
	ACCEPTED(202,"Accepted"),
	NON_AUTHORITATIVE_INFORMATION(203,"Non-Authoritative_Information"),
	NO_CONTENT(204,"No_Content"),
	RESET_CONTENT(205,"Reset_Content"),
	PARTIAL_CONTENT(206,"Partial_Content"),
	MULTIPLE_CHOICES(300,"Multiple_Choices"),
	MOVED_PERMANENTLY(301,"Moved_Permanently"),
	FOUND(302,"Found"),
	SEE_OTHER(303,"See_Other"),
	NOT_MODIFIED(304,"Not_Modified"),
	USE_PROXY(305,"Use_Proxy"),
	UNUSED(306,"Unused"),
	TEMPORARY_REDIRECT(307,"Temporary_Redirect"),
	BAD_REQUEST(400,"Bad_Request"),
	UNAUTHORIZED(401,"Unauthorized"),
	PAYMENT_REQUIRED(402,"Payment_Required"),
	FORBIDDEN(403,"Forbidden"),
	NOT_FOUND(404,"Not_Found"),
	METHOD_NOT_ALLOWED(405,"Method_Not_Allowed"),
	NOT_ACCEPTABLE(406,"Not_Acceptable"),
	PROXY_AUTHENTICATION_REQUIRED(407,"Proxy_Authentication_Required"),
	REQUEST_TIME_OUT(408,"Request_Time-out"),
	CONFLICT(409,"Conflict"),
	GONE(410,"Gone"),
	LENGTH_REQUIRED(411,"Length_Required"),
	PRECONDITION_FAILED(412,"Precondition_Failed"),
	REQUEST_ENTITY_TOO_LARGE(413,"Request_Entity_Too_Large"),
	REQUEST_URI_TOO_LARGE(414,"Request-URI_Too_Large"),
	UNSUPPORTED_MEDIA_TYPE(415,"Unsupported_Media_Type"),
	REQUESTED_RANGE_NOT_SATISFIABLE(416,"Requested_range_not_satisfiable"),
	EXPECTATION_FAILED(417,"Expectation_Failed"),
	INTERNAL_SERVER_ERROR(500,"Internal_Server_Error"),
	NOT_IMPLEMENTED(501,"Not_Implemented"),
	BAD_GATEWAY(502,"Bad_Gateway"),
	SERVICE_UNAVAILABLE(503,"Service_Unavailable"),
	GATEWAY_TIME_OUT(504,"Gateway_Time-out"),
	HTTP_VERSION_NOT_SUPPORTED(505,"HTTP_Version_not_supported");
	int errorCode;
	String noteText;
	HTTPCode(int errorCode,String noteText) {
		this.errorCode=errorCode;
		this.noteText=noteText;
	}
	public String getNote() {
		return noteText;
	}
	public int getErrorCode() {
		return errorCode;
	}
}