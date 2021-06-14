package Util.ImageUtil;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 *
 * @author
 *
 */
public class Histogram extends JFrame implements ChangeListener, ActionListener{
    /**
     * 头像界面的宽度
     */
    public static final int WIDTH = 680;
    /**
     * 头像界面的高度
     */
    public static final int HEIGHT = 400;
    /**
     * 显示图片版面的宽度
     */
    public static final int PWIDTH = 350;
    /**
     * 显示图片版面的高度
     */
    public static final int PHEIGHT = 300;
    /**
     * 直方图版面的宽度
     */
    public static final int H_WIDTH = 350;
    /**
     * 直方图片版面的高度
     */
    public static final int H_HEIGHT = 300;
    /**
     * 滑块的最大值
     */
    public static final int JS_MAXIMUM = 100;

    public static final String FRAMTITLE = "图像的灰度直方图";
    private String imgSrc;
    Image img;
    JSlider jsliderH, jsliderV;	//水平和垂直滑块
    JPanel uP, picP, uplodP, histP;
    JButton openFile, histogram, threshold;
    MyCanvas canvas;
    Canvas histCanvas;
    int imgW = PWIDTH, imgH = PHEIGHT;
    int xcentre = PWIDTH/2, ycentre = PHEIGHT/2;
    private int dx1 = xcentre-imgW/2, dy1 = ycentre-imgH/2, dx2 = xcentre + imgW/2, dy2 = ycentre + imgH/2;
    private int sx1 = 0, sy1 = 0, sx2, sy2;
    private float shx = 0, shy = 0;
    /**
     * 构造函数
     */
    public Histogram() {
        setTitle(FRAMTITLE);
        launchDialog();
    }

