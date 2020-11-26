/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Het
 */
package DataCompression;
import java.util.*;
import java.io.*;

class Node {
    int data;
    char c;
    Node left;// left node tree
    Node right;// right node tree

    public Node(char c, int data) {
        this.data = data;
        this.c = c;
        this.left = null;
        this.right = null;
    }
}

class myComparator implements Comparator<Node> {
    public int compare(Node x, Node y) {
        return x.data - y.data;
    }
}

public class HuffmanCoding {
    static String filename;

    HuffmanCoding(String filename) {
        this.filename = filename;
        run();
    }
    public static void buildTree(Node root, String s, TreeMap<Character, String> table, StringBuilder tree) {
        if (root.left == null && root.right == null) {
            // Code of Each CHar
            // System.out.println(root.c + ":" + s);
            tree.append("1" + root.c);
            table.put(root.c, s);
            return;
        }
        tree.append("0");
        buildTree(root.left, s + "0", table, tree);
        buildTree(root.right, s + "1", table, tree);
        
        return;
    }

    public static void run() {
        // Read, and count frequency
        char[] charArray = new char[1000];
        int[] charFreq = new int[1000];
        File myObj = new File(filename);
        String fileData = "";
        try {
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                fileData += myReader.nextLine();
                fileData += "\n";
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        int mainSize = fileData.length();

        char[] stringToCharArray = fileData.toCharArray();
        int size = 0;
        for (int i = 0; i < mainSize; i++) {
            int flag = 0;
            for (int j = 0; j < size; j++) {
                if (charArray[j] == stringToCharArray[i]) {
                    charFreq[j]++;
                    flag = 1;
                    break;
                }
            }
            if (flag == 0) {
                charArray[size] = stringToCharArray[i];
                charFreq[size] = 1;
                size++;
            }
        }

        // Check Frequency of Each Char for (int i = 0; i < size; i++) {
        // System.out.println(charArray[i] + " " + charFreq[i]); }

        // Algorithm
        PriorityQueue<Node> sortedTable = new PriorityQueue<Node>(size, new myComparator());
        for (int i = 0; i < size; i++) {
            Node h = new Node(charArray[i], charFreq[i]);
            sortedTable.add(h);
        }
        Node root = null;
        while (sortedTable.size() > 1) {
            Node frontNode1 = sortedTable.peek();
            sortedTable.poll();
            Node frontNode2 = sortedTable.peek();
            sortedTable.poll();
            Node replace = new Node('-', frontNode1.data + frontNode2.data);
            replace.left = frontNode1;
            replace.right = frontNode2;
            root = replace;
            sortedTable.add(replace);
        }

        // tree build
        TreeMap<Character, String> table = new TreeMap<Character, String>();
        StringBuilder tree = new StringBuilder();

        buildTree(root, "", table, tree);

        // Check Tree
        // System.out.println(tree);

        // String To Binary Code String
        String binaryCode = "";
        for (int i = 0; i < fileData.length(); i++) {
            binaryCode += table.get(stringToCharArray[i]);
        }

        // Binary Code to Anscii Code
        char[] charBinaryCode = binaryCode.toCharArray();
        int noOfBits = 7;
        String ansciiCode = "";
        int extraData = 0;
        for (int i = 0; i < charBinaryCode.length; i += noOfBits) {
            int ansciiChar = 0, cnt = 0;
            for (int j = 0; j < noOfBits && i + j < charBinaryCode.length; j++) {
                if (charBinaryCode[i + j] == '1')
                    ansciiChar += Math.pow(2, 6 - j);
                cnt++;
            }
            extraData = 7 - cnt;
            ansciiCode += ((char) ansciiChar);
        }
        // OUTPUT IN FILE
        try {
            File compress = new File("Compress.txt");
            compress.createNewFile();
            FileWriter myWriter = new FileWriter(compress);
            String treeMain = tree.toString();
            
            String treeSize = String.format("%05d", treeMain.length()) ;
            myWriter.write(treeSize);
            String dataSize = String.format("%05d", ansciiCode.length()) ;
            myWriter.write(dataSize);
            
            // tree print
            myWriter.write(treeMain);
            // anscii code print
            myWriter.write(ansciiCode);
            myWriter.write(extraData);
            myWriter.close();

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
