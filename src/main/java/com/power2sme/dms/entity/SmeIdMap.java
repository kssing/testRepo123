package com.power2sme.dms.entity;

public class SmeIdMap {
	
	private String smeId;
	private String uniqueId;
	public String getSmeId() {
		return smeId;
	}
	public void setSmeId(String smeId) {
		this.smeId = smeId;
	}
	public String getUniqueId() {
		return uniqueId;
	}
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}
	@Override
	public String toString() {
		return "SmeIdMap [smeId=" + smeId + ", uniqueId=" + uniqueId + "]";
	}

}
