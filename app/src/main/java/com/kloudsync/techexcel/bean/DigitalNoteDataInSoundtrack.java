package com.kloudsync.techexcel.bean;


public class DigitalNoteDataInSoundtrack {

    private long id;
    private String lastStrokeId;
    private long duration;
    private String strokeId;
    private long newId;
    private long oldId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLastStrokeId() {
        return lastStrokeId;
    }

    public void setLastStrokeId(String lastStrokeId) {
        this.lastStrokeId = lastStrokeId;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getStrokeId() {
        return strokeId;
    }

    public void setStrokeId(String strokeId) {
        this.strokeId = strokeId;
    }

    public long getNewId() {
        return newId;
    }

    public void setNewId(long newId) {
        this.newId = newId;
    }

    public long getOldId() {
        return oldId;
    }

    public void setOldId(long oldId) {
        this.oldId = oldId;
    }
}
