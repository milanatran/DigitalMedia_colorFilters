package uebung3;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
     Opens an image window and adds a panel below the image
 */
public class GLDM_U3_s0577618 implements PlugIn {

	ImagePlus imp; // ImagePlus object
	private int[] origPixels;
	private int width;
	private int height;

	String[] items = {"Original", "Rot-Kanal", "Negativ", "Graustufen", "Binärbild", "Graustufen 5", "Graustufen 10", "Binär mit horiz. Fehlerdiff.", "Sepia", "6 Farben"};


	public static void main(String args[]) {

		IJ.open("https://imi-gdm.github.io/uebungen/uebung3/Bear.jpg");
		//IJ.open("Z:/Pictures/Beispielbilder/orchid.jpg");

		GLDM_U3_s0577618 pw = new GLDM_U3_s0577618();
		pw.imp = IJ.getImage();
		pw.run("");
	}

	public void run(String arg) {
		if (imp==null) 
			imp = WindowManager.getCurrentImage();
		if (imp==null) {
			return;
		}
		CustomCanvas cc = new CustomCanvas(imp);

		storePixelValues(imp.getProcessor());

		new CustomWindow(imp, cc);
	}


	private void storePixelValues(ImageProcessor ip) {
		width = ip.getWidth();
		height = ip.getHeight();

		origPixels = ((int []) ip.getPixels()).clone();
	}


	class CustomCanvas extends ImageCanvas {

		CustomCanvas(ImagePlus imp) {
			super(imp);
		}

	} // CustomCanvas inner class


	class CustomWindow extends ImageWindow implements ItemListener {

		private String method;
		
		CustomWindow(ImagePlus imp, ImageCanvas ic) {
			super(imp, ic);
			addPanel();
		}

		void addPanel() {
			//JPanel panel = new JPanel();
			Panel panel = new Panel();

			JComboBox cb = new JComboBox(items);
			panel.add(cb);
			cb.addItemListener(this);

			add(panel);
			pack();
		}

		public void itemStateChanged(ItemEvent evt) {

			// Get the affected item
			Object item = evt.getItem();

			if (evt.getStateChange() == ItemEvent.SELECTED) {
				System.out.println("Selected: " + item.toString());
				method = item.toString();
				changePixelValues(imp.getProcessor());
				imp.updateAndDraw();
			} 

		}


		private void changePixelValues(ImageProcessor ip) {

			// Array zum Zurückschreiben der Pixelwerte
			int[] pixels = (int[])ip.getPixels();

			if (method.equals("Original")) {

				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						
						pixels[pos] = origPixels[pos];
					}
				}
			}
			
