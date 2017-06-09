package wordbox_7;

import java.util.*;

class WordStack{
             
    private final ArrayList<Character> from_matr;
    private final ArrayList<Character> from_dic;
    
    private final ArrayList<Integer> x;
    private final ArrayList<Integer> y;
    
    //Конструктор для создания пустого стека ячеек    
    WordStack(){
        from_matr=new ArrayList<>();
        from_dic=new ArrayList<>();
        x=new ArrayList<>();
        y=new ArrayList<>();
    }
    
    //Конструктор для создания стека на основе другого стека
    WordStack(WordStack stack) {
        from_matr=new ArrayList<>();
        from_dic=new ArrayList<>();
        x=new ArrayList<>();
        y=new ArrayList<>();
        
        int l=stack.from_matr.size();
        
        for(int i=0;i<l;i++){
            from_matr.add(stack.from_matr.get(i).charValue());
            from_dic.add(stack.from_dic.get(i).charValue());
            x.add(stack.x.get(i).intValue());
            y.add(stack.y.get(i).intValue());
        }
        
    }
    
    public void add(char m, char d, int xCoord, int yCoord){
        from_matr.add(m);
        from_dic.add(d);
        x.add(xCoord);
        y.add(yCoord);
    }
    
    public void remove(){
        if(!from_matr.isEmpty()){
            from_matr.remove(from_matr.size()-1);
            from_dic.remove(from_dic.size()-1);
            x.remove(x.size()-1);
            y.remove(y.size()-1);
        }
    }

    public boolean validate(){
        
        if(from_matr.isEmpty())return false;
        
        boolean bit;
        bit=true;
                
        int countSpace;
        countSpace=0;
        for(int i=0;i<from_matr.size();i++){
            if(from_matr.get(i).charValue()==' '){
                countSpace++;
                if(countSpace>1){
                    bit=false;
                    break;
                }
                continue;
            }
            if(from_matr.get(i).charValue()!=from_dic.get(i).charValue()){
                bit=false;
                break;
            }
        }
        return (bit & countSpace==1);
        
    }
    
    public int get_X(int index){
        if(index<0 | index>x.size())return -1;
        return x.get(index);
    }
    
    public int get_Y(int index){
        if(index<0 | index>y.size())return -1;
        return y.get(index);
    }
    
    public int getLen(){
        return from_matr.size();
    }
    
    @Override
    public String toString(){
        String str="";
        for(char ch: from_dic)str+=ch;
        return str;
    }
}