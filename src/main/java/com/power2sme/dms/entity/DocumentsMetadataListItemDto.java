package com.power2sme.dms.entity;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class DocumentsMetadataListItemDto {
	private Integer documentTypeId;
	private String documentTypeName;
	private Integer filesCount;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMM d, yyyy h:mm:ss a", timezone = "Asia/Kolkata")
	private Date recentUploadedOn;
}
