package cn.edu.nju.cs.screencamera;

/**
 * Created by zhantong on 16/4/24.
 */
public class MatrixFactory {
    public static Matrix createMatrix(BarcodeFormat barcodeFormat,byte[] pixels,int imgColorType, int imgWidth, int imgHeight,int[] initBorder) throws NotFoundException{
        Matrix matrix=null;
        switch (barcodeFormat){
            case NORMAL:
                matrix=new MatrixNormal(pixels,imgColorType,imgWidth,imgHeight,initBorder);
                break;
            case ZOOM:
                matrix=new MatrixZoom(pixels,imgColorType,imgWidth,imgHeight,initBorder);
                break;
            case ZOOMVARY:
                matrix=new MatrixZoomVary(pixels,imgColorType,imgWidth,imgHeight,initBorder);
                break;
            case ZOOMVARYALT:
                matrix=new MatrixZoomVaryAlt(pixels,imgColorType,imgWidth,imgHeight,initBorder);
                break;
            case ZOOMVARYALTBAR:
                matrix=new MatrixZoomVaryAltBar(pixels,imgColorType,imgWidth,imgHeight,initBorder);
                break;
        }
        return matrix;
    }
    public static Matrix createMatrix(BarcodeFormat barcodeFormat){
        Matrix matrix=null;
        switch (barcodeFormat){
            case NORMAL:
                matrix=new MatrixNormal();
                break;
            case ZOOM:
                matrix=new MatrixZoom();
                break;
            case ZOOMVARY:
                matrix=new MatrixZoomVary();
                break;
            case ZOOMVARYALT:
                matrix=new MatrixZoomVaryAlt();
                break;
            case ZOOMVARYALTBAR:
                matrix=new MatrixZoomVaryAltBar();
                break;
        }
        return matrix;
    }
}
