package wordbox_7;

import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;

/**
 * Данный класс реализует логику взаимодействия игрока с компьютером
 * Цель вынесения логики взаимодействия в отдельный класс - улучшить читаемость кода, разделив логику и методы создания интерфейса
 * @author Сергей Лебидко
 */
public class Game implements  MouseListener, KeyListener{
    
    private final JFrame F;         //Главное окно
    private final JLabel L1;        //Табло вывода очков пользователя
    private final JLabel L2;        //Табло вывода очков компьютера
    private final JTextArea T1;     //Табло вывода ходов игрока
    private final JTextArea T2;     //Табло вывода ходов компьютера
    private final JLabel L3;        //Табло вывода текущего слова
    
    private final Cell[][] c;       //Ячейки игрового поля
    private final int cSize;        //Размер игрового поля (опять же, мы предполагаем, что игровое поле - квадратное)
    
    private Word wrd;
    
    private int userScore;          //Количество очков, набранное пользователем
    private int compScore;          //Количество очков, набранное компьютером
    
    private int userPass;           //Количество пропусков хода пользователем
    private int compPass;           //Количество пропусков хода компьютером 
    
    private int stat;               //Равен 0 - требуется очистка поля после предыдущего хода, равен 1 - ждем ввода недостающей буквы, равен 2 - ждем ввода слова
    private Cell focusCell;         //Ячейка, в которую игрок вводит недостающую букву
    private ArrayList<Cell> pool;   //Пул ячеек, составляющих вводимое слово
    private String strPool;         //Вводимое слово
            
