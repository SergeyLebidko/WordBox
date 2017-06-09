package wordbox_7;

import java.awt.*;
import javax.swing.*;

/**
 * Данный класс создает элементы интерфейса
 * @author Сергей Лебидко
 */
public class Pad implements Runnable{

    private final JFrame f;                   //Главное окно
    
    private final JPanel gameFieldPanel;      //Игровое поле
    
    private final JPanel userScorePanel;      //Панель вывода количества очков, набранных пользователем
    private final JPanel compScorePanel;      //Панель вывода количества очков, набранных компьютером
    
    private final JPanel userWordsPanel;      //Панель вывода ходов пользователя
    private final JPanel compWordsPanel;      //Панель вывода ходов компьютера
    
    private final JPanel movesPanel;          //Панель вывода набираемого игроком слова либо хода компьютера
    
    private final JPanel statusPanel;         //Панель вывода подсказки по клавишам
    
    //Обявляем константу - опорный размер. К ней в дальнейшем идет привязка опорных точек и размеров компонентов
    private final int SQUARE=7;
    
    //Ширина и высота главного окна
    private final int WIDTH_f=165*SQUARE;
    private final int HEIGHT_f=116*SQUARE;
      
    //Размер игрового поля (в пикселях). Предполагается, что игровое поле - квадратное
    private final int SIZE_gfp=100*SQUARE;
    
    //Положение игрового поля в главном окне
    private final int X_gfp=32*SQUARE;
    private final int Y_gfp=1*SQUARE;
    
    //Размер поля вывода очков игрока
    private final int WIDTH_usp=30*SQUARE;
    private final int HEIGHT_usp=5*SQUARE;
    
    //Положение поля вывода очков игрока
    private final int X_usp=1*SQUARE;
    private final int Y_usp=1*SQUARE;
    
    //Размер поля вывода ходов игрока
    private final int WIDTH_uwp=30*SQUARE;
    private final int HEIGHT_uwp=94*SQUARE;
    
    //Положение поля вывода ходов игрока
    private final int X_uwp=1*SQUARE;
    private final int Y_uwp=7*SQUARE;
    
    //Размер поля вывода очков компьютера
    private final int WIDTH_csp=30*SQUARE;
    private final int HEIGHT_csp=5*SQUARE;
    
    //Положение поля вывода очков компьютера
    private final int X_csp=133*SQUARE;
    private final int Y_csp=1*SQUARE;
    
    //Размер поля вывода ходов компьютера
    private final int WIDTH_cwp=30*SQUARE;
    private final int HEIGHT_cwp=94*SQUARE;
    
    //Положение поля вывода ходов компьютера
    private final int X_cwp=133*SQUARE;
    private final int Y_cwp=7*SQUARE;
    
    //Размер панели набираемого слова
    private final int WIDTH_mp=162*SQUARE;
    private final int HEIGHT_mp=5*SQUARE;
    
    //Положение панели набираемого слова
    private final int X_mp=1*SQUARE;
    private final int Y_mp=102*SQUARE;
    
    //Размер панели подсказки
    private final int WIDTH_stat=162*SQUARE;
    private final int HEIGHT_stat=3*SQUARE;
    
    //Положение панели подсказки
    private final int X_stat=1*SQUARE;
    private final int Y_stat=108*SQUARE;
    
    //Размер игрового поля "в ячейках". Предполагаем, что игровое поле - квадратное
    private final int cSize=5;
    
    //Ячейки игрового поля
    private final Cell[][] c;
                    
    private final JLabel userScoreLabel;    //Метка для вывода количества очков игрока
    private final JLabel compScoreLabel;    //Метка для вывода количества очков компьютера
    
    private final JTextArea userWordsList;  //Список слов, введенных пользователем
    private final JTextArea compWordsList;  //Список слов, введенных компьютером
    
    private final JLabel movesLabel;        //Метка для вывода текущего хода
    
    private Game g;
    
