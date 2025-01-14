///**
// * @author swarnima kumar
// * @Date 03 July,2017
// * 
// * This class ensures that {@link SecurityConfig} gets registered before all other
// * Filters in Spring Security Filter chain
// * The class is close for extension since this is project specific
// * core configuration 
// * 
// */
//
//package com.power2sme.dms.config;
//
//import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
//
//public class SecurityWebApplicationInitializer extends AbstractSecurityWebApplicationInitializer {
//
//  public SecurityWebApplicationInitializer() {
//      super(SecurityConfig.class);
//  }
//}