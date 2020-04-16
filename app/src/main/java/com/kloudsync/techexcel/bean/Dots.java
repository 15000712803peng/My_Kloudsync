package com.kloudsync.techexcel.bean;

import java.io.Serializable;

public class Dots implements Serializable {
	public Dots(int BookID, int PageID, float x, float y, int f, int t, int width, int color, int counter, int angle) {
		bookID = BookID;
		pageID = PageID;
		pointX = x;
		pointY = y;
		force = f;
		ntype = t;
		penWidth = width;
		ncolor = color;
		ncounter = counter;
		nangle = angle;
	}

	public int bookID;
	public int pageID;
	public int ncounter;
	public float pointX;
	public float pointY;
	public int force;
	public int ntype;  //0-down;1-move;2-up;
	public int penWidth;
	public int ncolor;
	public int nangle;

	@Override
	public String toString() {
		return "Dots{" +
				"bookID=" + bookID +
				", pageID=" + pageID +
				", ncounter=" + ncounter +
				", pointX=" + pointX +
				", pointY=" + pointY +
				", force=" + force +
				", ntype=" + ntype +
				", penWidth=" + penWidth +
				", ncolor=" + ncolor +
				", nangle=" + nangle +
				'}';
	}
}
