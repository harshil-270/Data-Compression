package DataCompression;

import java.io.*;

class Node2 {
    char c;
    Node2 left;// left node tree
    Node2 right;// right node tree

    public Node2(char c) {
        this.c = c;
        this.left = null;
        this.right = null;
    }
}   

public class DecompressData {
    static String filePath;
    
    static int index = 0, cnt = 0;
    
    DecompressData(String filePath) {
        this.filePath = filePath;    
        run() ;
    }
    
    public static Node2 restoreTree(String asciiTree) {
        //Restore the Original tree from stored data.
        if(index >= asciiTree.length()) {
            return null;
        }
        // if character is 1 that means we reched the leaf node.
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
            // Scan the data from compressed file.
            StringBuilder sb = new StringBuilder();
            File filename1 = new File(filePath);
            try (BufferedReader myReader = new BufferedReader(new FileReader(filename1))){
                //In compressed file first we have stored size of tree's string length and data's string length.
                for(int i = 0, power = 10000; i < 5; i++,power /= 10) {
                    treeSize += (myReader.read() - 48) * power;
                }
                for(int i = 0, power = 10000; i < 5; i++,power /= 10) {
                    dataSize += (myReader.read() - 48) * power;
                }
                sb.append(String.format("%05d", treeSize) + String.format("%05d", dataSize));
                for(int i = 10; i < 10 + treeSize + dataSize + 2; i++){
                    int c = myReader.read();
                    if(c == -1)c = 26;
                    sb.append((char)c);
                }
            }
            catch (IOException e){
                System.out.println(e);
            }
            encodedTreeAndData = sb.toString();         
        } catch (Exception e) {
            System.out.println("An error occurred. Error : " + e);
        }
        //store tree and data in seprate strings.
        String asciiTree = encodedTreeAndData.substring(10, 10 + treeSize) ;
        String asciiData = encodedTreeAndData.substring(10 + treeSize, 10 + treeSize + dataSize) ;
        String dataInBin = "";
        int totalLength = encodedTreeAndData.length();
        //extrabytes in the ending word.
        int extraData = Integer.parseInt( encodedTreeAndData.substring(totalLength - 2, totalLength) ) - 48 ;
        
        //Build tree.
        Node2 root = restoreTree(asciiTree);
        
        //Convert data's string into binary string.
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
            String fileSavePath = filePath.substring(0, filePath.lastIndexOf('\\') + 1) + "Decompress.txt";  
            File file = new File(fileSavePath);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                //Npw using tree and compressed data in binary form restore the original data.
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
                    writer.write(cur.c);
                }
                writer.close();
            }
        } catch (IOException e) {
            System.out.println("An error occurred. Error : " + e);
        }
    }
}
