package supportive;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Support {
    static public List<Long> FindElem(String word, String elem){
        List<Long> list = null;
        if (elem.length() == 1){
            for (int i = 0; i < word.length(); i++) {
                if (word.toCharArray()[i] == elem.charAt(0)) {
                    if (list == null) {
                        list = new ArrayList<>();
                        list.add(Long.valueOf(i));
                    }
                    else{
                        list.add(Long.valueOf(i));
                    }
                }
            }

        }
        else{
            boolean found = false;
            for (int i = 0; i < word.length() && !found;) {
                String temp = "";
                for (int j = 0; j < elem.length(); j++) {
                    if ((i + 1) == word.length()){
                        found = true;
                        break;
                    }
                    temp += word.charAt(i++);
                }
                if (temp.equals(elem)) {
                    if (list == null) {
                        list = new ArrayList<>();
                        list.add(Long.valueOf(i));
                    }
                    else{
                        list.add(Long.valueOf(i));
                    }
                }
            }
        }
        return list;
    }
}
