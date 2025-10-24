package supportive;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Support {
    static public List<Long> FindElem(String word, char elem){
        List<Long> list = null;
        for (int i = 0; i < word.length(); i++) {
            if (word.toCharArray()[i] == elem) {
                if (list == null) {
                    list = new ArrayList<>();
                    list.add(Long.valueOf(i));
                }
                else{
                    list.add(Long.valueOf(i));
                }
            }
        }
        return list;
    }
}
