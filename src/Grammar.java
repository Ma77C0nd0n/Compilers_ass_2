//Matt Condon
//13523107

import java.util.ArrayList;
import java.util.Arrays;

public class Grammar {

    private ArrayList<Production> theGrammar = new ArrayList<>();
    private Symbol BExp = new Symbol("BExp", 0, false);
    private Symbol BTerm = new Symbol("BTerm", 0, false);
    private Symbol BExp1 = new Symbol("BExp1", 0, false);
    private Symbol BFactor = new Symbol("BFactor", 0, false);
    private Symbol BFactor1 = new Symbol("BFactor1", 0, false);
    private Symbol BConst = new Symbol("BConst", 0, false);
    private Symbol id = new Symbol("id", 0, true);
    private Symbol num = new Symbol("num", 0, true);
    private Symbol epsilon = new Symbol("epsilon", 0, false);


    public Grammar(){
        theGrammar.add(new Production(new Symbol("S", 0, false), new ArrayList<Symbol>(Arrays.asList(BExp, new Symbol("$$$", 0, true)))));
        theGrammar.add(new Production(BExp, new ArrayList<Symbol>(Arrays.asList(BTerm, BExp1))));

        theGrammar.add(new Production(BTerm, new ArrayList<Symbol>(Arrays.asList(BFactor))));
        theGrammar.add(new Production(BTerm, new ArrayList<Symbol>(Arrays.asList(new Symbol("not", 0, true), BTerm))));

        theGrammar.add(new Production(BExp1, new ArrayList<Symbol>(Arrays.asList(new Symbol("and", 0, true), BTerm, BExp1))));
        theGrammar.add(new Production(BExp1, new ArrayList<Symbol>(Arrays.asList(new Symbol("or", 0, true), BTerm, BExp1))));
        theGrammar.add(new Production(BExp1, new ArrayList<Symbol>(Arrays.asList(epsilon))));

        theGrammar.add(new Production(BFactor, new ArrayList<Symbol>(Arrays.asList(new Symbol("(", 0, true), BExp, new Symbol(")", 0, true)))));
        theGrammar.add(new Production(BFactor, new ArrayList<Symbol>(Arrays.asList(BConst))));
        theGrammar.add(new Production(BFactor, new ArrayList<Symbol>(Arrays.asList(id, BFactor1))));
        //left factoring through addition of BFactor1
        theGrammar.add(new Production(BFactor1, new ArrayList<Symbol>(Arrays.asList(new Symbol("=", 0, true), num))));
        theGrammar.add(new Production(BFactor1, new ArrayList<Symbol>(Arrays.asList(new Symbol(">", 0, true), num))));
        theGrammar.add(new Production(BFactor1, new ArrayList<Symbol>(Arrays.asList(new Symbol("<", 0, true), num))));
        theGrammar.add(new Production(BFactor1, new ArrayList<Symbol>(Arrays.asList(epsilon))));

        theGrammar.add(new Production(BConst, new ArrayList<Symbol>(Arrays.asList(new Symbol("false", 0, true)))));
        theGrammar.add(new Production(BConst, new ArrayList<Symbol>(Arrays.asList(new Symbol("true", 0, true)))));
    }

    public ArrayList<Production> getGrammar(){
        return theGrammar;
    }
}
