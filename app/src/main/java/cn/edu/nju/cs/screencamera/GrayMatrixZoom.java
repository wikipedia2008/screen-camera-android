package cn.edu.nju.cs.screencamera;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Created by zhantong on 16/4/22.
 */
public class GrayMatrixZoom extends GrayMatrix {
    public Point[][] pixels;
    public int width;
    public int height;
    public GrayMatrixZoom(int dimensionX, int dimensionY){
        pixels=new Point[dimensionX*dimensionY][];
        width=dimensionX;
        height=dimensionY;
    }
    public int get(int x, int y) {
        int offset = y * width + x;
        return pixels[offset][0].value;
    }
    public Point[] getPoints(int x, int y) {
        int offset = y * width + x;
        return pixels[offset];
    }
    public int[] getSamples(int x,int y){
        Point[] samples=getPoints(x,y);
        int[] values=new int[samples.length];
        for(int i=0;i<samples.length;i++){
            values[i]=samples[i].value;
        }
        return values;
    }
    public void set(int x,int y,Point[] samples){
        int offset = y * width + x;
        pixels[offset] = samples;
    }
    public void print(){
        System.out.println("width:"+width+"\theight:"+height);
        for(int y=0;y<height;y++){
            for(int x=0;x<width;x++){
                System.out.print("[");
                for(Point val:getPoints(x,y)){
                    System.out.print("("+val.x+","+val.y+","+val.value+") ");
                }
                System.out.print("]");
            }
            System.out.println();
        }
    }
    public void set(int x,int y,int pixel,int origX,int origY){
    }
    public Point getPoint(int x, int y){
        return null;
    }
    public void print(int x,int y){
        Point[] points=getPoints(x,y);
        System.out.println("("+x+","+y+"): ("+points[0].x+","+points[0].y+","+points[0].value+")\t("+
                points[1].x+","+points[1].y+","+points[1].value+")\t("+
                points[2].x+","+points[2].y+","+points[2].value+")\t("+
                points[3].x+","+points[3].y+","+points[3].value+")\t("+
                points[4].x+","+points[4].y+","+points[4].value+")");
    }
    public JsonNode toJSON(){
        ObjectMapper mapper=new ObjectMapper();
        JsonNode root=mapper.createObjectNode();
        ((ObjectNode)root).set("width", IntNode.valueOf(width));
        ((ObjectNode)root).set("height", IntNode.valueOf(height));
        ArrayNode samplePointsNode=mapper.createArrayNode();
        for(Point[] samplePoint:pixels){
            ObjectNode node=mapper.createObjectNode();
            ArrayNode pointsNode=mapper.createArrayNode();
            for(Point point:samplePoint){
                /*
                ObjectNode pointNode=mapper.createObjectNode();
                pointNode.set("x",IntNode.valueOf(point.x));
                pointNode.set("y",IntNode.valueOf(point.y));
                pointNode.set("value",IntNode.valueOf(point.value));
                pointsNode.add(pointNode);
                */

                pointsNode.add(IntNode.valueOf(point.value));
            }
            node.set("values",pointsNode);
            node.set("originX",IntNode.valueOf(samplePoint[0].x));
            node.set("originY",IntNode.valueOf(samplePoint[0].y));
            samplePointsNode.add(node);
        }
        ((ObjectNode)root).set("data",samplePointsNode);
        return root;
    }
    public String toString(){
        return toJSON().toString();
    }
}
