//Matt Condon
//13523107

import java.util.ArrayList;

public class Production {

    Symbol LHS = new Symbol(null, 0, false);
    ArrayList<Symbol> RHS = new ArrayList<>();

    public Production(Symbol LHS, ArrayList<Symbol> RHS) {
        this.LHS = LHS;
        this.RHS = RHS;
    }

    public Symbol getLHS() {return LHS;}
    public ArrayList<Symbol> getRHS() {return RHS;}
}