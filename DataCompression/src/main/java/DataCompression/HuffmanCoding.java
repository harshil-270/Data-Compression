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
import java.nio.charset.StandardCharsets;

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
    static String filePath;

    HuffmanCoding(String filePath) {
        this.filePath = filePath;
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
    }

    public static void run() {
        // Read, and count frequency
        char[] charArray = new char[1000];
        int[] charFreq = new int[1000];
        File myObj = new File(filePath);
        String fileData = "";
        try {
            StringBuilder sb = new StringBuilder();
            File file = new File(filePath);
            //Scan the data from file and store it in string.
            try (BufferedReader myReader = new BufferedReader(new FileReader(file))){
                String line = myReader.readLine();
                while (line != null){
                    sb.append(line + "\n");
                    line = myReader.readLine();
                }
            }
            catch (IOException e){
                System.out.println(e);
            }
            fileData = sb.toString();
        } catch (Exception e) {
            System.out.println(e);
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
        int noOfBits = 7;
        String ansciiCode = "";
        int extraData = 0;
        for (int i = 0; i < binaryCode.length(); i += noOfBits) {
            int ansciiChar = 0, cnt = 0;
            for (int j = 0; j < noOfBits && i + j < binaryCode.length(); j++) {
                if (binaryCode.charAt(i + j) == '1')
                    ansciiChar += Math.pow(2, 6 - j);
                cnt++;
            }
            extraData = 7 - cnt;
            ansciiCode += ((char) ansciiChar);
        }
        // OUTPUT IN FILE
        try {
            //Store huffman tree and compressed data into the file.
            String treeMain = tree.toString();
            String treeSize = String.format("%05d", treeMain.length()) ;
            String dataSize = String.format("%05d", ansciiCode.length()) ;
            String extraSize = String.format("%d", extraData);
            byte[] b = extraSize.getBytes(StandardCharsets.UTF_8);
            
            String s = treeSize + dataSize + treeMain + ansciiCode + b[0] ;
            
            String fileSavePath = filePath.substring(0, filePath.lastIndexOf('\\') + 1) + "Compress.txt";
            File file = new File(fileSavePath);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(s);
            }
        } catch (Exception e) {
            System.out.println("An error occurred. Error : " + e);
        }
    }
}
