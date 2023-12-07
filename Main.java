// CUSTOMIZABLE BELOW: (CHECK AGENT CLASS FOR ADVANCED COLLISION & SPAWN MECHANICS)

// AGENT: agents that move on the canvas

// Number of agents
int numAgents = 1000;
// Agent size
float agentSize = 5;
// Agent Speed :
float speed = 5;
// Agent Acceleration: rate at which agent speed increases
float acc = 0.00;
// Max agents: number of maximum agents (cap agents to maintain performance)
int maxAgents = 4 * numAgents;

//Colision direction : whether agents points towards center after collision with wall
boolean collisionCenterDir = true;

//Center direction : whether spawn direction is aimed at center
boolean spawnCenterDir = true;

//Correct angle : whether to correct spawn angle to be directed within the grid (for "corners" and "edges" spawns)
boolean correctAngle = true;

// PHEROMONES: trail left behind by agents

// Pheromone threshold: min value for a pheromone to exist. (CUT TRAIL OFF)
// (!!Increase this value to increase performance!!)
float pheroThreshold = 0.2;
// Pheromone decay speed: speed at which pheromones (trails) will fade (SHORTEN TRAIL)
float pheroDecay = 0.05;

// SPAWN: spawn parameters for agents

// spawnAtInit: whether agents will spawn at initialization
boolean spawnAtInit = false;

/* spawn: specify the spawning point of agents:
 - center: agents will spawn at center of the screen
 - corners: agents will spawn at corners of the screen
 - edges: agents will spawn at edges of the screen
 - random: agents will spawn at random places
 - spiral: agents will spawn on a spiral (see agent class CUSTOMIZABLE to customize spiral) */
String spawn = "corners";

/* Circle spawn: spawn parameters for circle on spawning point
 - detail: "on" agents spawn on the edge of the circle defined by radius in the center
 "in" agents spawn in the circle defined by radius (random pos in circle)
 "off" agents spawn in the center of the environment */
String detail = "on";

// Radius: radius of circle spawn
int radius = 100;

/* CANVAS: define boundaries for agents to bounce on :
 square: agents will bounce on a square canvas
 circle: agents will bounce on a circular canvas */
Canvas canvas;
String shape = "circle";
int pad = 100;

// DISPLAY

// heads: indicate whether head of the trail is displayed on top (true) or below (false) the tail of the trail.
// (WARNING: for increased performance, false is preferable)
boolean heads = false;

// COLORS

int[] rgbA = {255, 255, 255}; // Agent color
int[] rgbB = {0, 0, 0};       // Background color
int[] rgbP = {0, 0, 0};       // Color pheromone will fade to

// CLICK
/* Click type: what program does when clicking (left-click / right-click)
 - true: spawn new gen / delete old gen
 - false: attract agents / push agents away from point of click
 */
boolean clickType = true;

// RECORDING: recording parameters for saving frames

boolean recording = false;     // Whether to record simulation
int fr = 30;                   // Frame rate to record at
int intro = 1;                 // Length of intro (seconds) (== background screen without agents)
int outro = 1;                 // Length of outro (seconds) (== background screen without agents)
int totalLength = 10;          // Length of recording
String folderAddress = "frames/build/"; // Address of folder where frames are saved
String fileName = "frames-";   // Name of PNG file
int digits = 4;                // Digits to add after the name

// END OF CUSTOMIZABLE

ArrayList<Agent> agents = new ArrayList<Agent>(maxAgents);
ArrayList<Pheromone> pheromones = new ArrayList<Pheromone>();

int age = 0;
int lowestAge = 0;

// Method spawn: method to spawn generation of agents given agent number, size, etc...
void spawn() {
  // Spawn new agents
  for (int i = 0; i < numAgents; i++) {
    agents.add(new Agent(canvas, collisionCenterDir, spawnCenterDir, correctAngle, speed, acc, spawn, detail, radius, agentSize, age, rgbA));
  }
  age++; // Increase age

  // Clean up agents
  if (agents.size() > maxAgents) {
    purge();
  }
}

// Method purge: method to purge a generation of agents
void purge() {
  // Backwards loop to prevent array resizing while looping through issue
  for (int j = agents.size() - 1; j >= 0; j--) {
    Agent a = agents.get(j);
    if (a.age == lowestAge) {
      agents.remove(j);
    }
  }
  lowestAge++;
}

// Simulation setup
void setup() {
  canvas = new Canvas(shape, pad);
  size(800, 800);
  noStroke();
  rectMode(CENTER);
  if (spawnAtInit) {
    spawn();
  }
  println("Welcome");
  printRules();
  printType(clickType);
  if (recording) {
    frameRate(fr);
  }
}

