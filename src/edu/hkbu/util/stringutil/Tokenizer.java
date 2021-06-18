package edu.hkbu.util.stringutil;
import java.util.*;
public class Tokenizer {

    public static void main(String [] args){
        String test = "   For with you is the fountain  of   life";
        String test1 = "For with you is the fountain  of   life  ";
        String[] result = tokenize(test);
        String[] result1 = tokenize(test1);
    }

    public static String[] tokenize(String str) {
        StringBuilder sb = new StringBuilder();

        LinkedList<String> list = new LinkedList<>();

        boolean onToken =false;
        for (int i = 0; i < str.length(); i++) {
            if(!onToken){
                if(str.charAt(i) != ' '){
                    onToken = true ;
                    sb = new StringBuilder();
                    sb.append(str.charAt(i));
                }
            }else{
                if(str.charAt(i) != ' '){
                    sb.append(str.charAt(i));
                    if(i  == str.length() -1){
                        list.add(sb.toString());
                    }
                }else{
                    onToken = false;
                    list.add(sb.toString());
                }
            }

        }
        String [] result = new String[list.size()];
        list.toArray(result);
        return result;
    }

    public static String trim(String str) {
        StringBuilder sb = new StringBuilder();
        boolean trimHead = str.charAt(0) == ' ';
        boolean trimTail = str.charAt(str.length()) == ' ';
        int head = 0  ;
        int tail = str.length() -1;

        for (int i = 0; i < str.length() && trimHead; i++) {
            if (str.charAt(i) != ' ') {
                head = i ;
                break;
            }
        }

        for(int i = str.length() -1 ; i >= 0 && trimTail ; i --){
            if(str.charAt(i) != ' '){
                tail = i ;
                break;
            }
        }

        if(trimHead || trimTail) {
            for (int i = head; i < tail - head + 1; i++) {
                sb.append(str.charAt(i));
            }
            return sb.toString();
        }
        else{
            return str;
        }

    }
}
