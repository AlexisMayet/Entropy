class Agent {
  // CUSTOMIZABLE BELOW:

  boolean collisionCenterDir;
  boolean spawnCenterDir;
  boolean correctAngle;

  // SPIRAL CUSTOMIZATION
  float tScale = 15;
  float a = 500;

  // END OF CUSTOMIZABLE

  float speed;
  float acc;

  PVector pos;
  float angle;

  int radius;

  float size;
  int age;

  color ac;
  color pc;
  color contour;
  color[] palette;
  String colorChange;
  int cIndex;
  float diff = 1;
  boolean bounced;
  Canvas canvas;

  // Create
  Agent(Canvas canvas, boolean collisionCenterDir, boolean spawnCenterDir, boolean correctAngle, float speed, float acc, String spawn, String detail, int radius, float size, int age, color[] palette, color contour, String colorChange) {
    this.canvas = canvas;

    this.spawnCenterDir = spawnCenterDir;
    this.collisionCenterDir = collisionCenterDir;
    this.correctAngle = correctAngle;


    this.contour = contour;
    this.palette = palette;
    this.colorChange = colorChange;
    this.cIndex = 1;
    this.pc = palette[cIndex-1];
    this.ac = palette[cIndex];

    this.speed = speed;
    this.acc = acc;
    this.size = size;
    this.radius = radius;
    this.age = age;

    this.angle = random(2*PI);

    if (spawn == "center") { // Spawn in center (customizable)
      this.pos = new PVector(width/2, height/2);
    } else if (spawn == "corners") {
      float[] angles = {PI/4, 3*PI/4, 5*PI/4, 7*PI/4};
      boolean[] isInCorner = {false, false, false, false};
      int i = (int)random(4);
      float spawnAngle = angles[i]; //BOT RIGHT
      isInCorner[i] = true;
      float x = width/2 + canvas.maxDistance * cos(spawnAngle);
      float y = height/2 + canvas.maxDistance * sin(spawnAngle);
      this.pos = new PVector(x, y);

      if (correctAngle) { // Correct angle to point towards canvas
        float[][] correctAngles;
        if (canvas.shape == "square") {
          correctAngles = new float[][]{{PI, 3*PI/2}, {3*PI/2, 2*PI}, {0, PI/2}, {PI/2, PI}};
        } else {
          correctAngles = new float[][]{{3*PI/4, 7*PI/4}, {-3*PI/4, PI/4}, {7*PI/4, 11*PI/4}, {PI/4, 5*PI/4}};
        }

        if (isInCorner[0]) { // Bot right: angle to top left
          this.angle = random(correctAngles[0][0], correctAngles[0][1]);
        } else if (isInCorner[1]) { // Bot left: angle to top right
          this.angle = random(correctAngles[1][0], correctAngles[1][1]);
        } else if (isInCorner[2]) { // Top left: angle to bot right
          this.angle = random(correctAngles[2][0], correctAngles[2][1]);
        } else if (isInCorner[3]) { // Top right: angle to bot left
          this.angle = random(correctAngles[3][0], correctAngles[3][1]);
        }
      }
    } else if (spawn == "edges") { // Spawn on edges
      // Define 4 edge centers
      PVector [] edges = {
        new PVector(0 + canvas.pad, height/2),
        new PVector(width/2, 0+canvas.pad),
        new PVector(width-canvas.pad, height/2),
        new PVector(width/2, height - canvas.pad)
      };
      this.pos = edges[int(random(4))];

      if (correctAngle) { // Correct angle to point towards canvas
        if (this.pos.equals(edges[0])) { // Left edge: angle to the right
          this.angle = random(3*PI/2, 5*PI/2);
        } else if (this.pos.equals(edges[1])) { // Top edge: angle to the bot
          this.angle = random(0, PI);
        } else if (this.pos.equals(edges[2])) { // Right edge: angle to the left
          this.angle = random(PI/2, 3*PI/2);
        } else if (this.pos.equals(edges[3])) { // Bot edge: angle to the top
          this.angle = random(PI, 2*PI);
        }
      }
    }

    if (spawn == "random") { // Random position
      this.pos = new PVector(random(pad, width-pad), random(pad, height-pad));
    } else if (spawn == "spiral") { // Spiral position
      // Equation for spiral: x(t) = a * t * cos(t), y(t) = a * t * sin(t)
      float t = random(1) * tScale;
      float offsetX = a * t * cos(t);
      float offsetY = a * t * sin(t);
      float x = width/2 ;
      float y = height/2 ;
      if (random(1)>0.5) {
        x += offsetX;
        y += offsetY;
      } else {
        x -= offsetX;
        y -= offsetY;
      }
      this.pos = new PVector(x, y);
    } else {
      float xfactor = cos(angle);
      float yfactor = sin(angle);

      if (detail == "on") {
        xfactor *= radius;
        yfactor *= radius;
      } else if (detail == "in") {
        xfactor *= random(1) * radius;
        yfactor *= random(1) * radius;
      }
      this.pos.add(new PVector(xfactor, yfactor));
    }

    if (spawnCenterDir) { // Change angle to point to the center
      this.angle = atan2(pos.y - height/2, pos.x - width/2) + PI;
    }
  }

  // Update
  void update() {
    // Update position
    PVector dir = new PVector(cos(angle), sin(angle));
    this.pos.add(dir.mult(speed));
    this.speed += this.acc; // Apply acceleration
    float distance = sqrt(pow(pos.x - width/2, 2) + pow(pos.y - height/2, 2));

    if (distance <= canvas.maxDistance+size) {
      canvas.bounce(this);
    } else {
      distance = canvas.maxDistance-size;
    }


    if (colorChange == "bounce" && this.bounced) {//COLOR CHANGE ON BOUNCE
      colorChange();
      this.bounced = false;
    } else if (colorChange == "distance") {
      float fi = map(distance, 0, canvas.maxDistance, 0, palette.length-1); //float index
      int ip = floor(fi);
      this.diff = fi - ip;
      int ia = ip+1;
      //println("phero index : " + ip);
      //println("agent index : " + ia);
      if (ip >= palette.length) {
        ip= palette.length-1;
      }
      if (ia >= palette.length) {
        ia = 0;
      }
      this.pc = palette[ia];
      this.ac = palette[ip];
    }
  }

  void colorChange() {
    this.pc = this.ac;
    this.cIndex ++;
    if (cIndex == palette.length) {
      cIndex = 0;
    }
    this.ac = palette[cIndex];
  }

  // Display
  void show() {
    fill(this.contour);
    noStroke();
    rect(pos.x, pos.y, this.size, this.size);
  }
}
