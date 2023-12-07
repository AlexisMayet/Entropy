class Canvas {
  String shape;
  int pad;
  float maxDistance;

  Canvas(String shape, int pad) {
    this.shape = shape;
    this.pad = pad;

    if (shape == "square") {
      this.maxDistance = sqrt(pow(0 - width/2 - pad, 2) + pow(0 - height/2 - pad, 2));
    } else if (shape == "circle") {
      this.maxDistance = sqrt(pow(pad - width/2, 2));
    }
  }


  void bounce(Agent a) {
    if (a.collisionCenterDir) { //Angle towards center

      if (shape!="circle") { //Bounce on square

        if (a.pos.x <= 0 + pad || a.pos.x>=width - pad || a.pos.y<=0 + pad || a.pos.y>= height - pad) {

          a.angle = atan2(a.pos.y - height/2, a.pos.x - width/2) + PI;
        }
      } else { //Bounce on circle (w radius == width)

        if ( sqrt(pow((a.pos.x - width/2), 2) + pow((a.pos.y - height/2), 2)) > (width/2) - pad) { //Out of bounds

          a.angle = atan2(a.pos.y - height/2, a.pos.x - width/2) + PI;
        }
      }
    } else { //Agent bounces on wall
      if (shape!="circle") {
        if (a.pos.x<=0 + pad || a.pos.x>= width - pad) {//Vertical wall
          a.angle = PI - a.angle;
        } else if (a.pos.y<= 0  + pad|| a.pos.y>= height - pad) {//Horizontal wall
          a.angle = -a.angle;
        }
      } else { //Bounce off sphere
        if (sqrt(pow(a.pos.x - width/2, 2) + pow(a.pos.y - height/2, 2)) > width/2 - pad ) { //Out of bounds
          //println("Out of bounds");
          PVector agentToCenter = PVector.sub(a.pos, new PVector(width/2 - pad, height/2 - pad));
          float angleToCenter = atan2(agentToCenter.y, agentToCenter.x);
          float tangentAngle = angleToCenter + PI / 2;
          float angleDiff = a.angle - tangentAngle;
          a.angle = a.angle - 2 * angleDiff;
          float newPositionDistance = width/2 - pad - 2;
          a.pos = new PVector(width/2 + newPositionDistance * cos(angleToCenter), height/2 + newPositionDistance * sin(angleToCenter));
        }
      }
    }
  }
}
