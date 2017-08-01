package com.anz.currency.convertor.exceptions;

public class ServiceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7979187161433312590L;
	
	private String message;
	public ServiceException() {}
	
	public ServiceException( String message ) {
		this.message = message;
	}
	
	public ServiceException( String message, Exception e ) {
		this.message = message + e.getMessage();
	}
	
	@Override
	public String getMessage() {
		return message;
	}

}
