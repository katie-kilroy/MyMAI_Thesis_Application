package com.katiekilroy.myapplication.shortestpath;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class Graph {

  String jsonData = "your JSON data";
  Gson gson = new Gson();
//  Map<String, Object> map = gson.fromJson(jsonData, new TypeToken<Map<String, Object>>(){}.getType());

  FirebaseDatabase database = FirebaseDatabase.getInstance();
  DatabaseReference db = database.getReference("Map");

  Vertex Vertex = new Vertex();
  Edge Edge = new Edge();

  List<Vertex> vertices = new ArrayList<Vertex>();

  public Vertex getVertexByID(int vid) {
    Vertex temp = new Vertex();
    for (int i = 0; i < vertices.size(); i++) {
      temp = vertices.get(i);
      if (temp.getStateID() == vid) {
        return temp;
      }
    }
    return temp;
  }

  public List<Vertex> getVertices(){
    return vertices;
  }
  public boolean checkVertices(){

    Log.i("Vertices", "checking list");
    if (vertices.isEmpty()){
      return false;
    }
    else{
      return true;
    }
  }

  public void addVertex(Vertex newVertex) {
    boolean check = checkIfVertexExistByID(newVertex.getStateID());
    if (check) {
      System.out.println("Vertex with this ID already exists.");
    } else
      vertices.add(newVertex);

    System.out.println("New Vertex added succesfully\n");
  }

  public boolean checkIfVertexExistByID(int vid) {
    boolean flag = false;
    if (vertices == null) {
      System.out.println("vertices is null");
      return false;
    }


    for (int i = 0; i < vertices.size(); i++) {

      if (vertices.get(i).getStateID() == vid) {
        return true;
      }
    }
    return flag;
  }

  public boolean checkIfEdgeExistByID(int fromVertex, int toVertex) {
    Vertex v = getVertexByID(fromVertex);
    List<Edge> e;
    e = v.getEdgeList();
    boolean flag = false;
    for (Edge edge : e) {
      if (edge.getDestinationVertexID() == toVertex) {
        flag = true;
        return flag;
      }
    }
    return flag;
  }

  public void addEdgeByID(int newVertex1, int newVertex2, String V1name, String V2name, int weight, int direction, String description) {

    Log.i("Edge", "Adding Edge");

    Vertex vertex1 = new Vertex();
    Vertex vertex2 = new Vertex();

    vertex1.setID(newVertex1);
    vertex1.setStateName(V1name);
    vertex2.setID(newVertex2);
    vertex2.setStateName(V2name);

    boolean check1 = checkIfVertexExistByID(newVertex1);
    boolean check2 = checkIfVertexExistByID(newVertex2);

    if (!check1) {
      vertices.add(vertex1);
      check1 = true;
    }
    if (!check2) {
      vertices.add(vertex2);
      check2 = true;
    }

    if (check1 && check2) {
      boolean check3 = checkIfEdgeExistByID(newVertex1, newVertex2);
      if (check3) {
        System.out.println("Edge between " + getVertexByID(newVertex1).getStateName()
            + " (" + newVertex1 + ") and "
            + getVertexByID(newVertex2).getStateName() + " (" + newVertex2
            + ") already exits.\n");
      } else {
        for (int i = 0; i < vertices.size(); i++) {
          if (vertices.get(i).getStateID() == newVertex1) {
            Edge e = new Edge(newVertex2, weight, direction, description);
            // Create a new child node under the vertex with a unique key using push()
//            DatabaseReference edgeRef = db.child(String.valueOf(vertices.get(i).getStateID())).child("Edges").push();
//            // Set the value of the child node to the Edge object using setValue()
//            edgeRef.setValue(e);
            vertices.get(i).edgeList.add(e);
          } else if (vertices.get(i).getStateID() == newVertex2) {
            Edge e = new Edge(newVertex1, weight, -direction, description);
            // Create a new child node under the vertex with a unique key using push()
//            DatabaseReference edgeRef = db.child(String.valueOf(vertices.get(i).getStateID())).child("Edges").push();
//            // Set the value of the child node to the Edge object using setValue()
//            edgeRef.setValue(e);
            vertices.get(i).edgeList.add(e);
          }
        }

        System.out.println("Edge between " + getVertexByID(newVertex1).getStateName()
            + " (" + newVertex1 + ") and "
            + getVertexByID(newVertex2).getStateName() + " (" + newVertex2
            + ") has been added.");
      }
    }
     else {
      System.out.println("Invalid Vertex ID entered.");
     }
  }

  public void updateEdgeByID(int fromVertex, int toVertex, int newWeight, int newDirection, String newDescription) {
    boolean check = checkIfEdgeExistByID(fromVertex, toVertex);
    if (check) {
      for (Vertex vertex : vertices) {
        if (vertex.getStateID() == fromVertex) {
          for (Edge edge : vertex.getEdgeList()) {
            if (edge.getDestinationVertexID() == toVertex) { /////////////////////////////////// NEED TO ADD DATABASE TO THIS ///////
              edge.setWeight(newWeight);
              edge.setDirection(newDirection);
              edge.setDescription(newDescription);
              // Create a new child node under the vertex with a unique key using push()
              DatabaseReference edgeRef = database.getReference("Maps/" + fromVertex + "/Edges/" + toVertex);
              // Set the value of the child node to the Edge object using setValue()
              edgeRef.updateChildren((Map<String, Object>) edge);
              break;
            }
          }
        } else if (vertex.getStateID() == toVertex) {
          for (Edge edge : vertex.getEdgeList()) {
            if (edge.getDestinationVertexID() == fromVertex) {
              edge.setWeight(newWeight);
              edge.setDirection(newDirection);
              edge.setDescription(newDescription);

              // Create a new child node under the vertex with a unique key using push()
              DatabaseReference edgeRef = database.getReference("Maps/" + toVertex + "/Edges/" + fromVertex);
              // Set the value of the child node to the Edge object using setValue()
              edgeRef.updateChildren((Map<String, Object>) edge);
              break;
            }
          }
        }
      }
      System.out.println("Edge updated successfully. \n\n");
    } else {
      System.out.println("The edge betweem " + getVertexByID(fromVertex).getStateName()
          + "(" + fromVertex + ")"
          + " and " + getVertexByID(toVertex).getStateName() + "(" + toVertex
          + ") DOES NOT EXIST\n\n");
    }
  }

  public void deleteEdgeByID(int fromVertex, int toVertex) {
    boolean check = checkIfEdgeExistByID(fromVertex, toVertex);
    if (check) {
      for (int i = 0; i < vertices.size(); i++) {
        if (vertices.get(i).getStateID() == fromVertex) {
          for (Iterator<Edge> it = vertices.get(i).getEdgeList().iterator(); it.hasNext();) {
            Edge edge = it.next();
            if (edge.getDestinationVertexID() == toVertex) {
              it.remove();
              break;
            }
          }
        } else if (vertices.get(i).getStateID() == toVertex) {
          for (Iterator<Edge> it = vertices.get(i).getEdgeList().iterator(); it.hasNext();) {
            Edge edge = it.next();
            if (edge.getDestinationVertexID() == fromVertex) {
              it.remove();
              break;
            }
          }
        }
      }
      System.out.println("The edge betweem " + getVertexByID(fromVertex).getStateName()
          + "(" + fromVertex + ")"
          + " and " + getVertexByID(toVertex).getStateName() + "(" + toVertex
          + ") has been deleted.\n\n");
    } else {
      System.out.println("The edge betweem " + getVertexByID(fromVertex).getStateName()
          + "(" + fromVertex + ")"
          + " and " + getVertexByID(toVertex).getStateName() + "(" + toVertex
          + ") DOES NOT EXIST\n\n");
    }
  }

  public void deleteVertexByID(int vid) {
    int vIndex = -1;
    for (int i = 0; i < vertices.size(); i++) {
      if (vertices.get(i).getStateID() == vid) {
        vIndex = i;
      }
    }
    if (vIndex != -1) {
      List<Edge> edgeList = vertices.get(vIndex).getEdgeList();
      for (Iterator<Edge> it = edgeList.iterator(); it.hasNext();) {
        Edge edge = it.next();
        deleteEdgeByID(edge.getDestinationVertexID(), vid);
      }
      vertices.remove(vIndex);
      System.out.println("Vertex Successfully deleted. \n\n");
    }
  }

  public void updateVertex(int VID, String vname) {
    boolean check = checkIfVertexExistByID(VID);
    if (check) {
      for (int i = 0; i < vertices.size(); i++) {
        if (vertices.get(i).getStateID() == VID) {
          vertices.get(i).setStateName(vname);
          break;
        }
      }
      System.out.println("Vertex name Upadeted Suddceefully \n\n");
    }
  }

  public void findShortestEdge(int vid) {
    boolean check = checkIfVertexExistByID(vid);
    Vertex v = getVertexByID(vid);
    List<Edge> e;
    e = v.getEdgeList();
    if (check) {
      Iterator<Edge> in = vertices.get(vid).edgeList.iterator();
      int min = in.next().getWeight();

      for (Iterator<Edge> it = e.iterator(); it.hasNext();) {
        Edge edge = it.next();
        int temp = edge.getWeight();
        System.out.println(edge.getDestinationVertexID() + "(" + edge.getWeight() + ") -->");
        if (temp < min) {
          min = temp;
        }
      }
      System.out.println("min path is " + min);
    }
  }

  // function to print the shortest path from start node to end node
  public List<Integer> shortestPath(int start, int end) {
    // initialize distances array and set all distances to infinity
    int V = vertices.size();

    Log.i("Vertices array", "vertices" + V);
    Vertex v1 = getVertexByID(start);
    List<Edge> e;
    List<Pair<Integer, Integer>> dist = new ArrayList<Pair<Integer, Integer>>(); // First int is dist, second is the
                                                                                 // previous node.

    for (int i = 0; i <= V; i++) {
      dist.add(new Pair<Integer, Integer>(
          1000000007, i)); // Define "infinity" as necessary by constraints.
    }

    // initialize priority queue to store nodes to visit
    PriorityQueue<Pair<Integer, Integer>> pq = null;
    pq = new PriorityQueue<Pair<Integer, Integer>>(
        new Comparator<Pair<Integer, Integer>>() {
          public int compare(Pair<Integer, Integer> p1, Pair<Integer, Integer> p2) {
            return p1.getKey() - p2.getKey();
          }
        });

    // insert start node with distance 0 into priority queue
    // set distance of start node to 0
    pq.offer(new Pair<Integer, Integer>(start, 0));
    Log.i("Distance array", "start: " + start);
    dist.set(start, new Pair<Integer, Integer>(0, start));
    Log.i("Distance array", "start: " + start);

    int u = pq.peek().getKey();
    // loop until priority queue is empty
    while ((!pq.isEmpty()) && (u <= V)) {
      // get node with smallest distance from priority queue
      u = pq.poll().getKey();
      v1 = getVertexByID(u);
      e = v1.getEdgeList();
      for (com.katiekilroy.myapplication.shortestpath.Edge edge : e) {
        int v2 = edge.getDestinationVertexID();
        int direction;
        int weight = edge.getWeight();
        if (edge.getDirection() <= 0) {
          direction = edge.getDirection();
          direction = -direction;
        } else {
          direction = edge.getDirection();
        }
        weight = weight + direction;

        // if new distance to node is shorter than current distance, update
        // distance and previous node
        if (dist.get(v2).getKey() > dist.get(u).getKey() + weight) {
          // Update the distance of v.
          dist.set(v2, new Pair<Integer, Integer>(dist.get(u).getKey() + weight, u));

          Log.i("Distance array", "v2: " + v2);
          // Update the previous node of v.
          // dist[v2].second = u;
          // Insert v into the pq.

          System.out.println("Node " + v2 + "   Weight: " + dist.get(v2).getKey());
          pq.offer(new Pair<Integer, Integer>(v2, dist.get(v2).getKey()));
        }
      }
    }
    List<Integer> path = new ArrayList<Integer>();
    System.out.println("The distance from node " + start + " to node " + end
        + " is: " + dist.get(end).getKey());

    Log.i("Distance array", "size: " + dist.size());

    int current = dist.get(end).getValue();
    

    path.add(end);
    path.add(current);

    while ((current != start))
    {
      current = dist.get(current).getValue();
      path.add(current);
    }

    Collections.reverse(path);

    System.out.println("The shortest path taken was: ");
    for (Iterator<Integer> it = path.iterator(); it.hasNext();) {
      Integer node = it.next();
      System.out.print(" --> " + node);
    }
    System.out.println();

    return path;
  }



  public void printGraph() {
    for (int i = 0; i < vertices.size(); i++) {
      Vertex temp;
      String gr;
      temp = vertices.get(i);
      gr = temp.getStateName() + "(" + temp.getStateID() + ") -->";

      Log.i("Print Graph", temp.getStateName() + "(" + temp.getStateID() + ") -->");
      System.out.print(gr);
      temp.printEdgeList();
    }
  }

}
