//Matt Condon
//13523107

import java.util.*;

public class PredictiveParser {

    private Hashtable<String, Set<Symbol>> FollowSetHashTable = new Hashtable<>();
    private Hashtable<String, Set<Symbol>> InheritorsHashTable = new Hashtable<>();
    private Hashtable<Production, Set<Symbol>> PredParseTable = new Hashtable<>();
    private Production curProd;

    public PredictiveParser(Grammar G){
        PrepareFollowSetHashTable(G, new Symbol("S", 0, false));
        PreparePPTable(G);
//        printParseTable();
//        for(Production P : G.getGrammar()){
//            Symbol[] s = PredParseTable.get(P);
//            System.out.println(P.getLHS().getStr() + " " + s[0].getStr() + " " + s[1].getStr());
//        }
    }

    public Set<Symbol> FirstSet (ArrayList<Symbol> SymSeq, Grammar G){
        if(SymSeq.size()==0 || SymSeq.get(0).isEpsilon()) {
            return new HashSet<Symbol>(Arrays.asList(new Symbol("epsilon", 0, false)));
        }
        else if(SymSeq.get(0).isTerminal())
            return new HashSet<Symbol>(Arrays.asList(SymSeq.get(0)));
        else {
            Symbol NT = SymSeq.get(0);
            Set<Symbol> F2 = new HashSet<>();
            for (Production P : G.getGrammar()) {
                if (P.getLHS().getStr().equals(NT.getStr())) {
                    F2.addAll(FirstSet(P.getRHS(), G));
                }
            }
            for (Symbol s : F2) {
                if (s.isEpsilon()) {
                    F2.remove(s);
                    SymSeq.remove(0);
                    F2.addAll(FirstSet(SymSeq, G));
                    return F2;
                }
            }
            return F2;
        }
    }

    public Set<Symbol> FollowSet( String NT ){
        return FollowSetHashTable.get(NT);
    }

    private void PrepareFollowSetHashTable (Grammar G, Symbol SentenceSymbol){
        FollowSetHashTable.clear();
        InheritorsHashTable.clear();
        FollowSetHashTable.put(SentenceSymbol.getStr(),new HashSet<Symbol>(Arrays.asList(new Symbol("$$$", 0, true))));
        for (Production P : G.getGrammar()){
            ArrayList<Symbol> R = P.getRHS();
            int len = R.size();
            for(int i = 0; i <= len-1; i++){
                Symbol Sym = R.get(i);
                if(!Sym.isTerminal()){
                    ArrayList<Symbol> FSEntry = new ArrayList<>();
                    for(int j=i+1; j<=len-1; j++)
                        FSEntry.add(R.get(j));
                    for(Symbol F : FirstSet(FSEntry, G)){
                        if(F.isEpsilon())
                            InheritorsHashTable = addToStringTable(InheritorsHashTable,P.getLHS().getStr(), Sym);
                        else FollowSetHashTable = addToStringTable(FollowSetHashTable, Sym.getStr(), F);
                    }
                }
            }
        }
        boolean Idle = false;
        while(!Idle){
            Idle = true;
            Enumeration<String> enumKey = InheritorsHashTable.keys();
            while(enumKey.hasMoreElements()){
                String NT1 = enumKey.nextElement();
                Set<Symbol> V = InheritorsHashTable.get(NT1);
                for(Symbol NT2 : V){
                    if(FollowSetHashTable.containsKey(NT1)) {
                        for (Symbol Terminal : FollowSetHashTable.get(NT1)) {
                            int setSize=-1;
                            if(FollowSetHashTable.containsKey(NT2.getStr()))
                                setSize = FollowSetHashTable.get(NT2.getStr()).size();
                            FollowSetHashTable = addToStringTable(FollowSetHashTable, NT2.getStr(), Terminal);
                            if(FollowSetHashTable.get(NT2.getStr()).size() != setSize)
                                Idle = false;
                            //set size has not changed -> contents have not changed
                        }
                    }
                }
            }
        }
    }

