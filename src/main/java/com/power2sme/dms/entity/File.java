package com.power2sme.dms.entity;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class File {
	private String fileId;
	private MultipartFile file;
	private String fileName;
	private long fileSize;
	@JsonFormat(pattern = "MMM dd, yyyy hh:mm:ss a")
	private Date fileModifiedAt;
	private Integer timeToLive;

	public Integer getTimeToLive() {
		return timeToLive;
	}

	public void setTimeToLive(Integer timeToLive) {
		this.timeToLive = timeToLive;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public Date getFileModifiedAt() {
		return fileModifiedAt;
	}

	public void setFileModifiedAt(Date fileModifiedAt) {
		this.fileModifiedAt = fileModifiedAt;
	}

	@Override
	public String toString() {
		return "File{" + "fileId='" + fileId + '\'' + ", file=" + file + ", fileName='" + fileName + '\''
				+ ", fileSize=" + fileSize + ", fileModifiedAt=" + fileModifiedAt + '}';
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fileId == null) ? 0 : fileId.hashCode());
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
		File other = (File) obj;
		if (fileId == null) {
			if (other.fileId != null)
				return false;
		} else if (!fileId.equals(other.fileId))
			return false;
		return true;
	}

}
