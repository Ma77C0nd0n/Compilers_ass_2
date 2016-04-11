import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        Parser a = new Parser("./Examples Assignment2 Correct Tokenised");
//        a.testRDP();
        ArrayList<ArrayList<Symbol>> t = a.getToken_list();
        PredictiveParser p = new PredictiveParser(new Grammar());
        p.PredictiveParse(t.get(0));
    }
}
