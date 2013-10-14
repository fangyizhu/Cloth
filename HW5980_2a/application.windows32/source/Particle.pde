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

