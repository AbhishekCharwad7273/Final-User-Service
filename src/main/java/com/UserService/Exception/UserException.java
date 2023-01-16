package com.UserService.Exception;

import lombok.Getter;

@Getter
public class UserException extends RuntimeException{
	
	
	  private long id = 1L;
	    public UserException(String message) {
	        super(message);
	    }
	    public UserException(Long id,String message) {
	        super(message);
	        this.id = id;
	    }

}
