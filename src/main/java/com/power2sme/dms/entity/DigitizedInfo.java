package com.power2sme.dms.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class DigitizedInfo implements Comparable<DigitizedInfo>{

	private Integer digitizedInfoId;
	private Integer parentId;
	private String key;
	private String value;
	private String type;
	private Integer order;
	public Integer getDigitizedInfoId() {
		return digitizedInfoId;
	}
	public void setDigitizedInfoId(Integer digitizedInfoId) {
		this.digitizedInfoId = digitizedInfoId;
	}
	public Integer getParentId() {
		return parentId;
	}
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	@Override
	public String toString() {
		return "DigitizedInfo{" +
				"digitizedInfoId=" + digitizedInfoId +
				", parentId=" + parentId +
				", key='" + key + '\'' +
				", value='" + value + '\'' +
				", type='" + type + '\'' +
				", order=" + order +
				'}';
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((digitizedInfoId == null) ? 0 : digitizedInfoId.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DigitizedInfo other = (DigitizedInfo) obj;
		if (digitizedInfoId == null) {
			if (other.digitizedInfoId != null)
				return false;
		} else if (!digitizedInfoId.equals(other.digitizedInfoId))
			return false;
		return true;
	}

	@Override
	public int compareTo(DigitizedInfo o) {
		if(o!=null && o.getOrder()!=null && order!=null)
		{
			if(getOrder()>o.getOrder())
			{
				return 1;
			}
			else if(getOrder()<o.getOrder())
			{
				return -1;
			}
		}
		return 0;
	}
	
	
}
