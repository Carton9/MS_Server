/**
 * 
 */
package org.mike.ms.datacontroller;

/**
 * @author c
 *
 */
public enum StatusCode {
	SECCESS(0),DATA_NO_SYNC(1),DATA_SAVE_FAILURE(2),ENTRY_NOT_FOUND(3),INTERFACE_NOT_FOUND(4),ACCESS_FAILURE(5);
	int code;
	StatusCode(int code){
		this.code=code;
	}
	public static StatusCode formateCode(int code) {
		switch(code) {
		case 0:
			return SECCESS;
		case 1:
			return DATA_NO_SYNC;
		case 2:
			return DATA_SAVE_FAILURE;
		case 3:
			return ENTRY_NOT_FOUND;
		case 4:
			return INTERFACE_NOT_FOUND;
		case 5:
			return ACCESS_FAILURE;
		}
		return null;
	}
	public int getCode() {
		return code;
	}
	public StatusCode addCode(StatusCode st) {
		return formateCode(Math.max(code, st.code));
	}
}