    Pad(){
        
        //Создаем главное окно и основные JPanel в нем
        f=new JFrame("WordBox");
        f.setLayout(null);
        //f.getContentPane().setBackground(new Color(120, 120, 120));
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(WIDTH_f, HEIGHT_f);
        f.setResizable(false);
        int xPos=Toolkit.getDefaultToolkit().getScreenSize().width/2-WIDTH_f/2;
        int yPos=Toolkit.getDefaultToolkit().getScreenSize().height/2-HEIGHT_f/2;
        f.setLocation(xPos, yPos);
        f.setIconImage((new ImageIcon("wsmall.png")).getImage());
                       
        gameFieldPanel=new JPanel();
        gameFieldPanel.setLayout(new GridLayout(cSize, cSize, 3, 3));
        gameFieldPanel.setBounds(X_gfp, Y_gfp, SIZE_gfp, SIZE_gfp);
        f.add(gameFieldPanel);
        
        userScorePanel=new JPanel();
        userScorePanel.setBounds(X_usp, Y_usp, WIDTH_usp, HEIGHT_usp);
        f.add(userScorePanel);
        
        userWordsPanel=new JPanel();
        userWordsPanel.setBounds(X_uwp, Y_uwp, WIDTH_uwp, HEIGHT_uwp);
        f.add(userWordsPanel);
        
        compScorePanel=new JPanel();
        compScorePanel.setBounds(X_csp, Y_csp, WIDTH_csp, HEIGHT_csp);
        f.add(compScorePanel);
        
        compWordsPanel=new JPanel();
        compWordsPanel.setBounds(X_cwp, Y_cwp, WIDTH_cwp, HEIGHT_cwp);
        f.add(compWordsPanel);
        
        movesPanel=new JPanel();
        movesPanel.setBounds(X_mp, Y_mp, WIDTH_mp, HEIGHT_mp);
        f.add(movesPanel);
        
        userScorePanel.setBorder(BorderFactory.createEtchedBorder());
        userWordsPanel.setBorder(BorderFactory.createEtchedBorder());
        
        compScorePanel.setBorder(BorderFactory.createEtchedBorder());
        compWordsPanel.setBorder(BorderFactory.createEtchedBorder());
        
        movesPanel.setBorder(BorderFactory.createEtchedBorder());
        
        //Создаем ячейки игрового поля
        c=new Cell[cSize][cSize];
        for(int i=0;i<cSize;i++)
            for(int j=0;j<cSize;j++){
                c[i][j]=new Cell(i,j,' ');
                c[i][j].setLayout(null);
                gameFieldPanel.add(c[i][j]);
            }
        
        //Создаем элементы интерфейса, которые будут использоваться для вывода информации в текущей партии - метки, панели прокрутки и текстовые поля
        userScoreLabel=new JLabel("0");
        userScorePanel.add(userScoreLabel);
        
        compScoreLabel=new JLabel("0");
        compScorePanel.add(compScoreLabel);
        
        userWordsList=new JTextArea("");
        compWordsList=new JTextArea("");
        
        userWordsList.setEditable(false);
        compWordsList.setEditable(false);
        
        JScrollPane spUser;
        JScrollPane spComp;
        
        spUser=new JScrollPane(userWordsList);
        spComp=new JScrollPane(compWordsList);
        
        spUser.setPreferredSize(new Dimension(userWordsPanel.getWidth()-2*SQUARE, userWordsPanel.getHeight()-2*SQUARE));
        spComp.setPreferredSize(new Dimension(compWordsPanel.getWidth()-2*SQUARE, compWordsPanel.getHeight()-2*SQUARE));
        
        userWordsPanel.add(spUser);
        compWordsPanel.add(spComp);
        
        movesLabel=new JLabel("");
        movesPanel.add(movesLabel);
        
        statusPanel=new JPanel(new BorderLayout());
        statusPanel.setBounds(X_stat, Y_stat, WIDTH_stat, HEIGHT_stat);
        JLabel statusLabel;
        statusLabel=new JLabel("Enter - сделать ход. Esc - отменить ввод слова или пропустить ход.");
        statusLabel.setForeground(new Color(100,100,100));
        statusPanel.add(statusLabel,BorderLayout.WEST);
        f.add(statusPanel);
        
        g=new Game(f, userScoreLabel, compScoreLabel, userWordsList, compWordsList, movesLabel, c);
              
        g.StartEventsListeners();
        
        f.setVisible(true);
    }
    
    @Override
    public void run() {
       
    }
    
}