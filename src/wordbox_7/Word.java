
package wordbox_7;

import java.io.*;
import java.util.*;

/**
 * Данный класс реализует методы поиска слов на игровом поле
 * @author Сергей Лебидко
 */
public class Word {
    
    private LinkedList<String> d;            //Массив-словарь
    private LinkedList<String> stopWords;    //Массив запрещенных слов
    
    private char[][] m;                      //Матрица символов, с которой будем работать
    private int cSize;                       //Размер рабочей матрицы
    private LinkedList<WordStack> ws;        //Стеки найденных слов
       
    Word() throws IOException{
        
        //Загружаем список слов из файла на диске во внутренний словарь в оперативной памяти
        String dictionaryPath;
        dictionaryPath="wrd.txt";
            
        File f;
        f=new File(dictionaryPath);
            
        if(!f.exists() | !f.canRead())throw new IOException("Не могу прочитать словарик... :'(");
        
        FileInputStream fis=null;
        InputStreamReader isr=null;
        BufferedReader br=null;
         
        try{
            //Создаем входящий поток байт из файла dictionaryPath
            fis=new FileInputStream(dictionaryPath);
          
            //Поток байт конвертируем в поток символов
            isr=new InputStreamReader(fis,"Windows-1251");
        
            //Поток символов конвертируем в отдельные строки
            br=new BufferedReader(isr);
            
            String line;
            d=new LinkedList<>();
           
            do{
                line=br.readLine();
                if(line!=null)d.add(line);
            }while(line!=null);
        }catch(IOException ex){
            throw new IOException("Не могу прочитать словарь... :'(");
        }finally{
            if(br!=null)br.close();
            if(isr!=null)isr.close();
            if(fis!=null)fis.close();
        }
        
        //Создаем список запрещенных слов
        stopWords=new LinkedList<>();
        
        //Создаем вспомогательный архив стеков слов
        ws=new LinkedList<>();
            
    }
    
    //Метод возвращает слово заданной длины. Это слово используется классом Game как стартовое для начала игры
    public String getStartWord(int l) throws Exception{
        Random rnd;
        rnd=new Random();
        
        int maxCount;
        int count;
        
        maxCount=rnd.nextInt(d.size()*2)+1;
        count=0;
        
        while (true) {            
            for(String s: d){
                if(s.length()==l){
                    count++;
                    if(count==maxCount){
                        stopWords.add(s);
                        return s;
                    }
                }
            }
            if(count==0)throw new Exception("В моем словарике нет подходящего по длине слова... :'(");
        }
        
    }
    
    //Этот метод проверяет, имеется ли заданное строкой str слово в словаре запрещенных слов. Если его там нет, метод возвращает false и добавляет слово str в словарь и в список запрещенных слов
    public boolean isDisabledWords(String str){
        
        //Ищем слово str в массиве запрещенных слов и, если нашли, возвращаем true
        for(String s: stopWords)if(str.equals(s))return true;
        
        //Ищем слово str в словаре d. И если нашли - возвращаем false
        for(String s: d)if(str.equals(s)){
            stopWords.add(str);
            return false;
        }
        
        //Пытаемся добавить str в словарь на диске wrd.txt. Возвращаем false
        try {PrintWriter f=new PrintWriter(new OutputStreamWriter(new FileOutputStream("wrd.txt", true), "Windows-1251"));
            f.println();
            f.print(str);
            f.flush();
        } catch (Exception e) {}
        
        stopWords.add(str);
        d.add(str);
        return false;
        
    }
    
    //Этот метод очищает список запрещенных слов. Это нужно делать перед началом новой игры
    public void clearStopWords(){
        stopWords.clear();
    }
    
    //Этот метод ищет ответный ход и возвращает его в виде массива ячеек Cell. Если компьютер не может найти ход, он возвращает null (признак пропуска хода)
    public Cell[] seeker(Cell[][] c){
        
        //Формируем новую рабочую матрицу
        cSize=c.length;
        m=new char[cSize][cSize];
        for(int i=0;i<cSize;i++)
            for(int j=0;j<cSize;j++)m[i][j]=c[i][j].get_text();
        
        //Очищаем массив стеков
        ws.clear();
        
        //Перебираем слова из словаря
        for(String dtmp: d){
            
            //Сперва проверяем, не содержится ли очередное слово из словаря в списке запрещенных. Если содержится, то пропускаем поиск этого слова в матрице
            boolean bit;
            bit=false;
            for(String stmp: stopWords)if(dtmp.equals(stmp)){
                    bit=true;
                    break;
                }
            if(bit)continue;
            
            //Теперь ищем dtmp в матрице
            for(int i=0;i<cSize;i++)
                for(int j=0;j<cSize;j++)if(m[i][j]==dtmp.charAt(0) | m[i][j]==' ')E(dtmp, j, i, new WordStack());
        }    
            
        //Если полученный в итоге массив стеков пуст - пропускаем ход
        if(ws.isEmpty())return null;
            
        //Теперь нужно определить максимальную длину полученных стеков
        int maxLen=0;
        for(WordStack stack: ws)if(stack.getLen()>maxLen)maxLen=stack.getLen();
            
        //Теперь нужно выбрать случайный стек максимальной длины
        WordStack maxStack=null;
        Random rnd;
        rnd=new Random();
        int maxCount;
        int count;
        maxCount=rnd.nextInt(ws.size()*2)+1;
        count=0;
        while (count!=maxCount) {
            for(WordStack stack: ws){
                if(stack.getLen()==maxLen){
                    count++;
                    if(count==maxCount){
                        maxStack=stack;
                        break;
                    }
                }
            }
        }
        
        //Теперь формируем из объекта maxStack массив ячеек и передаем его в вызывающий код
        Cell[] outCells;
        String outString;
        outCells=new Cell[maxLen];
        outString=maxStack.toString();
        for(int i=0;i<maxLen;i++){
            outCells[i]=c[maxStack.get_Y(i)][maxStack.get_X(i)];
            if(outCells[i].isEmpty())outCells[i].set_text(outString.charAt(i));
        }
        
        //Добавляем выбранное компьютером слово в массив запрещенных слов
        stopWords.add(outString);
        
        //Возврат значения
        return outCells;
    }
    
    //Метод ищет варианты укладки конкретного слова
    private void E(String str, int x, int y, WordStack stack){
               
        //Обработка случаев, в которых ответ однозначен - false
        if((x<0) | (y<0) | (x>=cSize) | (y>=cSize))return;
        if(m[y][x]=='*')return;
        
        //Обработка случая, в котором входящая строка состоит из одного символа
        if(str.length()==1){
            stack.add(m[y][x], str.charAt(0), x, y);
            if(stack.validate())ws.add(new WordStack(stack));
            stack.remove();
            return;
        }
        
        //Обработка случая, в котором входящая строка состоит более чем из одного символа
        String substr;
        char mtmp;
        
        stack.add(m[y][x], str.charAt(0), x, y);
        mtmp=m[y][x];
        m[y][x]='*';
        substr=str.substring(1);
        E(substr, x, y-1, stack);
        E(substr, x+1, y, stack);
        E(substr, x, y+1, stack);
        E(substr, x-1, y, stack);
        m[y][x]=mtmp;
        stack.remove();
    
    }
    
}
