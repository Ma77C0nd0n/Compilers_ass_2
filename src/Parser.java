//Matt Condon
//13523107

import java.io.*;
import java.util.*;

public class Parser {

    private static int N = 1000;

    private int[][] pairs = new int[N][2];
    private ArrayList<ArrayList<Symbol>> token_list = new ArrayList<>();
    private ArrayList<Symbol> current_token = new ArrayList<>();
    private Grammar G = new Grammar();
    private int RHS_tested = 0, RHS_success = 0, RHS_discard = 0;

    public Parser(String filename) {
        createPairs(filename);
        ArrayList<Symbol> token = new ArrayList<>();
        HashMap<Integer, String> terminal_hash = get_terminals();
        create_tokens(terminal_hash);
        current_token = token_list.get(2);
        int i=0;

        PredictiveParser Pred = new PredictiveParser(G);
//        System.out.println("\n\n"+Pred.PredictiveParse(current_token));

//        ArrayList<Symbol> a = new ArrayList<>(Arrays.asList(new Symbol("S", 0, false)));
//        for(Symbol x : Pred.FirstSet(a, G)) {
//            System.out.println(x.getStr());
//        }
//        for(Symbol s : Pred.FollowSet("BExp")){
//            System.out.println(s.getStr());
//        }
//        test_production_fits_lang();
    }

    public ArrayList<ArrayList<Symbol>> getToken_list(){
        return token_list;
    }

    private HashMap<Integer, String> get_terminals(){
        String[] terminals = {"num", "id", "(", ")", "and", "or", "not", "true", "false", "=", "<", ">"};
        HashMap<Integer, String> hash = new HashMap<>();
        for (int i = 0; i < terminals.length; i++)
            hash.put(i + 1, terminals[i]);
        return hash;
    }

    private void create_tokens(HashMap<Integer, String> terminals) {
        for (int i=0; pairs[i][0] != 0; i++) {
            ArrayList<Symbol> symbolsList = new ArrayList<>();
            int j;
            for (j = 0; pairs[j + i][0] != 99; j++) {
                symbolsList.add(new Symbol(terminals.get(pairs[i+j][0]), pairs[i+j][1], true));
            }
            i += j;
            symbolsList.add(new Symbol("$$$", 0, true));
            token_list.add(symbolsList);
        }
    }

    private void createPairs(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(filename)));
            int cur, i = 0, j = 0;
            String cur_str = "";
            while ((cur = reader.read()) != -1)
                if (cur != ' ' && cur != '\t' && cur != '\n' && cur != -1 && cur != 0)
                    cur_str += (char) cur;
                else {
                    if (cur_str.length() > 0) {
                        pairs[i][j] = parse_my_int(cur_str);
                        if (j == 0) j = 1;
                        else {
                            i++;
                            j = 0;
                        }
                        cur_str = "";
                    }
                }
        } catch (IOException e) {
            System.err.print("Caught IOException: " + e.getMessage());
        }
    }

    private int parse_my_int(String str) {
        int answer = 0, factor = 1;
        for (int i = str.length() - 1; i >= 0; i--) {
            answer += (str.charAt(i) - '0') * factor;
            factor *= 10;
        }
        return answer;
    }

    public void testRDP(){
        int i=0;
        for(ArrayList<Symbol> x : token_list) {
            current_token = x;
            i++;
            System.out.print("Result "+i+" is: ");
            System.out.println(RDP(new Symbol("S", 0, false), 0).getSuccess() ? "Success" : "Failure");
            System.out.println("RHS tested: " + RHS_tested);
            System.out.println("RHS success: " + RHS_success);
            System.out.println("RHS discarded: " + RHS_discard + "\n");
            RHS_tested = 0; RHS_success = 0; RHS_discard = 0;
        }
    }

    //Recursive Descent Parser
    private Result RDP(Symbol NT, int STARTPOS){
        prodLoop:
        for(Production P : G.getGrammar()) {

            if (P.getLHS().getStr().equals(NT.getStr())) {
                int NEXTPOS = STARTPOS;
                for (Symbol SYM : P.getRHS()) {
                    RHS_tested++;
                    if (SYM.isTerminal()) {
                        if (current_token.get(NEXTPOS).getStr().equals(SYM.getStr())) {
                            NEXTPOS++;
                            RHS_success++;
                        } else {
                            RHS_discard++;
                            continue prodLoop;
                        }
                    } else {
                        if(SYM.isEpsilon()) {
                            return new Result(true, NEXTPOS);
                        }
                        Result new_res = RDP(SYM, NEXTPOS);
                        if (new_res.getSuccess()) {
                            NEXTPOS = new_res.getPos();
                        }
                        else{
                            continue prodLoop; //checks next matching LHS
                        }
                    }
                }
                /* all symbols in the RHS of P were successfully matched */
                return new Result(true, NEXTPOS);
            }
        }
        return new Result(false, -1);
    }

    private class Result{
        private boolean success;
        private int pos;
        public Result(boolean success, int pos){
            this.success = success;
            this.pos = pos;
        }
        public boolean getSuccess(){ return success; }
        public int getPos(){ return pos; }
    }
}
