class Pheromone {
  PVector pos;
  float strength;
  float decaySpeed;
  float acc;
  int[] rgbA;
  int[] rgbP;
  float size;
  
  // Create pheromone
  Pheromone(PVector pos, float pheroDecay, float acc, int[] rgbA, int[] rgbP, float size) {
    this.pos = pos;
    this.decaySpeed = pheroDecay;
    this.acc = acc;
    this.rgbA = rgbA;
    this.rgbP = rgbP;
    this.size = size;
    this.strength = 1;
  }
  
  void decay() { 
    // Decay pheromone strength
    this.strength += -decaySpeed;
  }
  
  void show() {
    // Create color objects
    color colourA = color(rgbA[0], rgbA[1], rgbA[2]);
    color colourP = color(rgbP[0], rgbP[1], rgbP[2]);
    
    // Get fade of objects given colors & strength
    color trailColor = lerpColor(colourA, colourP, 1 - strength);
    
    fill(trailColor);
    rect(pos.x, pos.y, size, size);
  }
}
