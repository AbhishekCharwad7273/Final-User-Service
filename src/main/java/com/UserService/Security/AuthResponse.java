package com.UserService.Security;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
	
	 private String token;
	    private Long id;
	    private String name;
	    private String password;
	    private String emailId;
	    private boolean authorUser;

}
