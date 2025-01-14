package com.power2sme.dms.config;

/**
 * @author Himanshu Raghuvanshi {himanshushekhar002@gmail.com}
 * @link{}
 * @Date June 16, 2016
 * This class is written to support json object for username-password authentication 
 * mechanism in addition to default form login. 
 * It will work only by adding this filter before UsernamePasswordAuthenticationFilter.
 * All configuration like set username parameter name , password parameter name, 
 * AuthenticationSuccessHandler, AuthenticationFailureHandler etc. needs to be added
 * to this filter again if changed from default.
 * 
 */

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.google.gson.Gson;

public final class JsonUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	
	private static Logger logger=LoggerFactory.getLogger(JsonUsernamePasswordAuthenticationFilter.class);
	
	
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response){
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        if (request.getHeader("Content-Type")!=null && request.getHeader("Content-Type").toLowerCase().contains("application/json")) {
           	logger.debug("Non json content-type :: "+request.getHeader("Content-Type"));           	
           	LoginRequest loginRequest = this.getLoginRequest(request);
           	UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
           	setDetails(request, authRequest);
           	return this.getAuthenticationManager().authenticate(authRequest);
        }
        else{
        	logger.debug("Non json content-type 2:: "+request.getHeader("Content-Type"));
        	return super.attemptAuthentication(request, response);
        }
    }

    /**
     * This method returns the {@link : LoginRequest} object representing 
     * Json input parameter. 
     * This will work only if useraname and password parameter are 'username' & 'password' respectively.
     * @param request
     * @return
     */
    private LoginRequest getLoginRequest(HttpServletRequest request) {
        BufferedReader reader = null;
        LoginRequest loginRequest = null;
        try {
            reader = request.getReader();
            Gson gson = new Gson();
            loginRequest = gson.fromJson(reader, LoginRequest.class);
            logger.debug("LoginRequest :: "+loginRequest);
            if(loginRequest.getUsername()==null || loginRequest.getUsername().trim().isEmpty()){
            	throw new AuthenticationServiceException("Username Cannot be blank");
            }
            if(loginRequest.getPassword()==null || loginRequest.getPassword().trim().isEmpty()){
            	throw new AuthenticationServiceException("Password Cannot be blank");
            }
        } catch (IOException ex) {
            logger.error(null, ex);
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                logger.error(null, ex);
            }
        }

        if (loginRequest == null) {
            loginRequest = new LoginRequest();
        }
        return loginRequest;
    }
    
    private static final class LoginRequest {
	  	
  	   private String username;
 	   private String password;

 	   @Override
 	public String toString() {
 		// TODO Auto-generated method stub
 		return "[Username : "+String.valueOf(username)+"][password :"+String.valueOf(password)+"]";
 	}
 	   
 	   public LoginRequest(String username, String password) {
 	      this.username = username;
 	      this.password = password;
 	   }
 	   
 	   
 	   public LoginRequest(){
 	      this("", "");
 	   }

 	   public String getPassword() {
 	      return password;
 	   }


 	   public String getUsername() {
 	      return username;
 	   }

 	   @Override
 	   public boolean equals(Object o) {
 	      if(this == o) return true;
 	      if(o == null || getClass() != o.getClass()) return false;

 	      LoginRequest that = (LoginRequest)o;

 	      if(password != null ? !password.equals(that.password) : that.password != null) return false;
 	      if(username != null ? !username.equals(that.username) : that.username != null) return false;

 	      return true;
 	   }

 	   @Override
 	   public int hashCode() {
 	      int result = username != null ? username.hashCode() : 0;
 	      result = 31 * result + (password != null ? password.hashCode() : 0);
 	      return result;
 	   }
 	}
}