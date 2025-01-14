package com.power2sme.dms.entity;

public class ZipFileDocuments {
    private Integer documentTypeId;
    private Integer versionNo;

    public Integer getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(Integer documentTypeId) {
        this.documentTypeId = documentTypeId;
    }

    public Integer getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(Integer versionNo) {
        this.versionNo = versionNo;
    }

    @Override
    public String toString() {
        return "ZipFileDocumentsId{" +
                "documentTypeId=" + documentTypeId +
                ", versionNo=" + versionNo +
                '}';
    }
}
