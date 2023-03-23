package com.katiekilroy.myapplication.shortestpath;

public class Edge {
    private int DesinationVertexID;
    private int weight;
    private int direction;
    private String description;

    
  public Edge() {}

    public Edge(int destVID, int w, int dir, String desp) {
        this.DesinationVertexID = destVID;
        this.description = desp;
        this.direction = dir;
        this.weight = w;
    }

    public void setEdgeValues(int destVID, int w){

      this.DesinationVertexID = destVID;
      this.weight = w;
    }

    public void setWeight(int w){ this.weight = w; }

    public void setDirection(int dir){this.direction = dir; }

    public void setDescription(String desp){this.description = desp; }

    public int getDirection(){return direction; }

    public String getDescription(){return description; }

    public int getDestinationVertexID(){ return DesinationVertexID; }

    public int getWeight(){ return weight; };
};