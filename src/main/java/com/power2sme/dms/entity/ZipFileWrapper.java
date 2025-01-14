package com.power2sme.dms.entity;

import java.util.List;

public class ZipFileWrapper {

    private String smeName;
    private String smeId;
    private List<ZipFileDocuments> zipFileDocumentsList;

    public String getSmeName() {
        return smeName;
    }

    public void setSmeName(String smeName) {
        this.smeName = smeName;
    }

    public String getSmeId() {
        return smeId;
    }

    public void setSmeId(String smeId) {
        this.smeId = smeId;
    }

    public List<ZipFileDocuments> getZipFileDocumentsList() {
        return zipFileDocumentsList;
    }

    public void setZipFileDocumentsList(List<ZipFileDocuments> zipFileDocumentsList) {
        this.zipFileDocumentsList = zipFileDocumentsList;
    }

    @Override
    public String toString() {
        return "ZipFileWrapper{" +
                "smeName='" + smeName + '\'' +
                ", smeId='" + smeId + '\'' +
                ", zipFileDocumentsList=" + zipFileDocumentsList +
                '}';
    }
}
