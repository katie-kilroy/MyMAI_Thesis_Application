package com.katiekilroy.myapplication.shortestpath;

import java.util.ArrayList;
import java.util.List;
import java.lang.String;

public class Vertex {
  
  private String state_name;
  int state_id;

  private boolean visited;

  Edge Edge = new Edge();


  List<Edge> edgeList = new ArrayList<Edge>();

  public Vertex() {
    this.state_id = 0;
    this.state_name = "";
  }

  public  Vertex(int id, String sname) {
    this.state_id = id;
    this.state_name = sname;
  }

  public void setID(int id) {
    this.state_id = id;
  }

  public void setStateName(String sname) {
    this.state_name = sname;
  }

  public int getStateID() {
    return state_id;
  }

  public String getStateName() {
    return state_name;
  }

  public List<Edge> getEdgeList() { return edgeList; }

  public void printEdgeList() {
    System.out.print("[");
    for (Edge edge : edgeList) {
            System.out.print(edge.getDestinationVertexID() + "(" + edge.getWeight() + ","
           + edge.getDirection() + ") -->");
    }
    System.out.println("]");
  }
}
;