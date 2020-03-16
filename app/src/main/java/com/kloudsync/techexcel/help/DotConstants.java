package com.kloudsync.techexcel.help;



public class DotConstants {

    //(本子铺码) A5纸规格尺寸：148mm × 210mm
    private static final int PAPER_WIDTH = 148;
    private static final int PAPER_HEIGHT = 210;

    //(本子铺码) OID4码点规格：1.524mm × 1.524mm
    public static final double XDIST_PERUNIT = 1.524;
    public static final double YDIST_PERUNIT = 1.524;

    //(本子铺码) 一英寸里大小 1in=2.54cm=25.40mm
    public static final float IN_SIZE = 25.40f;
    //(本子铺码) 一英寸里的像素
    public static final int IN_PIXEL = 600;

    //铺码生成的背景图片原始像素：4300 x 6048
    public static final int ORIGINAL_IMAGE_WIDTH = 4300;
    public static final int ORIGINAL_IMAGE_HEIGHT = 6048;

    //书写本子实际大小
    public static final double A5_WIDTH = (((double) ORIGINAL_IMAGE_WIDTH) / IN_PIXEL) * IN_SIZE;
    public static final double A5_HEIGHT = (((double) ORIGINAL_IMAGE_HEIGHT) / IN_PIXEL) * IN_SIZE;

    //原始背景图片过大，等比例缩小图片宽高得到合适大小背景图片（减少内存开销），放到app中作为背景图片
    public static int BG_REAL_WIDTH = ORIGINAL_IMAGE_WIDTH / 4;
    public static int BG_REAL_HEIGHT = ORIGINAL_IMAGE_HEIGHT / 4;

}