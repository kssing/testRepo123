package com.power2sme.dms.interceptors;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.util.matcher.ELRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.power2sme.dms.config.properties.IPConfiguration;


//import com.power2sme.finansme.utils.PropertyUtil;

public class RequestAuthorizationFilter extends OncePerRequestFilter {
	
	@Autowired
	IPConfiguration ipConfiguration;

	private static final String NONAJAX_ALLOWED_LIST_IP = "NONAJAX_ALLOWED_LIST_IP";
	private static final String BLCAK_LIST_IP = "BLCAK_LIST_IP";
    private static final RequestMatcher requestMatcher = new ELRequestMatcher(
            "hasHeader('X-Requested-With','XMLHttpRequest')");

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String ip=getIp(request);
		
		logger.debug("IP BlacListed :: "+isIpBlackListed(ip) +" AJAX CALL :: "+isAjaxRequest(request)+" IP in NONAJAX LIST : "+ isIpInNonAjaxList(ip)+" Mobile App Request :: "+isRequestMobileValid(request));
		
		if(isIpBlackListed(ip)){
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			response.getWriter().println("BlackListed IP");
		}
		
		/*
		 * This will be activated once NON AJAX LIST ip identified securely because
		 * all client will have non ajax ip for angular app deployement
		 */
//		else if(!isAjaxRequest(request) && !isIpInNonAjaxList(ip) && !isRequestMobileValid(request)){
//			System.out.println("him filter");
//			response.sendError(HttpServletResponse.SC_FORBIDDEN);
//			response.getWriter().println("IP is not in Non-Ajax IP List or Not a valid Mobile Request");
//		}
		else{
           filterChain.doFilter(request, response);
		}
	}
	
	private boolean isIpBlackListed(String ip){
		logger.info("entered");
//		String ips=PropertyUtil.properties.getProperty(BLCAK_LIST_IP);
		String ips=ipConfiguration.getBlacklist();
		Set<String> blackListIps = new HashSet<String>(Arrays.asList(ips.split(",")));
		logger.info("exit");
		return blackListIps.contains(ip);
		
	}
	

    /**
     * Checks if it is a rest request
     * @param request
     * @return
     */
    protected boolean isAjaxRequest(HttpServletRequest request) {
      logger.debug("going to check for ajax request");
        return requestMatcher.matches(request);
    }

	private boolean isIpInNonAjaxList(String ip){
		logger.info("entered");
//		String ips=PropertyUtil.properties.getProperty(NONAJAX_ALLOWED_LIST_IP);
		String ips=ipConfiguration.getNonajaxallowed();
		Set<String> nonAjaxListIps = new HashSet<String>(Arrays.asList(ips.split(",")));
		logger.info("exit");
		return nonAjaxListIps.contains(ip);
	}

	
	private boolean isRequestMobileValid(HttpServletRequest req){
		logger.info("entered");		
		
		String xPower2smeToken=req.getHeader("X-Power2sme-Token");
		String platform=req.getHeader("Platform");
		String deviceId=req.getHeader("Device-Id");
		String deviceName=req.getHeader("Device-Name");
		String appVersionCode=req.getHeader("App-Version-Code");
		Long date=req.getDateHeader("Date");
		
		logger.debug("##################Headers####################");
		
		logger.debug("X-Power2sme-Token :: "+String.valueOf(xPower2smeToken));
		logger.debug("Platform :: "+String.valueOf(platform));
		logger.debug("Device-Id :: "+String.valueOf(deviceId));
		logger.debug("Device-Name :: "+String.valueOf(deviceName));
		logger.debug("App-Version-Code :: "+String.valueOf(appVersionCode));
		logger.debug("Date :: "+String.valueOf(date));
		
		logger.debug("##################END####################");
		
		logger.info("exit");
		//IF ANY Header is missing
		return false;
	}
	
	private String getIp(HttpServletRequest request){
		System.out.println("---IP filter preHandle---");
		System.out.println(request.getRemoteAddr()+":"+request.getRemoteHost()+":"+request.getRemotePort()+":"+request.getRemoteUser()+":"+request.getServerName());
		String ipAddress = request.getHeader("X-FORWARDED-FOR");  // For Proxy
		   if (ipAddress == null) {  
			   ipAddress = request.getRemoteAddr();  
		   }
		   System.out.println(ipAddress);  
			String ip = request.getHeader("X-Forwarded-For");
			System.out.println("IP::"+String.valueOf(ip));
	        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	            ip = request.getHeader("Proxy-Client-IP");
	            System.out.println("IP::"+String.valueOf(ip));
	        }
	        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	            ip = request.getHeader("WL-Proxy-Client-IP");
	            System.out.println("IP::"+String.valueOf(ip));
	        }
	        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	            ip = request.getHeader("HTTP_CLIENT_IP");
	            System.out.println("IP::"+String.valueOf(ip));
	        }
	        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
	            System.out.println("IP::"+String.valueOf(ip));
	        }
	        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	            ip = request.getRemoteAddr();
	            System.out.println("IP::"+String.valueOf(ip));
	        }
	        System.out.println("IP::"+ip);
	        return ip;
	}
}