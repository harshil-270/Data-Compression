package DataCompression;

import java.io.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

class Node2 {
    int data;
    char c;
    Node2 left;// left node tree
    Node2 right;// right node tree

    public Node2(char c) {
        this.data = data;
        this.c = c;
        this.left = null;
        this.right = null;
    }
}   

public class DecompressData {
    String status = "production";
    static String filePath;
    
    static int index = 0, cnt = 0;
    
    DecompressData(String filePath) {
        if(status.equals("production")) {
            this.filePath = "Compress.txt";
        } else {
            this.filePath = filePath;
        }
        run() ;
    }
    
    public static Node2 restoreTree(String asciiTree) {
        
        if(index >= asciiTree.length()) {
            return null;
        }
        if(asciiTree.charAt(index) == '1') {
            index++;
            Node2 leaf = new Node2(asciiTree.charAt(index));
            index++;
            return leaf;
        } else {
            index++;
            Node2 left = restoreTree(asciiTree); 
            Node2 right = restoreTree(asciiTree);
            Node2 par = new Node2('?') ;
            par.left = left;
            par.right = right;
            return par;
        }
    }
    
    
    
    public static void run() {
        String encodedTreeAndData = "";
        int treeSize = 0, dataSize = 0;
        try {
            FileReader myReader = new FileReader(filePath);
            for(int i = 0, power = 10000; i < 5; i++,power /= 10) {
                treeSize += (myReader.read() - 48) * power;
            }
            for(int i = 0, power = 10000; i < 5; i++,power /= 10) {
                dataSize += (myReader.read() - 48) * power;
            }
            encodedTreeAndData = String.format("%05d", treeSize) + String.format("%05d", dataSize);
            for(int i = 10; i < 10 + treeSize + dataSize + 1; i++){
                int c = myReader.read();
                if(c == -1)c = 26;
                encodedTreeAndData += ((char)c);
            }
            myReader.close();
            
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
        } catch (IOException ex) {
            System.out.println("An error occurred.");
        }
        
        String asciiTree = encodedTreeAndData.substring(10, 10 + treeSize) ;
        String asciiData = encodedTreeAndData.substring(10 + treeSize, 10 + treeSize + dataSize) ;
        String dataInBin = "";
        int extraData = (int)(encodedTreeAndData.charAt(encodedTreeAndData.length() - 1));
        
        Node2 root = restoreTree(asciiTree);
        
        int noOfBits = 7;
        for(int i = 0; i < asciiData.length(); i++) {
            String bin = "";
            int num = (int)asciiData.charAt(i);
            for(int j = 0; j < noOfBits; j++) {
                bin = ((num % 2 == 0) ? "0" : "1") + bin;
                num /= 2;
            }
            dataInBin += bin;
        }
        dataInBin = dataInBin.substring(0, dataInBin.length() - extraData);
        
        try {
            File compress = new File("Decompress.txt");
            compress.createNewFile();
            FileWriter myWriter = new FileWriter(compress);
        
            for(int i = 0; i < dataInBin.length();) {
                Node2 cur = root;
                while(cur.left != null && cur.right != null && i < dataInBin.length()) {
                    if(dataInBin.charAt(i) == '1'){
                        cur = cur.right;
                    } else {
                        cur = cur.left;
                    }
                    i++;
                }
                myWriter.write(cur.c);
            }
            myWriter.close();

        } catch (IOException e) {
            System.out.println("An error occurred.");
        }
    }
}
