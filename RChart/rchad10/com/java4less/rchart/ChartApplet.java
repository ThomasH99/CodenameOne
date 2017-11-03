//  RChart
//  Copyright (C)
//
//  RReport@Confluencia.net
//  All rights reserved
//
// Adquisition , use and distribution of this code is subject to restriction:
//  - You may modify the source code in order to adapt it to your needs.
//  - Redistribution of this ( or a modified version) source code is prohibited. You may only redistribute compiled versions.
//  - You may redistribute the compiled version as part of your application, not a new java component with the same purpose as this one.
//  - You may not remove this notice from the source code
//  - This notice disclaim all warranties of all material
//
package com.java4less.rchart;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;

public class ChartApplet extends Applet implements ChartListener,MouseMotionListener,MouseListener {
  private chartLoader loader;
  boolean stopped=false;

  // image for double buffering
  private java.awt.Image  tmpImage=null;

  // used to detect if it has been resized
  private int lastWidth=-1;
  private int lastHeight=-1;

  Cursor handCursor=new Cursor(Cursor.HAND_CURSOR);
  Cursor pointerCursor=new Cursor(Cursor.DEFAULT_CURSOR);

  // configuration for realtime applet
//++ REAL
  public long msecs=2000; // 2 second
  public String reloadFrom="";
  public boolean triggerJS=false;

//-- REAL

  //++ REAL
  private chartD deamon=null;
  //-- REAL

//++ TIPS
  private int cursorLastX=0;
  private int cursorLastY=0;
  private int lastSelectedPoint=-1;
  private String lastSelectedSerie="";
//-- TIPS

  //Construct the applet
  public ChartApplet() {
  }

  //Initialize the applet
  public void init() {

     stopped=false;
     loader= new chartLoader(this);



     // load runtime configuration
     String tmp="";
     //++ REAL
     tmp=this.getParameter("REALTIME_DATAFILE");
     if (tmp==null) tmp="";
     this.reloadFrom=tmp;

     tmp=this.getParameter("REALTIME_MSECS");
     if (tmp==null) tmp="2000";
     if (tmp.length()==0) tmp="2000";
     this.msecs=new Integer(tmp).longValue();
     this.triggerJS=(tmp.toUpperCase().compareTo("TRUE")==0);
     //-- REAL

     //++ JS
     tmp=this.getParameter("REALTIME_JS");
     if (tmp==null) tmp="";
     //-- JS


     // build chart
     loader.build(true,true);
  }



 public void update(Graphics g)
{
    paint(g);
}

public void paint(Graphics g)
{

  if ((loader.interactive) || (!loader.paintDirect))  super.paint(g);
  else {


          // if size changed force repaint
	    if ((this.lastWidth!=this.getSize().width) || ( this.lastHeight!=this.getSize().height)){
			 tmpImage=null;
                         this.lastWidth=this.getSize().width;
                         this.lastHeight=this.getSize().height;
	   }

	  if (tmpImage==null) {

	    loader.gChart.setSize(this.getSize());
	    tmpImage=this.createImage(this.getSize().width,this.getSize().height);
	    Graphics g2=tmpImage.getGraphics();
            loader.gChart.paint(g2);
	  }

	  g.drawImage(tmpImage,0,0,null);
  }
}



  //Component initialization
  private void jbInit() throws Exception {
  }

  //Start the applet
  public void start() {
    //System.out.println("start");
    if (stopped) init();
    stopped=false;

    //++ REAL
    if ((this.triggerJS) || (reloadFrom.length()>0)) {
      deamon=new chartD();
      deamon.applet=this;
      new Thread(deamon).start();
    }
    //-- REAL


  }

  //Stop the applet
  public void stop() {
      //System.out.println("stop");
      stopped=true;

      //++ REAL
      if (deamon!=null)  deamon.stop=true;
      deamon=null;
      //-- REAL
  }

  //Destroy the applet
  public void destroy() {
 // System.out.println("destroy");
 //++ REAL
       if (deamon!=null)  deamon.stop=true;
      deamon=null;
//-- REAL

  }

  //Get Applet information
  public String getAppletInfo() {
    return "Applet Information";
  }

  //Get parameter info
  public String[][] getParameterInfo() {
    String[][] pinfo =
      {
      {"Title", "String", ""},
      };
    return pinfo;
  }

  // Java script function
//++ JS
  public void rebuild(String Clear,String reReadFile) {

     boolean c=false;
     boolean r=false;

     if (Clear.compareTo("Y")==0)   c=true;
     if (reReadFile.compareTo("Y")==0)  r=true;

     loader.build(c,r);
     tmpImage=null;
  }
//-- JS

  public void loadFromFile(String psFile,String Clear) {

    boolean c=false;
    if (Clear.compareTo("Y")==0)  c=true;

    loader.loadFromFile(psFile,c);

  }

  public String getLoaderParameter(String param) {
    return loader.getParameter(param,"");
  }

  public void setParameter(String param,String value) {
     loader.setParameter(param,value);
  }


