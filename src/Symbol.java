//Matt Condon
//13523107

public class Symbol {

    private String expression;
    private int value;
    private boolean isTerminal = false;

    Symbol(String expression, int value, boolean isTerminal){
        this.expression = expression;
        this.value = value;
        this.isTerminal = isTerminal;
    }

    public void setStr(String str){ expression = str; }
    public String getStr(){ return expression;}

    public void setValue(int value){ this.value = value; }
    public int getValue(){ return value; }

    public void setTerminal(boolean term){ isTerminal = term; }
    public boolean isTerminal(){ return isTerminal; }
    public boolean isEpsilon(){ return expression.equals("epsilon") ? true : false; }

}
