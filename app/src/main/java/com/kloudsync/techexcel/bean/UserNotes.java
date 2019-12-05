package com.kloudsync.techexcel.bean;

import java.util.List;

public class UserNotes{
        private String paramsId;
        private String userId;
        private String userName;
        private int noteCount;
        private List<NoteDetail> notes;
        public String getParamsId() {
            return paramsId;
        }

        public void setParamsId(String paramsId) {
            this.paramsId = paramsId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public int getNoteCount() {
            return noteCount;
        }

        public void setNoteCount(int noteCount) {
            this.noteCount = noteCount;
        }

        public List<NoteDetail> getNotes() {
            return notes;
        }

        public void setNotes(List<NoteDetail> notes) {
            this.notes = notes;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            UserNotes userNotes = (UserNotes) o;

            return userId != null ? userId.equals(userNotes.userId) : userNotes.userId == null;
        }

        @Override
        public int hashCode() {
            return userId != null ? userId.hashCode() : 0;
        }

        @Override
        public String toString() {
            return "UserNotes{" +
                    "userId='" + userId + '\'' +
                    ", userName='" + userName + '\'' +
                    ", noteCount=" + noteCount +
                    '}';
        }
    }
