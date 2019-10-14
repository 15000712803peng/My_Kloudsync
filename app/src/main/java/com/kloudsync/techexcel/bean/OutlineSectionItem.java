package com.kloudsync.techexcel.bean;

import java.util.List;

/**
 * Created by tonyan on 2019/10/14.
 */

public class OutlineSectionItem {
    String IdeaID;
    String SectionTitle;
    OutlineSectionPisition SectionPosition;
    long OutLineID;
    int Expanded;
    long ParentOutLineID;
    String KeyWords;
    List<OutlineChildSectionItem> ChildSectionItems;

    public String getIdeaID() {
        return IdeaID;
    }

    public void setIdeaID(String ideaID) {
        IdeaID = ideaID;
    }

    public String getSectionTitle() {
        return SectionTitle;
    }

    public void setSectionTitle(String sectionTitle) {
        SectionTitle = sectionTitle;
    }

    public OutlineSectionPisition getSectionPosition() {
        return SectionPosition;
    }

    public void setSectionPosition(OutlineSectionPisition sectionPosition) {
        SectionPosition = sectionPosition;
    }

    public long getOutLineID() {
        return OutLineID;
    }

    public void setOutLineID(long outLineID) {
        OutLineID = outLineID;
    }

    public int getExpanded() {
        return Expanded;
    }

    public void setExpanded(int expanded) {
        Expanded = expanded;
    }

    public long getParentOutLineID() {
        return ParentOutLineID;
    }

    public void setParentOutLineID(long parentOutLineID) {
        ParentOutLineID = parentOutLineID;
    }

    public String getKeyWords() {
        return KeyWords;
    }

    public void setKeyWords(String keyWords) {
        KeyWords = keyWords;
    }

    public List<OutlineChildSectionItem> getChildSectionItems() {
        return ChildSectionItems;
    }

    public void setChildSectionItems(List<OutlineChildSectionItem> childSectionItems) {
        ChildSectionItems = childSectionItems;
    }
}