    public Game(JFrame frame, JLabel label1, JLabel label2, JTextArea text1, JTextArea text2, JLabel label3, Cell[][] cells) {
        F=frame;
        L1=label1;
        L2=label2;
        T1=text1;
        T2=text2;
        L3=label3;
        c=cells;
        cSize=c.length;
        pool=new ArrayList<>();
              
        try {
            wrd=new Word();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(),"Хьюстон, у нас проблемы!", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        
        startGame();
                       
    }
    
    private void startGame(){
        //Обнуляем внутренние переменные
        userScore=0;
        compScore=0;
        userPass=0;
        compPass=0;
        stat=1;
        pool.clear();
        strPool="";
        
        //Очищаем игровое поле
        for(int i=0;i<cSize;i++)
            for(int j=0;j<cSize;j++){
                c[i][j].set_style(Cell.NORMAL);
                c[i][j].set_text(' ');
            }
        
        //Обнуляем все табло
        L1.setText(""+userScore);
        L2.setText(""+compScore);
        L3.setText(strPool);
        T1.setText("");
        T2.setText("");
        
        //Очищаем список запрещенных слов. В новой игре он будет уже не актуален
        wrd.clearStopWords();        
        
        //Выводим на игровое поле стартовое слово
        String startWord="";
        try {
            startWord=wrd.getStartWord(cSize);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(),"Хьюстон, у нас проблемы!", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        int outputLine;
        outputLine=(int)(cSize/2);
        for(int j=0;j<cSize;j++)c[outputLine][j].set_text(startWord.charAt(j));
        
    }

    private void endGame(){
        String str="";
        Label1:{
            if(userPass==3){
                str="<html>Вы проиграли... Три раза пропустили ход<br>Хотите сыграть еще?";
                break Label1;
            }
            if(compPass==3){
                str="<html>Вы победили! Компьютер три раза пропустил ход...<br>Хотите сыграть еще?";
                break Label1;
            }
            if(userScore>compScore){
                str="<html>Вы победили! Набрали больше очков, чем компьютер!<br>Хотите сыграть еще?";
                break Label1;
            }
            if(userScore<compScore){
                str="<html>Вы проиграли... Компьютер набрал больше очков<br>Хотите сыграть еще?";
                break Label1;
            }
            if(userScore==compScore){
                str="<html>Ничья! Победила дружба!<br>Хотите сыграть еще?";
                break Label1;
            }
        }
        int answer;
        answer=JOptionPane.showConfirmDialog(F, str, "Игра окончена", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null);
        if((answer==JOptionPane.CLOSED_OPTION) | (answer==JOptionPane.NO_OPTION))System.exit(0);
        startGame();
        
    }
    
    //В отдельный метод вынесены команды добавления обработчиков событий. Это сделано во избежание появления предупреждения "Потеря this в конструкторе"
    public void StartEventsListeners(){
        for(int i=0;i<cSize;i++)
            for(int j=0;j<cSize;j++){
                c[i][j].addMouseListener(this);
            }
        F.setFocusable(true);
        F.addKeyListener(this);               
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        
        //Допускаются клики только левой клавишей мыши
        if(e.getButton()!=1)return;
        
        //Убираем с игрового поля предыдущий ход
        if(stat==0){
            L3.setText("");
            for(int i=0;i<cSize;i++)
                for(int j=0;j<cSize;j++)c[i][j].set_style(Cell.NORMAL);
            stat=1;
        }
               
        Cell cell;
        cell=(Cell)(e.getSource());
        
        //Если мы находимся в режиме ввода недостающей буквы
        if (stat==1) {
            
            if(cell.isFull())return;
            
            boolean bit=false;
            for(Cell[] c1: c)
                for(Cell c2: c1){
                    bit=bit | (c2.isFull() & cell.isNear(c2));
                }
            if(!bit)return;
            
            if(focusCell!=null){
                focusCell.set_style(Cell.NORMAL);
            }
            
            focusCell=cell;
            focusCell.set_style(Cell.FOCUS);
            
        }
        
        //Если мы находимся в режиме ввода слова
        if(stat==2){
            
            //В этом режиме принимаются только заполненные ячейки
            if(cell.isEmpty())return;
            
            //Если пул пуст
            if(pool.isEmpty()){
                pool.add(cell);
                cell.set_style(Cell.SELECTED);
                strPool+=""+cell.get_text();
                L3.setText(strPool);
                return;
            }
            
            //Если пул не пуст
            if(!cell.isNear(pool.get(pool.size()-1)))return;
            for(Cell ctmp: pool)if(cell==ctmp)return;
            pool.add(cell);
            cell.set_style(Cell.SELECTED);
            strPool+=""+cell.get_text();
            L3.setText(strPool);
            
        }
        
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {
                
        //Ввод символов допускается, только в режиме ожидания ввода
        if(stat!=1)return;
        
        //Ячейка, в которую производим ввод должна быть выбрана
        if(focusCell==null)return;
        
        //Символ должен быть разрешен
        String enabledChars="ЙЦУКЕНГШЩЗХЪЭЖДЛОРПАВЫФЯЧСМИТЬБЮ";
        String inputChar=""+e.getKeyChar();
        inputChar=inputChar.toUpperCase();
        if(!enabledChars.contains(inputChar))return;
        
        focusCell.set_text(inputChar.charAt(0));
        stat=2;
    }

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        
        //Обрабатываются только нажатия клавиш Enter и Esc
        if(e.getKeyCode()!=27 & e.getKeyCode()!=10)return;
        
        //Если нажата клавиша Enter
        if(e.getKeyCode()==10){
            //Сперва проверяем валидность введенного слова. 
            //Во-первых, в нем должно быть более одного символа.
            //Во-вторых, в него должна входить ячейка, в которую ввели букву.
            //В-третьих, слово, введенное игроком, долно быть разрешенным
            if(strPool.length()<2)return;
            boolean bit=false;
            for(int i=0;i<pool.size();i++)if(pool.get(i).equals(focusCell)){
                bit=true;
                break;
            }
            if(!bit)return;
            if(wrd.isDisabledWords(strPool)){
                JOptionPane.showMessageDialog(F, "Такое слово уже было! Введите другое!", "Ошибка", JOptionPane.ERROR_MESSAGE, null);
                return;
            }
            //Слово прошло проверку на валидность и теперь нужно подсчитать и отразить результаты хода
            userScore+=strPool.length();
            L1.setText(""+userScore);
            String txt;
            txt=T1.getText();
            txt+=strPool+"\n";
            T1.setText(txt);
            for(Cell ctmp: pool)ctmp.set_style(Cell.NORMAL);
            focusCell=null;
            stat=1;
            pool.clear();
            strPool="";
            L3.setText(strPool);
            //Теперь нужно определить, выполнены ли безоговорочные условия окончания игры? Это:
            // - нет больше свободных ячеек
            int emptyCount;
            emptyCount=1;
            for(Cell[] c1: c)
                for(Cell c2: c1)if(c2.isEmpty())emptyCount++;
            if(emptyCount==0){
                endGame();
                return;
            }
        }
        
        //Если нажата клавиша Esc
        if(e.getKeyCode()==27){
            //В начале обрабатываем случай, когда производится сброс набранного слова
            if(focusCell!=null){
                focusCell.set_text(' ');
                focusCell.set_style(Cell.NORMAL);
                for(Cell ctmp: pool)ctmp.set_style(Cell.NORMAL);
                focusCell=null;
                stat=1;
                pool.clear();
                strPool="";
                L3.setText(strPool);
                return;
            }
            //Теперь обрабатываем случай пропуска хода
            if(focusCell==null){
                userPass++;
                String txt;
                txt=T1.getText();
                txt+="пропуск хода № "+userPass+"\n";
                T1.setText(txt);
                if(userPass==3){
                    endGame();
                    return;
                }
                for(Cell[] c1: c)
                    for(Cell c2: c1)c2.set_style(Cell.NORMAL);
            }
        }
    
        //Обрабатываем ответ компьютера
        Cell[] answer=null;
        answer=wrd.seeker(c);
        
        //Случай первый: компьютер пропустил ход
        if(answer==null){
            compPass++;
            String txt;
            txt=T2.getText();
            txt+=""+"пропуск хода № "+compPass;
            T2.setText(txt);
            if(compPass==3){
                endGame();
                return;
            }
            for(Cell[] c1: c)
                for(Cell c2: c1)c2.set_style(Cell.NORMAL);
        }
        
        //Случай второй: компьютер походил
        if(answer!=null){
            //Подсчитываем количество набранных им очков
            compScore+=answer.length;
            L2.setText(""+compScore);
            //Отражаем его ход на доске:
            String compString="";
            int direction;
            for(int i=0;i<answer.length;i++){
                compString+=""+answer[i].get_text();
                answer[i].set_style(Cell.SELECTED);
                if(i<(answer.length-1)){
                    direction=answer[i].get_direction(answer[i+1]);
                    answer[i].set_direction(direction);
                }
            }
            L3.setText(compString);
            String txt;
            txt=T2.getText();
            txt+=compString+"\n";
            T2.setText(txt);
            //Проверяем условия окончания игры
            int emptyCount;
            emptyCount=0;
            for(Cell[] c1: c)
                for(Cell c2: c1)if(c2.isEmpty())emptyCount++;
            if(emptyCount==0){
                endGame();
            }
            stat=0;
        }
        
    }
    
}