    private Hashtable<String, Set<Symbol>> addToStringTable(Hashtable<String, Set<Symbol>> table, String key, Symbol sym) {
        if (table.containsKey(key)) {
            Set<Symbol> Update = table.get(key);
            Update.add(sym);
            table.put(sym.getStr(), Update);
        }else{
            table.put(key, new HashSet<Symbol>(Arrays.asList(sym)));
        }
        return table;
    }

    public Set<Symbol> PredictiveParsingTable( Production P ){
        return PredParseTable.get(P);
    }

    private void PreparePPTable(Grammar G){
        for(Production P : G.getGrammar()){
            for(Symbol a : FirstSet(P.getRHS(), G)){
                if(a.isEpsilon()){
                    for(Symbol b : FollowSet(P.getLHS().getStr())) {
                        PredParseTable = addToProdTable(PredParseTable, P, b);
                    }
                }
                else if(a.isTerminal()) {
                    PredParseTable = addToProdTable(PredParseTable, P, a);
//                    System.out.print(P.getLHS().getStr() + " -> ");
//                    for(Symbol s: P.getRHS())
//                        System.out.print(s.getStr() + ", ");
//                    System.out.println("is at "+P.getLHS().getStr() + ", " + a.getStr());
                }
            }
        }
    }


    private Hashtable<Production, Set<Symbol>> addToProdTable(Hashtable<Production, Set<Symbol>> table, Production key, Symbol sym) {
        if (table.containsKey(key)) {
            Set<Symbol> Update = table.get(key);
            Update.add(sym);
            table.put(key, Update);
        }else{
            table.put(key, new HashSet<Symbol>(Arrays.asList(sym)));
        }
        return table;
    }

    private void printParseTable(){
        Enumeration<Production> enumKey = PredParseTable.keys();
        while(enumKey.hasMoreElements()){
            Production P = enumKey.nextElement();
            System.out.print("\n"+P.getLHS().getStr() + " -> ");
            for(Symbol s: P.getRHS())
                System.out.print(s.getStr() + ", ");
            Set<Symbol> s = PredParseTable.get(P);
            System.out.print("is at ");
            for(Symbol s1 : s){
                System.out.print(P.getLHS().getStr() + " " + s1.getStr() + ", ");
            }
            //same production could have multiple table positions
        }
    }

    public boolean PredictiveParse(ArrayList<Symbol> token){
        Stack<Symbol> st = new Stack();
        st.push(new Symbol("$", 0, true));
        st.push(new Symbol("S", 0, false));
        int pos = 0;
        Symbol a = token.get(pos);
        Symbol T = st.peek();
        while(T.getStr()!="$") {
            if (T.getStr().equals(a.getStr())) {
                st.pop();
                T = st.peek();
                pos++;
                if (pos != token.size()){
                    System.out.println("\nremoved: " + a.getStr());
                    a = token.get(pos);
                }
            }
            else if(T.isEpsilon()){
                st.pop().getStr();
                T=st.peek();
            }
            else if(T.isTerminal()) {
                return false;
            }
            else if(posIsEmpty(T, a)) {
                return false;
            }
            else{
                st.pop();
                Stack<Symbol> temp = new Stack<>();
                System.out.print("\n"+curProd.getLHS().getStr() + " ::= ");
                for(Symbol next: curProd.getRHS()) {
                    System.out.print(next.getStr() + " ");
                    temp.push(next);
                }
                while(!temp.isEmpty())
                    st.push(temp.pop());
                T = st.peek();

                for(Symbol s : st){
                    System.out.println("* "+s.getStr()+" *");
                }
            }
        }
        return true;
    }

    private boolean posIsEmpty(Symbol t, Symbol a) {
        for (Production P : PredParseTable.keySet()) {
            if(t.isEpsilon())
                for (Symbol s : PredParseTable.get(P))
                    if(s.getStr().equals(a.getStr())) {
                        curProd = P;
                        return false;
                    }
            if (P.getLHS().getStr().equals(t.getStr()))
                for (Symbol s : PredParseTable.get(P))
                    if (s.getStr().equals(a.getStr())) {
                        curProd = P;
                        return false;
                    }
        }
        return true;
    }
}
