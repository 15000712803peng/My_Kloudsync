package com.kloudsync.techexcel.bean;

public class BookNote {

    public String documentId;
    public String parentUniqueId = "";
    public String title = "";
    public int associationType = 0;
    public String associationId;
    public boolean jumpBackToNote = true;

    public BookNote() {

    }

    public BookNote setDocumentId(String documentId) {
        this.documentId = documentId;
        return this;
    }

    public BookNote setParentUniqueId(String parentUniqueId) {
        this.parentUniqueId = parentUniqueId;
        return this;
    }

    public BookNote setTitle(String title) {
        this.title = title;
        return this;
    }

    public BookNote setAssociationType(int associationType) {
        this.associationType = associationType;
        return this;
    }

    public BookNote setAssociationId(String associationId) {
        this.associationId = associationId;
        return this;
    }

    public BookNote setJumpBackToNote(boolean jumpBackToNote) {
        this.jumpBackToNote = jumpBackToNote;
        return this;
    }

    @Override
    public String toString() {
        return "BookNote{" +
                "documentId='" + documentId + '\'' +
                ", parentUniqueId='" + parentUniqueId + '\'' +
                ", title='" + title + '\'' +
                ", associationType=" + associationType +
                ", associationId='" + associationId + '\'' +
                ", jumpBackToNote=" + jumpBackToNote +
                '}';
    }
}
