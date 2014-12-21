package hmmalgo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

class BinaryTreeNode implements Comparable<BinaryTreeNode>{
	public String word;
	public int noOfOccurences;
	public BinaryTreeNode leftNode;
	public BinaryTreeNode rightNode;
	
	public BinaryTreeNode(String key, int cnt){
		this.word = key;
		this.noOfOccurences = cnt;
	}
	
	public int compareTo(BinaryTreeNode obj)
	{
		BinaryTreeNode tmp = (BinaryTreeNode)obj;
		if(this.noOfOccurences < tmp.noOfOccurences){
			/* instance lt received */
			return -1;
		} 
		else if(this.noOfOccurences > tmp.noOfOccurences){
			/* instance gt received */ 
			return 1;
		}
		/* instance == received */
		return 0; 
	}
	
}


public class BinaryTreeExp {
	
	private BinaryTreeNode root;
	
	
	public BinaryTreeExp(){
		root = null;
	}
	
	private static boolean isKatakana(char c) {
		//return (isHalfwidthKatakana(c) || isFullwidthKatakana(c));
		return (isFullwidthKatakana(c));
	}
	private static boolean isFullwidthKatakana(char c) {
		if (!(('\u30a1' <= c) && (c <= '\u30fe')))
        {
            return false;
        }
		return true;
	}


	private static boolean isHiragana(char c) {
		if (!(('\u3041' <= c) && (c <= '\u309e')))
        {
            return false;
        }
		return true;
	}