     // chart listener
 public void paintUserExit(Chart c,Graphics g) {
 // paint values on screen, where the cursor is

//++ TIPS
   if ((loader.showTips) && (c.selectedSerie.length()>0) &&  (c.selectedSeriePoint>=0)) {

     for (int i=0;i<loader.pSeriesNames.length;i++)
       if (loader.pSeriesNames[i]!=null)
         if (loader.pSeriesNames[i].compareTo(c.selectedSerie)==0)

           // serie found, get tip
           if (loader.tips.length>i)
            if (loader.tips[i]!=null)
              if (loader.tips[i].length>c.selectedSeriePoint)  {

              // now show tip

              // multiline
              String[] tip=loader.convertList(loader.tips[i][c.selectedSeriePoint],"\\n");

		 g.setFont(loader.tipFont);
		 // value
                 String val="" /*+(int) c.currentValueX+","*/ +(int) c.currentValueY ;
                 int he=g.getFontMetrics().getHeight() + 4;
                 int wi=4;

                 for (int h=0;h<tip.length;h++)
                  if ((g.getFontMetrics().stringWidth(tip[h])+ 4)>wi) wi=g.getFontMetrics().stringWidth(tip[h])+ 4;

		 g.setColor(loader.tipColor); // background
		 g.fillRect(c.currentX,c.currentY-(he*tip.length),wi,he*tip.length);
		 g.setColor(loader.tipFontColor); // border
		 g.drawRect(c.currentX,c.currentY-(he*tip.length),wi,he*tip.length);

                  for (int h=0;h<tip.length;h++)
         	      g.drawString(tip[h],c.currentX+2,c.currentY-4-(he*(tip.length-h-1)));


                  // do not draw posisiotn
                  return;

         }

   }
 //-- TIPS

//++ TIPS
    if (loader.showPosition)
	 if ((c.currentX>0) && (c.currentY>0)) {
		 g.setFont(loader.tipFont);
		 // value
                 String val="" /*+(int) c.currentValueX+","*/ +(int) c.currentValueY ;
                 int he=g.getFontMetrics().getHeight() + 4;
                 int wi=g.getFontMetrics().stringWidth(val)+4;

		 g.setColor(loader.tipColor); // background
		 g.fillRect(c.currentX,c.currentY-he,wi,he);
		 g.setColor(loader.tipFontColor); // border
		 g.drawRect(c.currentX,c.currentY-he,wi,he);
		 g.drawString(val,c.currentX+2,c.currentY-4);
	 }
//-- TIPS
 }

 public void mouseMoved(MouseEvent e) {

      // change cursor

    if ((loader.activateHtmlLinks) || (loader.triggerJavaScript)) {
      if (loader.gChart.selectedSerie.length()>0)  this.setCursor(handCursor);
      else  this.setCursor(pointerCursor);
    }


//++ TIPS
    //if ( (loader.gChart.selectedSerie.compareTo(lastSelectedSerie)!=0) || (loader.gChart.selectedSeriePoint!=lastSelectedPoint) || ( (loader.gChart.selectedSerie.length()>0) && ((Math.abs(loader.gChart.currentX-this.cursorLastX)>6) || (Math.abs(loader.gChart.currentY-this.cursorLastY)>6)))) {
    if ( (Math.abs(loader.gChart.currentX-this.cursorLastX)>2) || (Math.abs(loader.gChart.currentY-this.cursorLastY)>2)) {
      this.cursorLastX=loader.gChart.currentX;
      this.cursorLastY=loader.gChart.currentY;
      lastSelectedPoint=loader.gChart.selectedSeriePoint;
      lastSelectedSerie=loader.gChart.selectedSerie;
      if ((loader.showPosition) || (loader.showTips)) {

         if (loader.interactive)   loader.gChart.paint(loader.gChart.getGraphics());
         else {
           tmpImage=null; // in order  to force repainting
           paint(this.getGraphics());
          }
       }
   }
//-- TIPS

 }

 public void mouseExited(MouseEvent e) {
 }

 public void mouseEntered(MouseEvent e) {}
 public void mouseDragged(MouseEvent e) {}

 public void mousePressed(MouseEvent e) {
 }

 public void mouseReleased(MouseEvent e) {}

 public void mouseClicked(MouseEvent e) {

//++ TIPS
  for (int i=0;i<loader.pSeriesNames.length;i++)
       if (loader.pSeriesNames[i]!=null)
         if (loader.pSeriesNames[i].compareTo(loader.gChart.selectedSerie)==0)

           // serie found, get tip
           if (loader.htmlLinks.length>i)
            if (loader.htmlLinks[i]!=null)
              if (loader.htmlLinks[i].length>loader.gChart.selectedSeriePoint)
                if (loader.htmlLinks[i][loader.gChart.selectedSeriePoint].length()>0)
              try {
                this.getAppletContext().showDocument(new java.net.URL(loader.htmlLinks[i][loader.gChart.selectedSeriePoint]),loader.htmlLinkTarget);
                break;
              } catch (Exception e1) {System.out.println(e1.getMessage());}
//-- TIPS
//++ JS
   if (loader.triggerJavaScript)
     if (loader.gChart.selectedSerie.length()>0)
 	try {
		netscape.javascript.JSObject JS=netscape.javascript.JSObject.getWindow(this);

		Object[] o=new Object[2];
		o[0]=loader.gChart.selectedSerie;
                o[1]="" + loader.gChart.selectedSeriePoint;
                //System.out.println(""+gChart.selectedSeriePoint);
		JS.call("OnClickRChart",o);
	} catch (Exception e1) {System.out.println(e1.getMessage());}
//-- JS
 }
//++ REAL
 private class chartD implements Runnable {

   public boolean stop=false;
   public Applet applet;

   public void run() {

      while (!stop) {

         // trigger JS
//++   JS
         if (triggerJS) {
            try {
		netscape.javascript.JSObject JS=netscape.javascript.JSObject.getWindow(applet);
		JS.call("OnRChart",null);
	   } catch (Exception e1) {System.out.println(e1.getMessage());}
         }
//-- JS


         // new parameters
         if (reloadFrom.length()>0) {
            //System.out.println("loading "+reloadFrom);
            loadFromFile(reloadFrom,"N");
            loader.build(false,false);
            tmpImage=null;

         }

         try{
         Thread.currentThread().sleep(msecs);
         } catch (Exception e) {}


      }

   }
 }
//-- REAL
}