package wordbox_7;

import java.awt.*;
import java.awt.font.*;
import javax.swing.*;

/**
 * Данный класс нужен для описания отдельных ячеек игрового поля
 * @author Сергей Лебидко
 */
public class Cell extends JPanel{
    
    static final int NORMAL=1;    //Номер стиля для обычной ячейки
    static final int SELECTED=2;  //Номер стиля для выбранной ячейки
    static final int FOCUS=3;     //Номер стиля для ячейки, в которую ожидаем ввод буквы
        
    static final int UP=1;            //Направление - вверх
    static final int RIGHT=2;         //Направление - вправо
    static final int DOWN=3;          //Направление - вниз
    static final int LEFT=4;          //Направление - влево
        
    private final int x;          //x-координата ячейки на игровом поле (номер столбца)
    private final int y;          //y-координата ячейки на игровом поле (номер строки)
    private char text;            //Символ в ячейке
    private int style;            //Стиль ячейки
    private int direction;        //Направление стрелки
        
    Cell(int y, int x, char text){
        this.y=y;
        this.x=x;
        this.text=text;
        this.style=NORMAL;
    }
    
    public int get_X(){
        return x;
    }
    
    public int get_Y(){
        return y;
    }
    
    public char get_text(){
        return text;
    }
    
    public void set_text(char tx){
        text=tx;
        repaint();
    }
       
    public void set_style(int st){
        if(st!=NORMAL & st!=FOCUS & st!=SELECTED)return;
        style=st;
        repaint();
    }
    
    public boolean isEmpty(){
        return text==' ';
    }
    
    public boolean isFull(){
        return text!=' ';
    }
    
    public boolean isNear(Cell c){
        return ( Math.abs(x-c.get_X())+Math.abs(y-c.get_Y()) )==1;
    }
    
    public void set_direction(int direction){
        this.direction=direction;
        repaint();
    }
    
    public int get_direction(Cell c){
        int x0, y0;
        int x1, y1;
        int dx, dy;
        x0=x;
        y0=y;
        x1=c.get_X();
        y1=c.get_Y();
        dx=x1-x0;
        dy=y1-y0;
        if((dx==0) & (dy==-1))return UP;
        if((dx==1) & (dy==0))return RIGHT;
        if((dx==0) & (dy==1))return DOWN;
        if((dx==-1) & (dy==0))return LEFT;
        return 0;
    }
    
    @Override
    public void paint(Graphics g){
            
        Graphics2D g2;
        g2=(Graphics2D)g;
                       
        int w=this.getSize().width;
        int h=this.getSize().height;
                  
        switch(style){
            case NORMAL:{
               g2.setColor(new Color(200,200,200));
               Shape sh=new Rectangle(0, 0, w, h);
               g2.fill(sh); 
               break;
            }
            case FOCUS:{
               g2.setColor(new Color(250,210,210));
               Shape sh=new Rectangle(0, 0, w, h);
               g2.fill(sh); 
               break;
            }
            case SELECTED:{
                g2.setColor(new Color(210,250,250));
               Shape sh=new Rectangle(0, 0, w, h);
               g2.fill(sh);
            }
        }
         
        String str;
        str=(new Character(text)).toString();
        
        int wstr, hstr;
        int xstr, ystr;
        
        Font f;
        f=new Font("Times New Roman", Font.PLAIN, (int)(h*0.6));
        
        FontRenderContext frc;
        frc=g2.getFontRenderContext();
        
        LineMetrics lm;
        lm=f.getLineMetrics(str, frc);
        
        wstr=(int)f.getStringBounds(str, frc).getWidth();
        hstr=(int)f.getStringBounds(str, frc).getHeight();
        xstr=(int)( w/2-wstr/2 );
        ystr=(int)( h/2+hstr/2 )-(int)lm.getDescent()-(int)lm.getLeading();
               
        g2.setFont(f);
        g2.setColor(Color.BLACK);
        g2.drawString(str, xstr, ystr);
        
        if(direction!=UP & direction!=RIGHT & direction!=DOWN & direction!=LEFT)return;
        int dx=w/10;
        int dy=h/10;
        
        switch(direction){
            case UP:{
                g2.setPaint(Color.BLACK);
                g2.drawLine(5*dx, 1*dy, 6*dx, 2*dy);
                g2.drawLine(6*dx, 2*dy, 4*dx, 2*dy);
                g2.drawLine(4*dx, 2*dy, 5*dx, 1*dy);
                direction=0;
                break;
            }
            case RIGHT:{
                g2.setPaint(Color.BLACK);
                g2.drawLine(9*dx, 5*dy, 8*dx, 6*dy);
                g2.drawLine(8*dx, 6*dy, 8*dx, 4*dy);
                g2.drawLine(8*dx, 4*dy, 9*dx, 5*dy);
                direction=0;
                break;
            }
            case DOWN:{
                g2.setPaint(Color.BLACK);
                g2.drawLine(5*dx, 9*dy, 4*dx, 8*dy);
                g2.drawLine(4*dx, 8*dy, 6*dx, 8*dy);
                g2.drawLine(6*dx, 8*dy, 5*dx, 9*dy);
                direction=0;
                break;
            }
            case LEFT:{
                g2.setPaint(Color.BLACK);
                g2.drawLine(1*dx, 5*dy, 2*dx, 4*dy);
                g2.drawLine(2*dx, 4*dy, 2*dx, 6*dy);
                g2.drawLine(2*dx, 6*dy, 1*dx, 5*dy);
                direction=0;
            }
        }   
        
    }

}