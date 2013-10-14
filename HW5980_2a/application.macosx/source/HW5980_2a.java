import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class HW5980_2a extends PApplet {

int mode = 0; //1: cloth
Particle[][] ptcs;

final int ny = 40;
final int nx = 40;

final int dx = 5;
final int dy = 5;

final int top = 100;
final int left = 500;

final float ks = 1; //stiffness
final float kd = 20; //damping factor

final float cod = 1; //coefficient of drag

float sphereR = 30;
PVector spherePos = new PVector(500, 500, 0);

public void setup() {
  frameRate(15);
  size(1200, 800, P3D);
  background(255);
  initCloth();
}

public void draw() {
  background(255);
  updateCloth();
  camera(mouseX, height/2, (height/2) / tan(PI/6), width/2, height/2, 0, 0, 1, 0);
  for (int x = 0; x < (nx-1); x++) {
    for (int y = 0; y < (ny-1); y++) { 
      line(ptcs[x][y].pos.x, ptcs[x][y].pos.y, ptcs[x][y].pos.z, 
      ptcs[x][y+1].pos.x, ptcs[x][y+1].pos.y, ptcs[x][y+1].pos.z);
      line(ptcs[x][y].pos.x, ptcs[x][y].pos.y, ptcs[x][y].pos.z, 
      ptcs[x+1][y].pos.x, ptcs[x+1][y].pos.y, ptcs[x+1][y].pos.z);
    }
  }
}
public void initCloth() {
  ptcs = new Particle[nx][ny];
  for (int x = 0; x < nx; x++) {
    for (int y = 0; y < ny; y++) { 
      ptcs[x][y] = new Particle(new PVector(top+x*dx, top+y*dy, 0));
    }
  }
}

public void updateCloth() {
  for (int x = 0; x < nx; x++) {
    for (int y = 0; y < ny; y++) { 
      ptcs[x][y].vel = ptcs[x][y].oldVel.get();
    }
  }
  for (int x = 0; x < (nx-1); x++) {
    for (int y = 0; y < ny; y++) { 
      PVector e = (ptcs[x+1][y].pos).get();
      e.sub(ptcs[x][y].pos);
      float l = sqrt(e.get().dot(e));
      e.div(l);
      float v1 = e.get().dot(ptcs[x][y].vel);
      float v2 = e.get().dot(ptcs[x+1][y].vel);
      float f = -ks*(dx-l)-kd*(v1-v2);
      PVector fmulte = e.get();
      fmulte.mult(f);      
      ptcs[x][y].vel.add(fmulte);
      ptcs[x+1][y].vel.sub(fmulte);
    }
  }
  for (int x = 0; x < nx; x++) {
    for (int y = 0; y < (ny - 1); y++) { 
      PVector e = (ptcs[x][y+1].pos).get();
      e.sub(ptcs[x][y].pos);
      float l = sqrt(e.get().dot(e));
      e.div(l);
      float v1 = e.get().dot(ptcs[x][y].vel);
      float v2 = e.get().dot(ptcs[x][y+1].vel);
      float f = -ks*(dx-l)-kd*(v1-v2);
      PVector fmulte = e.get();
      fmulte.mult(f);
      ptcs[x][y].vel.add(fmulte);
      ptcs[x][y+1].vel.sub(fmulte);
    }
  }
  for (int x = 0; x < (nx - 1); x++) {
    for (int y = 0; y < (ny - 1); y++) {
      PVector crossp1 = ptcs[x][y+1].pos.get();
      crossp1.sub(ptcs[x][y].pos);
      PVector crossp2 = ptcs[x+1][y].pos.get();
      crossp2.sub(ptcs[x][y].pos);
      PVector crossp = crossp1.cross(crossp2);
      float area = sqrt(crossp.dot(crossp));
      PVector vavg = (ptcs[x][y].oldVel).get();
      vavg.add(ptcs[x][y+1].oldVel);
      vavg.add(ptcs[x+1][y].oldVel);
      vavg.add(ptcs[x+1][y+1].oldVel);
      vavg.mult(0.25f);
      float vmag = sqrt(vavg.dot(vavg));
      PVector vdrag = crossp.get();
      vdrag.mult(-cod*(vmag*(vavg.dot(crossp))/area));
      vdrag.div(4);
      ptcs[x][y].drag.add(vdrag);
      ptcs[x+1][y].drag.add(vdrag);
      ptcs[x][y+1].drag.add(vdrag);
      ptcs[x+1][y+1].drag.add(vdrag);
    }
  }

  for (int y = 0; y < ny; y++) {
    ptcs[0][y].drag = ptcs[1][y].drag.get();
    ptcs[nx-1][y].drag = ptcs[nx-2][y].drag.get();
  }

  for (int x = 0; x < nx; x++) {
    ptcs[x][0].drag = ptcs[x][1].drag.get();
    ptcs[x][ny-1].drag = ptcs[x][ny-2].drag.get();
  }

  for (int x = 0; x < nx; x++) {
    for (int y = 0; y < ny; y++) {
      ptcs[x][y].vel.add(ptcs[x][y].drag);
      ptcs[x][y].vel.add(new PVector(0, 1, 0));//gravity
    }
  }
  for (int x = 0; x < nx; x++) {
    for (int y = 0; y < ny; y++) {
      ptcs[x][y].oldVel = ptcs[x][y].vel.get();
      ptcs[x][y].pos.add(ptcs[x][y].oldVel);
    }
  }
}

class Particle {  
  PVector pos; //p

  PVector vel; //vn
  PVector oldVel; //v
  
  PVector drag;

  Particle(PVector pos) {
    this.pos = pos.get();
    vel = new PVector(0, 0, 0);
    oldVel = new PVector(0, 0, 0);
    drag = new PVector(0, 0, 0);
  }
  

}

  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "HW5980_2a" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
