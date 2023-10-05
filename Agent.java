class Agent {
  // CUSTOMIZABLE BELOW:
  
  // Collision direction: Whether agents point towards center after collision with the wall
  boolean collisionCenterDir = true;
  
  // Center direction: Whether spawn direction is aimed at the center
  boolean spawnCenterDir = false;
  
  // Correct angle: Whether to correct spawn angle to be directed within the grid (for "corners" and "edges" spawns)
  boolean correctAngle = false;
  
  // SPIRAL CUSTOMIZATION
  float tScale = 200;
  float a = 5;
  
  // END OF CUSTOMIZABLE
  
  float speed;
  float acc;
  
  PVector pos;
  float angle;
  
  int radius;
 
  int[] rgb;
  float size;
  int age;
  
  String canvas;
  
  // Create
  Agent(float speed, float acc, String spawn, String detail, int radius, String canvas, float size, int age, int[] rgb){
    this.speed = speed;
    this.acc = acc;
    this.size = size;
    this.rgb = rgb;
    this.radius = radius;
    this.age = age;
    this.canvas = canvas;
    this.angle = random(2*PI);
    
    if(spawn == "center"){ // Spawn in center (customizable)
      this.pos = new PVector(width/2, height/2);
    } else if(spawn == "corners"){ // Spawn in corners
      // Choose corners randomly
      int[] xs = {0, width};
      int[] ys = {0, height};
      this.pos = new PVector(xs[int(random(0, 2))], ys[int(random(0, 2))]);
      
      if (correctAngle) { // Correct angle to point towards canvas
        if(this.pos.equals(new PVector(0, 0))){ // Top left: angle to bot right
          this.angle = random(0, PI/2);
        } else if(this.pos.equals(new PVector(0, height))){ // Bot left: angle to top right
          this.angle = random(3*PI/2, 2*PI);
        } else if(this.pos.equals(new PVector(width, 0))){ // Top right: angle to bot left
          this.angle = random(PI/2, PI);
        } else if(this.pos.equals(new PVector(width, height))){ // Bot right: angle to top left
          this.angle = random(PI, 3*PI/2);
        }
      }
    } else if(spawn == "edges"){ // Spawn on edges
      // Define 4 edge centers
      PVector [] edges = {
        new PVector(0, height/2), 
        new PVector(width/2, 0), 
        new PVector(width, height/2), 
        new PVector(width/2, height)
      };
      this.pos = edges[int(random(4))];
      
      if (correctAngle) { // Correct angle to point towards canvas
        if(this.pos.equals(new PVector(0, height/2))){ // Left edge: angle to the right
          this.angle = random(3*PI/2, 5*PI/2);
        } else if(this.pos.equals(new PVector(width/2, 0))){ // Top edge: angle to the bot
          this.angle = random(0, PI);
        } else if(this.pos.equals(new PVector(width, height/2))){ // Right edge: angle to the left
          this.angle = random(PI/2, 3*PI/2);
        } else if(this.pos.equals(new PVector(width/2, height))){ // Bot edge: angle to the top
          this.angle = random(PI, 2*PI);
        }
      }
    }
    
    if(spawn == "random"){ // Random position
      this.pos = new PVector(random(width), random(height));
    } else if(spawn == "spiral"){ // Spiral position
      // Equation for spiral: x(t) = a * t * cos(t), y(t) = a * t * sin(t) 
      float t = random(1) * tScale;
      float x = width/2 + a * t * cos(t);
      float y = height/2 + a * t * sin(t);
      this.pos = new PVector(x, y);
    } else {
      float xfactor = cos(angle);
      float yfactor = sin(angle);
      
      if(detail == "on"){
        xfactor *= radius;
        yfactor *= radius;
      } else if(detail == "in"){
        xfactor *= random(1) * radius;
        yfactor *= random(1) * radius;
      }
      this.pos.add(new PVector(xfactor, yfactor));
    }
    
    if(spawnCenterDir){ // Change angle to point to the center
      this.angle = atan2(pos.y - height/2, pos.x - width/2) + PI;
    }
  }
  
  // Update
  void update(){
    // Update position
    PVector dir = new PVector(cos(angle), sin(angle));
    this.pos.add(dir.mult(speed));
    
    // Collision
    if(collisionCenterDir){ // Angle towards center
      if(canvas != "circle"){ // Bounce on square
        if(pos.x <= 0 || pos.x >= width || pos.y <= 0 || pos.y >= height){
          this.angle = atan2(pos.y - height/2, pos.x - width/2) + PI;
        }
      } else { // Bounce on circle (with radius == width)
        if(sqrt(pow((pos.x - width/2), 2) + pow((pos.y - height/2), 2)) > width/2){ // Out of bounds
          this.angle = atan2(pos.y - height/2, pos.x - width/2) + PI;
        }
      }
    } else { // Angle bounces on wall
      if(pos.x <= 0 || pos.x >= width){ // Vertical wall
        this.angle = PI - this.angle;
      } else if(pos.y <= 0 || pos.y >= height){ // Horizontal wall
        this.angle = -this.angle;
      }
    }
    
    this.speed += this.acc; // Apply acceleration
  }
  
  // Display 
  void show(){
    fill(rgb[0], rgb[1], rgb[2]);
    noStroke();
    rect(pos.x, pos.y, this.size, this.size);
  }
}
