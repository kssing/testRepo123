package com.power2sme.dms.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@JsonInclude(Include.NON_NULL)
@Data
public class Verification implements Comparable<Verification>{
	private Integer verificationId;
	private Integer systemId;
	private String status;
	private String updatedBy;
	private String systemName;
	private Date createdTime;
	
	@Override
	public String toString() {
		return "Verification [systemId=" + systemId + ", status=" + status + ", updatedBy=" + updatedBy
				+ ", systemName=" + systemName + "]";
	}
	
	@Override
	public int compareTo(Verification o) {
		if(createdTime!=null &&  o!=null && o.getCreatedTime()!=null)
		{
			if(createdTime.after(o.getCreatedTime()))
			{
				return 1;
			}
			else if(createdTime.before(o.getCreatedTime()))
			{
				return -1;
			}	
		}
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Verification other = (Verification) obj;
		if (createdTime == null) {
			if (other.createdTime != null)
				return false;
		} else if (!createdTime.equals(other.createdTime))
			return false;
		if (systemId == null) {
			if (other.systemId != null)
				return false;
		} else if (!systemId.equals(other.systemId))
			return false;
		if (updatedBy == null) {
			if (other.updatedBy != null)
				return false;
		} else if (!updatedBy.equals(other.updatedBy))
			return false;
		if (verificationId == null) {
			if (other.verificationId != null)
				return false;
		} else if (!verificationId.equals(other.verificationId))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((createdTime == null) ? 0 : createdTime.hashCode());
		result = prime * result + ((systemId == null) ? 0 : systemId.hashCode());
		result = prime * result + ((updatedBy == null) ? 0 : updatedBy.hashCode());
		result = prime * result + ((verificationId == null) ? 0 : verificationId.hashCode());
		return result;
	}
	
	
	

}