	//Aim: To check if the character from the tanaka coprus is a kanji character or not
	//Input: single character from the file
	//Output: boolean value if is kanji or not
	private static boolean isKanji(char c) {
		Boolean charIsKanji = false;
		if (('\u4e00' <= c) && (c <= '\u9fa5'))
        {
            charIsKanji = true;
        }
        if (('\u3005' <= c) && (c <= '\u3007'))
        {
            charIsKanji = true;
        }
        if (!charIsKanji)
        {
            return false;
        }
		
		return charIsKanji;
	}

	
	public BinaryTreeNode insertElements(){
//		root = new BinaryTreeNode("hello", 1);
//		insert(root, "sue");
//		insert(root, "apple");
//		insert(root, "grapes");
//		insert(root, "apple");
//		insert(root, "orange");
//		insert(root, "grapes");
//		insert(root, "hey");
//		insert(root, "apple");
//		insert(root, "orange");
//		insert(root, "hello");
//		insert(root, "he");
//		return root;
		
		File fileIn  = new File("TanakaCorpus-JP.txt");
		String line = null;
		boolean isFirstEntry = true;
		
		try {
			Scanner scanner = new Scanner(fileIn);			
			while ((scanner.hasNextLine())){
				line = scanner.nextLine();
				
				for(int i = 0; i < line.length(); i++){
					if(i+3 != line.length()){
						String str = line.substring(i, i+3);
						String word = new String();
						//System.out.println("str => " + str);
						if(isKanji(str.charAt(0)) || isHiragana(str.charAt(0)) || isKatakana(str.charAt(0))){//check if 1st character is valid japanese character
							if(isKanji(str.charAt(1)) || isHiragana(str.charAt(1)) || isKatakana(str.charAt(1))){//check if 2nd character is also valid japanese character
								if(!isKanji(str.charAt(2)) && !isHiragana(str.charAt(2)) && !isKatakana(str.charAt(2))){
									word = str.substring(0, 2).concat("EOW");
									if(isFirstEntry == true){
										root = new BinaryTreeNode(word, 1);	
										isFirstEntry = false;
									}else{
										insert(root, word);	
									}
								}else{
									if(isFirstEntry == true){
										root = new BinaryTreeNode(str, 1);	
										isFirstEntry = false;
									}else{
										insert(root, str);	
									}
								}
							}
						}
					}else{
						break;
					}
				}
			}
			scanner.close();
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return root;
	}
	
	public void insert(BinaryTreeNode root, String key){
		
		//before inserting first search for the word in the binary tree
		//if exists increment its noOfOccurences
		//else insert to left or right		
		BinaryTreeNode keyFound = search(root, key);
		//System.out.println("key => " + key + " keyFound => " + keyFound);
		
		if(keyFound != null){
			keyFound.noOfOccurences++;
		}else{
			if(key.compareTo(root.word) < 0){
				if(root.leftNode != null){
					insert(root.leftNode, key);
				}else{
					root.leftNode = new BinaryTreeNode(key, 1);
				}
			}else{
				if(root.rightNode != null){
					insert(root.rightNode, key);
				}else{
					root.rightNode = new BinaryTreeNode(key, 1);
				}
			}
		}
		
	}
	
	public BinaryTreeNode search(BinaryTreeNode root, String key){
		BinaryTreeNode newNode = null;
		//boolean dataFound = false;
		if(root == null){
			//return dataFound;
			return null;
		}else{
			if(key.equalsIgnoreCase(root.word)){
				//dataFound = true;
				//return dataFound;
				newNode = root;
				return newNode;
			}else{
				if(key.compareTo(root.word) < 0){
					return (search(root.leftNode, key));
				}else{
					return (search(root.rightNode, key));
				}
			}
		}
	}

	public void display(BinaryTreeNode root){
		if(root != null){
			display(root.leftNode);
			System.out.println(root.word + " " + root.noOfOccurences);
			display(root.rightNode);
		}
	}
	
	public static void main(String args[]){
		//create and add elements to binary tree
		
		BinaryTreeExp binTree = new BinaryTreeExp();
		BinaryTreeNode binNode = binTree.insertElements();
		
		//display binary tree
		//binTree.display(binNode);
		

		//String userIp = "これ";
		//accept input from user
		System.out.println("Please enter input =>");
		Scanner input = new Scanner(System.in);
		String userStr = input.nextLine();
		System.out.println(userStr);
		
		ArrayList<BinaryTreeNode> arrNodeList = new ArrayList<BinaryTreeNode>();
		getWordList(userStr, binNode, arrNodeList);		
		//sort
		Collections.sort(arrNodeList, new Comparator(){
			 
            public int compare(Object o1, Object o2) {
            	BinaryTreeNode p1 = (BinaryTreeNode) o1;
            	BinaryTreeNode p2 = (BinaryTreeNode) o2;
               return p1.compareTo(p2);
            }
 
        });
		
		//give first 3-4 suggestions to the user
		int cnt = 0;
		for(int i = arrNodeList.size()-1; i >= 0; i--){
			//if(cnt < 4 && arrNodeList.size() > 3){
			//if(cnt < 4){
				System.out.println("もうしかして? : => " + arrNodeList.get(i).word);
				cnt++;
				//System.out.println("arr => " + arrNodeList.get(i).noOfOccurences);
			//}
		}
	}

	private static void getWordList(String userIp, BinaryTreeNode binNode, ArrayList<BinaryTreeNode> arrList) {
		// TODO Auto-generated method stub
		//ArrayList<BinaryTreeNode> arrList = new ArrayList<BinaryTreeNode>();
		//boolean dataFound = false;
		if(userIp != null && userIp != ""){
			if(binNode.word.compareTo(userIp) > 0 || binNode.word.startsWith(userIp)){
					//return (getWordList(userIp, binNode.rightNode));
				if(binNode.leftNode != null)
					getWordList(userIp, binNode.leftNode, arrList);
				
			}
			if(binNode.word.startsWith(userIp))
				arrList.add(binNode);
			if(binNode.word.compareTo(userIp) < 0 || binNode.word.startsWith(userIp)){

				//return (getWordList(userIp, binNode.leftNode));
				if(binNode.rightNode != null)
					getWordList(userIp, binNode.rightNode, arrList);
			
			}

		}

	}
}

