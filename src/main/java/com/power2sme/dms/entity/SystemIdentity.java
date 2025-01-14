package com.power2sme.dms.entity;

public class SystemIdentity {
	private Integer systemId;
	private String systemName;
	public Integer getSystemId() {
		return systemId;
	}
	public void setSystemId(Integer systemId) {
		this.systemId = systemId;
	}
	public String getSystemName() {
		return systemName;
	}
	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}
	@Override
	public String toString() {
		return "systemIdentity [systemId=" + systemId + ", systemName=" + systemName + "]";
	}		
}