			if (method.equals("Rot-Kanal")) {

				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						int argb = origPixels[pos];  // Lesen der Originalwerte 

						int r = (argb >> 16) & 0xff;
						//int g = (argb >>  8) & 0xff;
						//int b =  argb        & 0xff;

						int rn = r;
						int gn = 0;
						int bn = 0;

						// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

						pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
					}
				}
			}
			
			if (method.equals("Negativ")) {

				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						int argb = origPixels[pos];  // Lesen der Originalwerte 

						int r = (argb >> 16) & 0xff;
						int g = (argb >>  8) & 0xff;
						int b =  argb        & 0xff;
						
						int rn = 255 - r;
						int gn = 255 - g;
						int bn = 255 - b;

						// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

						pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
					}
				}
			}
			
			if (method.equals("Graustufen")) {

				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						int argb = origPixels[pos];  // Lesen der Originalwerte 

						int r = (argb >> 16) & 0xff;
						int g = (argb >>  8) & 0xff;
						int b =  argb        & 0xff;
						
						int d = (r+g+b)/3; //Durchschnittswert
						int rn = d;
						int gn = d;
						int bn = d;

						// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

						pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
					}
				}
			}
			
			if (method.equals("Binärbild")) {

				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						int argb = origPixels[pos];  // Lesen der Originalwerte 

						int r = (argb >> 16) & 0xff;
						int g = (argb >>  8) & 0xff;
						int b =  argb        & 0xff;
						
						int d = (r+g+b)/3; //Durchschnittswert
						int rn = d;
						int gn = d;
						int bn = d;
						
						int bw = 255 / 2; //mit dem int entscheiden ob Pixel schwarz wird oder weiß
						
						if(d < bw) {
							rn = 0;
							gn = 0;
							bn = 0;
						} else {
							rn = 255;
							gn = 255;
							bn = 255;
						}

						// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

						pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
					}
				}
			}
			
			
			if (method.equals("Graustufen 5")) {

				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						int argb = origPixels[pos];  // Lesen der Originalwerte 

						int r = (argb >> 16) & 0xff;
						int g = (argb >>  8) & 0xff;
						int b =  argb        & 0xff;
						
						int m = (r+g+b)/3; //Durchschnitt
						int stufen = 255/4; //in 5 Graustufen geteilt
						int d = m / stufen * stufen; //wird gerundet beim Teilen wegen int
						int rn = d;
						int gn = d;
						int bn = d;

						// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

						pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
					}
				}
			}
			
			
			if (method.equals("Graustufen 10")) {

				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						int argb = origPixels[pos];  // Lesen der Originalwerte 

						int r = (argb >> 16) & 0xff;
						int g = (argb >>  8) & 0xff;
						int b =  argb        & 0xff;
						
						int m = (r+g+b)/3; // Durchschnitt
						int stufen = 255/9; //in 10 Graustufen geteilt
						int d = m / stufen * stufen; //wird gerundet beim Teilen wegen int
						int rn = d;
						int gn = d;
						int bn = d;

						// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

						pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
					}
				}
			}
			
			//Quelle: https://youtu.be/0t8BHaLsXTM
			if (method.equals("Binär mit horiz. Fehlerdiff.")) {

				for (int y=0; y<height; y++) {
					int error = 0;
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						int argb = origPixels[pos];  // Lesen der Originalwerte 

						int r = (argb >> 16) & 0xff;
						int g = (argb >>  8) & 0xff;
						int b =  argb        & 0xff;
						
						int d = (r+g+b)/3 + error; //Durchschnittswert plus dem Fehler des letzten Pixels
						int rn = d;
						int gn = d;
						int bn = d;
						
						int bw = 255 / 2; //mit dem int entscheiden ob Pixel schwarz wird oder weiß
						
						if(d < bw) {
							rn = 0;
							gn = 0;
							bn = 0;
							error = d;
						} else {
							rn = 255;
							gn = 255;
							bn = 255;
							error = d - 255;
						}
						
						// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

						pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
					}
				}
			}
			
			//Quelle: https://www.tutorialspoint.com/how-to-convert-a-colored-image-to-sepia-image-using-java-opencv-library
			if (method.equals("Sepia")) {

				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						int argb = origPixels[pos];  // Lesen der Originalwerte 

						int r = (argb >> 16) & 0xff;
						int g = (argb >>  8) & 0xff;
						int b =  argb        & 0xff;
						
						int sepia = 20;
						int d = (r+g+b)/3; //Durchschnittswert
						int rn = d + (sepia * 2);
						int gn = d + sepia;
						int bn = d - 30;
						
						rn = Math.min(255, Math.max(0, rn));
						gn = Math.min(255, Math.max(0, gn));
						bn = Math.min(255, Math.max(0, bn));
						
						// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

						pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
					}
				}
			}
			
			if (method.equals("6 Farben")) {

				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						int argb = origPixels[pos];  // Lesen der Originalwerte 

						int r = (argb >> 16) & 0xff;
						int g = (argb >>  8) & 0xff;
						int b =  argb        & 0xff;
						
						//6 Farben herausgesucht mit https://imagecolorpicker.com/
						//rgb(156,148,132) mittelbraun
						//rgb(212,204,212) rosa-braun
						//rgb(100,92,84) dunkelbraun
						//rgb(44,100,140) blau
						//rgb(108,132,164) hellblau
						//rgb(36,36,36) dunkelgrau, fast schwarz
						
						int rn = r;
						int gn = g;
						int bn = b;
						
						if(rn < 80 && bn < 60) {
							//schwarz/ grau
							rn = 36;
							gn = 36;
							bn = 36;
						} else if(rn < 100 && gn >= 100) {
							//blau
							rn = 44;
							gn = 100;
							bn = 140;
						} else if(rn < 140 && bn < 130) {
							// braun
							rn = 100;
							gn = 92;
							bn = 84;
						} else if(rn < 140 && bn < 190) {
							//hellblau
							rn = 108;
							gn = 132;
							bn = 164;
						} else if(rn < 200 && gn < 170) {
							//mittelbraun
							rn = 156;
							gn = 148;
							bn = 132;
						} else {
							//rosa-braun
							rn = 212;
							gn = 204;
							bn = 212;
						}
						// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

						pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
					}
				}
			}


	} // CustomWindow inner class
		
		
	} 
}
