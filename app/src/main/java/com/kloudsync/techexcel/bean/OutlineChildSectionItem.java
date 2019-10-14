package com.kloudsync.techexcel.bean;

import java.util.List;

/**
 * Created by tonyan on 2019/10/14.
 */

public class OutlineChildSectionItem {

    String IdeaID;
    String SectionTitle;
    int OutLineID;
    int Expanded;
    int ParentOutLineID;
    String KeyWord;
    OutlineSectionPisition SectionPosition;
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

    public int getOutLineID() {
        return OutLineID;
    }

    public void setOutLineID(int outLineID) {
        OutLineID = outLineID;
    }

    public int getExpanded() {
        return Expanded;
    }

    public void setExpanded(int expanded) {
        Expanded = expanded;
    }

    public int getParentOutLineID() {
        return ParentOutLineID;
    }

    public void setParentOutLineID(int parentOutLineID) {
        ParentOutLineID = parentOutLineID;
    }

    public String getKeyWord() {
        return KeyWord;
    }

    public void setKeyWord(String keyWord) {
        KeyWord = keyWord;
    }

    public OutlineSectionPisition getSectionPosition() {
        return SectionPosition;
    }

    public void setSectionPosition(OutlineSectionPisition sectionPosition) {
        SectionPosition = sectionPosition;
    }

    public List<OutlineChildSectionItem> getChildSectionItems() {
        return ChildSectionItems;
    }

    public void setChildSectionItems(List<OutlineChildSectionItem> childSectionItems) {
        ChildSectionItems = childSectionItems;
    }
}