    /**
     * 返回canvas
     * @return
     */
    public Canvas getCanvas() {
        return canvas;
    }
    /**
     * 界面设计
     */
    private void launchDialog() {
        //初始化图片对象
        imgSrc = "F:\\image processing\\baboom2_gray.jpg";
        img = Toolkit.getDefaultToolkit().getImage(imgSrc);
        //初始化组件
        canvas = new MyCanvas();
        jsliderH = new JSlider();
        jsliderH.setMaximum(JS_MAXIMUM);
        jsliderH.setValue(JS_MAXIMUM/2);
        jsliderH.setMinimum(1);
        jsliderH.setOrientation(JSlider.HORIZONTAL);
        jsliderH.addChangeListener(this);
        jsliderV = new JSlider();
        jsliderV.setMaximum(JS_MAXIMUM);
        jsliderV.setValue(JS_MAXIMUM/2);
        jsliderV.setMinimum(1);
        jsliderV.setOrientation(JSlider.VERTICAL);
        jsliderV.addChangeListener(this);
        picP = new JPanel();
        picP.setPreferredSize(new Dimension(PWIDTH, PHEIGHT));
        //picP.setBackground(Color.green);
        uP = new JPanel();
        uplodP = new JPanel();
        openFile = new JButton("打开图片");
        histogram = new JButton("显示直方图");
        threshold = new JButton("显示二值图像");
        openFile.addActionListener(this);
        histogram.addActionListener(this);
        threshold.addActionListener(this);
        //添加组件
        picP.setLayout(new BorderLayout());
        picP.add(canvas, BorderLayout.CENTER);
        uP.setLayout(new BorderLayout());
        uP.add(picP, BorderLayout.CENTER);
        uP.add(jsliderH, BorderLayout.SOUTH);
        uP.add(jsliderV, BorderLayout.EAST);

        histCanvas = new Canvas();
        histP = new JPanel(new BorderLayout());
        histP.add(histCanvas);
        histP.setPreferredSize(new Dimension(H_WIDTH, H_HEIGHT));
        histP.setBorder(new LineBorder(Color.blue));
        //System.out.println("w:" + histP.getWidth() + "  h:" + histP.getHeight() + " " + histP.HEIGHT);

        uplodP.setLayout(new FlowLayout());
        uplodP.add(openFile);
        uplodP.add(histogram);
        uplodP.add(threshold);
        Container c = getContentPane();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        //c.add(uP);
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.add(uP);
        p.add(histP);
        c.add(p);
        c.add(uplodP);
        setSize(WIDTH, HEIGHT);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    public void drawHistogram() {
        try {
            BufferedImage bfImg = ImageIO.read(new File(imgSrc));
            int w = bfImg.getWidth();
            int h = bfImg.getHeight();
            int pix[] = new int[w*h];
            int hist[] = new int[256];
			/*for(int i=0; i<hist.length; i++) {
				hist[i] = 0;
			}*/
            int imgType = bfImg.getType();
            int temp;
            bfImg.getRGB(0, 0, w, h, pix, 0, w);
            ColorModel cm = ColorModel.getRGBdefault();
            for(int i=0; i<pix.length; i++) {
				/*for(int j=0; j<hist.length; j++) {
					if(j ==  cm.getRed(pix[i])) {
						hist[j] ++;
					}
				}*/
                temp = cm.getRed(pix[i]);
                hist[temp] ++;
            }
            //System.out.println(hist.length);

            int max = 0;
            for(int i=0; i<hist.length; i++) {
                if(hist[i] > max) {
                    max = hist[i];
                }

            }
            for(int i=0; i<hist.length; i++) {
                hist[i] = (int)(hist[i]/(float)max * 250);
				/*System.out.print(hist[i] + "\t");
				if(i%10 == 0) {
					System.out.println();
				}*/
            }
            //histCanvas.setHistPix(hist);
            //histCanvas.repaint();

            Graphics g = histCanvas.getGraphics();
            Color c = g.getColor();
            g.setColor(Color.red);
            g.drawLine(10, H_HEIGHT-10, H_WIDTH-30, H_HEIGHT-10);
            g.drawLine(H_WIDTH-35, H_HEIGHT-15, H_WIDTH-30, H_HEIGHT-10);
            g.drawLine(H_WIDTH-35, H_HEIGHT-5, H_WIDTH-30, H_HEIGHT-10);
            g.drawString("灰度级", H_WIDTH-80, H_HEIGHT);
            g.drawLine(10,  H_HEIGHT-10, 10, 10);
            g.drawLine(5, 15, 10, 10);
            g.drawLine(15, 15, 10, 10);
            g.drawString("像素个数", 15, 15);
            g.setColor(Color.black);
            for(int i=0; i<hist.length; i++) {
                g.drawLine(10+i, H_HEIGHT-10, 10+i, H_HEIGHT-10-hist[i]);
                if(i%30 == 0) {
                    g.drawString(i+"", 10+i, H_HEIGHT);
                }
            }
            g.setColor(c);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void threshold(int threshold) {
        try {
            BufferedImage bfImg = ImageIO.read(new File(imgSrc));
            int w = bfImg.getWidth();
            int h = bfImg.getHeight();
            int pix[] = new int[w*h];

            int imgType = bfImg.getType();
            bfImg.getRGB(0, 0, w, h, pix, 0, w);
            int max = 0;
            ColorModel cm = ColorModel.getRGBdefault();
            for(int i=0; i<pix.length; i++) {
                if(cm.getRed(pix[i]) <= threshold) {
                    pix[i] = new Color(255,255,255).getRGB();
                } else {
                    pix[i] = new Color(0, 0, 0).getRGB();
                }
            }
            bfImg.setRGB(0, 0, w, h, pix, 0, w);

            //histCanvas.setHistPix(hist);
            //histCanvas.repaint();

            Graphics g = histCanvas.getGraphics();
            g.clearRect(0, 0, H_WIDTH, H_HEIGHT);
            Color c = g.getColor();
            g.drawImage(bfImg, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, this);
            g.setColor(c);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 事件监听响应
     */
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == openFile) {
            FileDialog openFileDialog = new FileDialog(this, "打开图片");
            openFileDialog.setMode(FileDialog.LOAD);	//设置此对话框为从文件加载内容
            openFileDialog.setFile("*.jpg;*.jpeg;*.gif;*.png;*.bmp;*.tif;");		//设置可打开文件的类型为：.txt,.java

            openFileDialog.setVisible(true);
            String fileName = openFileDialog.getFile();
            String directory = openFileDialog.getDirectory();
            if(null != fileName) {
                imgSrc = directory + fileName;
                img = Toolkit.getDefaultToolkit().getImage(imgSrc);
                histCanvas.repaint();
            } else {
                JOptionPane.showMessageDialog(this, "您已经取消选择了，请重新选择!");
            }
        } else if(e.getSource() == histogram) {
            System.out.println(new Date());
            drawHistogram();
            System.out.println(new Date());
        } else if(e.getSource() == threshold) {
            threshold(65);
        }
    }
    /**
     * 滑动条滑动响应事件
     */
    public void stateChanged(ChangeEvent e) {
        if(e.getSource() == jsliderH) {
            float valueH = jsliderH.getValue();
            imgW = (int)(2*PWIDTH*(valueH/JS_MAXIMUM));
            if(imgW < PWIDTH/4) {
                imgW = PWIDTH/4;
            }
            dx1 = xcentre-imgW/2;
            dy1 = ycentre-imgH/2;
            dx2 = xcentre + imgW/2;
            dy2 = ycentre + imgH/2;
            canvas.repaint();
        } else if(e.getSource() == jsliderV) {
            float valueV = jsliderV.getValue();
            imgH = (int)(2*PHEIGHT*(valueV/JS_MAXIMUM));
            if(imgH < PHEIGHT/4) {
                imgH = PHEIGHT/4;
            }
            dx1 = xcentre-imgW/2;
            dy1 = ycentre-imgH/2;
            dx2 = xcentre + imgW/2;
            dy2 = ycentre + imgH/2;
            canvas.repaint();
        }
    }

    public static void main(String[] args) {
        new Histogram();
    }

    /**
     * 用于画图像的Canvas
     */
    class MyCanvas extends Canvas {
        public MyCanvas() {

        }
        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            //g.drawImage(img, xcentre-imgW/2, ycentre-imgH/2, imgW, imgH, this);
            sx2  = img.getWidth(this);
            sy2  = img.getHeight(this);
            g2.shear(shx, shy);
            g2.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, this);//Color.green,
        }
    }

}

/*class HistCanvas extends Canvas {
int histPix[] = new int[0];
public void setHistPix(int[] pix) {
	histPix = pix;
}
public void paint(Graphics g) {
	Color c = g.getColor();
	g.setColor(Color.red);
	g.drawLine(10, H_HEIGHT-10, H_WIDTH-30, H_HEIGHT-10);
	g.drawLine(H_WIDTH-35, H_HEIGHT-15, H_WIDTH-30, H_HEIGHT-10);
	g.drawLine(H_WIDTH-35, H_HEIGHT-5, H_WIDTH-30, H_HEIGHT-10);
	g.drawString("灰度级", H_WIDTH-80, H_HEIGHT);
	g.drawLine(10,  H_HEIGHT-10, 10, 10);
	g.drawLine(5, 15, 10, 10);
	g.drawLine(15, 15, 10, 10);
	g.drawString("像素个数", 15, 15);
	g.setColor(Color.black);
	for(int i=0; i<histPix.length; i++) {
		g.drawLine(10+i, H_HEIGHT-10, 10+i, H_HEIGHT-10-histPix[i]);
		if(i%30 == 0) {
			g.drawString(i+"", 10+i, H_HEIGHT);
		}
	}
	g.setColor(c);
}
}*/