// draw method: draws iteratively
void draw() {
  if (recording && frameCount == int(intro * fr)) { // Spawn setup for recording
    spawn = "center";
    spawn();
  }
  if (frameCount%50 == 0) {
    println("Iter: " + frameCount);
  }
  // Background color
  background(rgbB[0], rgbB[1], rgbB[2]);

  // Move agents, Display agents & add pheromones
  for (int i = 0; i < agents.size(); i++) {
    pheromones.add(new Pheromone(agents.get(i).pos.copy(), pheroDecay, acc, rgbA, rgbP, agentSize));
    agents.get(i).update();
    agents.get(i).show();
  }

  // Display pheromones
  if (heads == true) { // Display "head" of agent on top of "tail"
    for (Pheromone p : pheromones) {
      p.show();
      p.decay();
    }
  }

  // Clean pheromones array
  for (int j = pheromones.size() - 1; j >= 0; j--) {
    Pheromone p = pheromones.get(j);
    if (heads == false) { // Display "tail" of agent on top of "head"
      p.show();
      p.decay();
    }

    // Remove pheromones under threshold
    if (p.strength <= pheroThreshold) {
      pheromones.remove(j);
    }
  }

  if (recording) {
    String a = folderAddress + fileName + nf(frameCount, digits) + ".png";
    saveFrame(a);

    if (frameCount > int(fr * (totalLength - outro))) { // Make outro: purge all agents
      agents = new ArrayList<Agent>();
    }
    if (frameCount > fr * totalLength) { // Exit after outro
      exit();
      println("Use this FFmpeg command to stitch frames together : ");
      String regex = "%0" + digits + "d";
      println("FFmpeg -framerate " + fr + " -i " + fileName + regex + ".png -c:v libx264 -pix_fmt yuv420p output.mp4");
    }
  }
}

void mouseClicked() {
  if (mouseButton != RIGHT) { // Left click
    if (clickType) { // Spawn
      println("Spawning");
      int x = mouseX;
      int y = mouseY;
      // Check if mouse is in corners, edges, or center
      boolean tl = (x >= 0 && x <= width / 3) && (y >= 0 && y <= height / 3);
      boolean tr = (x >= 2 * width / 3 && x <= width) && (y >= 0 && y <= height / 3);
      boolean bl = (x >= 0 && x <= width / 3) && (y >= 2 * height / 3 && y <= height);
      boolean br = (x >= 2 * width / 3 && x <= width) && (y >= 2 * height / 3 && y <= height);
      boolean corners = tl || tr || bl || br;
      // Stitch statements together
      boolean center = (x >= width / 3 && x <= 2 * width / 3) && (y >= width / 3 && y <= 2 * width / 3);
      if (corners) { // Set spawn accordingly
        spawn = "corners";
      } else if (center) {
        spawn = "center";
      } else {
        spawn = "edges";
      }
      spawn(); // Spawn
    } else { // Attract
      println("Attracting");
      for (Agent a : agents) { // Change agent angle to point at the point of click
        a.angle = atan2(a.pos.y - mouseY, a.pos.x - mouseX) + PI;
      }
    }
  } else { // Right click
    if (clickType) {
      println("Despawning");
      purge();
    } else { // Push away
      println("Repulsing");
      for (Agent a : agents) { // Change agent angle to point away from the point of click
        a.angle = atan2(a.pos.y - mouseY, a.pos.x - mouseX);
      }
    }
  }
}

void keyPressed() {
  if (keyCode == ENTER) { // Change click type
    clickType = !clickType;
    printType(clickType);
  } else if (keyCode == 32) { // Turn around
    println("Turning around");
    for (Agent a : agents) {
      a.angle += PI;
    }
    spawn = "random";
  } else if (keyCode == BACKSPACE) { // Purge all agents
    agents = new ArrayList<Agent>();
  } else if (key == 's') { // Spiral spawn
    spawn = "spiral";
    spawn();
  } else if (key == 'e') { // Edges spawn
    spawn = "edges";
    spawn();
  } else if (key == 'c') { // Corner spawn
    spawn = "corners";
    spawn();
  } else if (key == 'o') { // Center spawn
    spawn = "center";
    spawn();
  }
}

// Print info
void printType(boolean clickType) {
  print("Current click type: ");
  if (clickType) {
    println("SPAWN/DESPAWN");
  } else {
    println("ATTRACT/REPULSE");
  }
}

// Print info
void printRules() {
  println("Mode SPAWN/DESPAWN:");
  println("Left click: spawn " + numAgents + " agents (Limited to " + maxAgents + " at once)");
  println("Right click: delete oldest " + numAgents + " agents");
  println("");
  println("Mode ATTRACT/REPULSE");
  println("Left click: agents will change direction towards the point of click");
  println("Right click: agents will change direction away from the point of click");
  println("");
  println("Type ENTER to change click type");
  println("");
}
