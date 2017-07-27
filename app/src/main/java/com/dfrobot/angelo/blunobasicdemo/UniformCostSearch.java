package com.dfrobot.angelo.blunobasicdemo;
import java.util.Comparator;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;
/**
 * Created by Anhnonymouz on 25/7/2017.
 */

public class UniformCostSearch
{
    private PriorityQueue<Node> priorityQueue;
    private Set<Integer> settled;
    private int distances[];
    private int numberOfNodes;
    private int adjacencyMatrix[][];
    private LinkedList<Integer> path;
    private int[] parent;
    private int source, destination;
    public static final int MAX_VALUE = 999;

    public UniformCostSearch(int numberOfNodes)
    {
        this.numberOfNodes = numberOfNodes;
        this.settled = new HashSet<Integer>();
        this.priorityQueue = new PriorityQueue<>(numberOfNodes, new Node());
        this.distances = new int[numberOfNodes + 1];
        this.path = new LinkedList<Integer>();
        this.adjacencyMatrix = new int[numberOfNodes + 1][numberOfNodes + 1];
        this.parent = new int[numberOfNodes + 1];
    }

    public int uniformCostSearch(int adjacencyMatrix[][], int source, int destination)
    {
        int evaluationNode;
        this.source = source;
        this.destination = destination;

        for (int i = 1; i <= numberOfNodes; i++)
        {
            distances[i] = MAX_VALUE;
        }

        for (int sourcevertex = 1; sourcevertex <= numberOfNodes; sourcevertex++)
        {
            for (int destinationvertex = 1; destinationvertex <= numberOfNodes; destinationvertex++)
            {
                this.adjacencyMatrix[sourcevertex][destinationvertex] =
                        adjacencyMatrix[sourcevertex][destinationvertex];
            }
        }

        priorityQueue.add(new Node(source, 0));
        distances[source] = 0;

        while (!priorityQueue.isEmpty())
        {
            evaluationNode = getNodeWithMinDistanceFromPriorityQueue();
            System.out.println("The eval Node is " + evaluationNode);
            if (evaluationNode == destination)
            {
                return distances[destination];
            }
            settled.add(evaluationNode);
            addFrontiersToQueue(evaluationNode);
        }
        return distances[destination];
    }

    private void addFrontiersToQueue(int evaluationNode)
    {
        for (int destination = 1; destination <= numberOfNodes; destination++)
        {
            if (!settled.contains(destination))
            {
                if (adjacencyMatrix[evaluationNode][destination] != MAX_VALUE)
                {
                    if (distances[destination] > adjacencyMatrix[evaluationNode][destination]
                            + distances[evaluationNode])
                    {
                        distances[destination] = adjacencyMatrix[evaluationNode][destination]
                                + distances[evaluationNode];
                        parent[destination] = evaluationNode;
                    }

                    Node node = new Node(destination, distances[destination]);
                    if (priorityQueue.contains(node))
                    {
                        priorityQueue.remove(node);
                    }
                    priorityQueue.add(node);
                }
            }
        }
    }

    private int getNodeWithMinDistanceFromPriorityQueue()
    {
        Node node = priorityQueue.remove();
        return node.node;
    }

    public void printPath()
    {
        path.add(destination);
        boolean found = false;
        int vertex = destination;
        while (!found)
        {
            if (vertex == source)
            {
                found = true;
                continue;
            }
            path.add(parent[vertex]);
            vertex = parent[vertex];
        }

        System.out.println("The Path between " + source + " and " + destination+ " is ");
        Iterator<Integer> iterator = path.descendingIterator();
        while (iterator.hasNext())
        {
            System.out.print(iterator.next() + "\t");
        }
    }

    public String getPath() {
        path.add(destination);
        boolean found = false;
        int vertex = destination;
        while (!found)
        {
            if (vertex == source)
            {
                found = true;
                continue;
            }
            path.add(parent[vertex]);
            vertex = parent[vertex];
        }

        System.out.println("The Path between " + source + " and " + destination+ " is ");
        Iterator<Integer> iterator = path.descendingIterator();
        String route = "";
        while (iterator.hasNext())
        {
            route += iterator.next() + " ";
        }
        return route;
    }

//    public static void main(String... arg)
//    {
//        int adjacency_matrix[][];
//        int number_of_vertices;
//        int source = 0;
//        int destination = 0;
//        int distance;
//        Scanner scan = new Scanner(System.in);
//        try
//        {
//            System.out.println("Enter the number of vertices");
//            number_of_vertices = scan.nextInt();
//
//            adjacency_matrix = new int[number_of_vertices + 1][number_of_vertices + 1];
//            System.out.println("Enter the Weighted Matrix for the graph");
//            for (int i = 1; i <= number_of_vertices; i++)
//            {
//                for (int j = 1; j <= number_of_vertices; j++)
//                {
//                    adjacency_matrix[i][j] = scan.nextInt();
//                    if (i == j)
//                    {
//                        adjacency_matrix[i][j] = 0;
//                        continue;
//                    }
//                    if (adjacency_matrix[i][j] == 0)
//                    {
//                        adjacency_matrix[i][j] = MAX_VALUE;
//                    }
//                }
//            }
//
//            System.out.println("Enter the source ");
//            source = scan.nextInt();
//
//            System.out.println("Enter the destination");
//            destination = scan.nextInt();
//
//            UniformCostSearch uniformCostSearch = new UniformCostSearch(number_of_vertices);
//            distance = uniformCostSearch.uniformCostSearch(adjacency_matrix,source, destination);
//            uniformCostSearch.printPath();
//
//            System.out.println("\nThe Distance between source " + source +
//                    " and destination " + destination + " is " + distance);
//
//        } catch (InputMismatchException inputMismatch)
//        {
//            System.out.println("Wrong Input Format");
//        }
//        scan.close();
//    }
}

class Node implements Comparator<Node>
{
    public int node;
    public int cost;

    public Node()
    {

    }

    public Node(int node, int cost)
    {
        this.node = node;
        this.cost = cost;
    }

    @Override
    public int compare(Node node1, Node node2)
    {
        if (node1.cost < node2.cost)
            return -1;
        if (node1.cost > node2.cost)
            return 1;
        if (node1.node < node2.node)
            return -1;
        return 0;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Node)
        {
            Node node = (Node) obj;
            if (this.node == node.node)
            {
                return true;
            }
        }
        return false;
    }
}
