package com.github.ilms49898723.fluigi.device.graph;

public class GraphEdge implements Comparable<GraphEdge> {
    private String mVertexA;
    private String mVertexB;
    private int mWeight;

    public GraphEdge(String vertexA, String vertexB, int weight) {
        mVertexA = vertexA;
        mVertexB = vertexB;
        mWeight = weight;
    }

    public void increaseWeight() {
        mWeight += 1;
    }

    public String getVertexA() {
        return mVertexA;
    }

    public String getVertexB() {
        return mVertexB;
    }

    public int getWeight() {
        return mWeight;
    }

    @Override
    public int compareTo(GraphEdge o) {
        return Integer.compare(mWeight, o.mWeight);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GraphEdge graphEdge = (GraphEdge) o;

        if (getWeight() != graphEdge.getWeight()) {
            return false;
        }
        if (getVertexA() != null ? !getVertexA().equals(graphEdge.getVertexA()) : graphEdge.getVertexA() != null) {
            return false;
        }
        return getVertexB() != null ? getVertexB().equals(graphEdge.getVertexB()) : graphEdge.getVertexB() == null;
    }

    @Override
    public int hashCode() {
        int result = getVertexA() != null ? getVertexA().hashCode() : 0;
        result = 31 * result + (getVertexB() != null ? getVertexB().hashCode() : 0);
        result = 31 * result + getWeight();
        return result;
    }
}
