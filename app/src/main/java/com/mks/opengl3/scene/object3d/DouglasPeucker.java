package com.mks.opengl3.scene.object3d;

import java.util.ArrayList;
import java.util.List;

public class DouglasPeucker {

    public static List<Point2D> simplify(List<Point2D> points,
                                         float tolerance){

        if(points.size()<3)
            return points;

        boolean[] keep=new boolean[points.size()];

        keep[0]=true;
        keep[points.size()-1]=true;

        simplify(points,0,points.size()-1,tolerance,keep);

        ArrayList<Point2D> result=new ArrayList<>();

        for(int i=0;i<points.size();i++){

            if(keep[i])
                result.add(points.get(i));

        }

        return result;
    }

    private static void simplify(List<Point2D> pts,
                                 int start,
                                 int end,
                                 float tolerance,
                                 boolean[] keep){

        if(end<=start+1)
            return;

        float maxDistance=0;
        int index=-1;

        Point2D a=pts.get(start);
        Point2D b=pts.get(end);

        for(int i=start+1;i<end;i++){

            float d=distanceToLine(pts.get(i),a,b);

            if(d>maxDistance){

                maxDistance=d;
                index=i;

            }

        }

        if(maxDistance>tolerance){

            keep[index]=true;

            simplify(pts,start,index,tolerance,keep);
            simplify(pts,index,end,tolerance,keep);

        }

    }

    private static float distanceToLine(Point2D p,
                                        Point2D a,
                                        Point2D b){

        float dx=b.x-a.x;
        float dy=b.y-a.y;

        if(dx==0 && dy==0){

            dx=p.x-a.x;
            dy=p.y-a.y;

            return (float)Math.sqrt(dx*dx+dy*dy);

        }

        float t=((p.x-a.x)*dx+(p.y-a.y)*dy)/(dx*dx+dy*dy);

        float px=a.x+t*dx;
        float py=a.y+t*dy;

        dx=p.x-px;
        dy=p.y-py;

        return (float)Math.sqrt(dx*dx+dy*dy);

    }

}
