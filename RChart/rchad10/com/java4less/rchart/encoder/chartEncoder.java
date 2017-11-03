package com.java4less.rchart.encoder;

public class chartEncoder {

   com.java4less.rchart.Chart cha;
   String sFile;
   String sFormat;

  public chartEncoder(com.java4less.rchart.Chart c,String psFile,String psFormat) {

     cha=c;
     sFormat=psFormat;
     sFile=psFile;
  }


  public boolean encode() {

    if (sFormat.toUpperCase().compareTo("GIF")==0) return saveToGIF(cha,sFile);
    if (sFormat.toUpperCase().compareTo("JPEG")==0) return saveToJPEG(cha,sFile);
    if (sFormat.toUpperCase().compareTo("PNG")==0) return saveToPNG(cha,sFile);

    return false;
  }

   public boolean saveToGIF(com.java4less.rchart.Chart c,String psFile) {


           try {

            // download and install gif encoder from this address:
            // http://www.acme.com/resources/classes/Acme.tar.gz

            //create bufferred image
            java.awt.image.BufferedImage image = new java.awt.image.BufferedImage( c.getSize().width,c.getSize().height,java.awt.image.BufferedImage.TYPE_BYTE_INDEXED );
            //java.awt.Image image=c.createImage(c.getSize().width,c.getSize().height);
            java.awt.Graphics imgGraphics = image.createGraphics();

             c.paint(imgGraphics );

            // open file
            java.io.File f=new java.io.File(psFile);
            f.delete();
            java.io.FileOutputStream of=new java.io.FileOutputStream(f);

            // encode buffered image to a gif

            Class enClass=Class.forName("Acme.JPM.Encoders.GifEncoder");
            Class[] constructorParams = new Class[2];
            constructorParams[0]=Class.forName("java.awt.Image");
            constructorParams[1]=Class.forName("java.io.OutputStream");
            Object[] constructorObj = new Object[2];
            constructorObj[0]=image;
            constructorObj[1]=of;

            Object encoder= enClass.getConstructor(constructorParams).newInstance(constructorObj);

            Class[] encodeParams = new Class[0];
            Object[] encodeObj = new Object[0];

            enClass.getMethod("encode",encodeParams).invoke(encoder,encodeObj);


            //Acme.JPM.Encoders.GifEncoder encoder = new Acme.JPM.Encoders.GifEncoder(image,of);
            //encoder.encode();
            of.close();

            imgGraphics =null;
            image=null;
            of=null;

            }
            catch (Exception e) {
                        System.out.println(e.getMessage());
            return false;}

            return true;
        }


        public boolean saveToJPEG(com.java4less.rchart.Chart c,String psFile) {


           try {

            // create bufferred image
            java.awt.image.BufferedImage image = new java.awt.image.BufferedImage( c.getSize().width,c.getSize().height,java.awt.image.BufferedImage.TYPE_INT_RGB );
            java.awt.Graphics imgGraphics = image.createGraphics();

             c.paint(imgGraphics );

            // open file
            java.io.File f=new java.io.File(psFile);
            f.delete();
            java.io.FileOutputStream of=new java.io.FileOutputStream(f);

            // encode buffered image to a jpeg
            com.sun.image.codec.jpeg.JPEGImageEncoder encoder = com.sun.image.codec.jpeg.JPEGCodec.createJPEGEncoder(of );
            encoder.encode( image );

            of.close();

            imgGraphics =null;
            image=null;
            of=null;

            }
            catch (Exception e) {
            System.out.println(e.getMessage());
            return false;}

            return true;

        }

  public boolean saveToPNG(com.java4less.rchart.Chart c,String psFile) {

           // before you use the PNG encoder you must install the com.bigfoot.bugar.image.PNGEncoder encoder
           // download from http://users.boone.net/wbrameld/pngencoder/

           try {


            // create bufferred image
            java.awt.image.BufferedImage image = new java.awt.image.BufferedImage( c.getSize().width,c.getSize().height,java.awt.image.BufferedImage.TYPE_INT_RGB );
            java.awt.Graphics imgGraphics = image.createGraphics();

             c.paint(imgGraphics );

            // open file
            java.io.File f=new java.io.File(psFile);
            f.delete();
            java.io.FileOutputStream of=new java.io.FileOutputStream(f);

            // encode buffered image to a png
            // download png encoder from http://users.boone.net/wbrameld/pngencoder/
            Class enClass=Class.forName("com.bigfoot.bugar.image.PNGEncoder");
            Class[] constructorParams = new Class[2];
            constructorParams[0]=Class.forName("java.awt.Image");
            constructorParams[1]=Class.forName("java.io.OutputStream");
            Object[] constructorObj = new Object[2];
            constructorObj[0]=image;
            constructorObj[1]=of;

            Object encoder= enClass.getConstructor(constructorParams).newInstance(constructorObj);

            Class[] encodeParams = new Class[0];
            Object[] encodeObj = new Object[0];

            enClass.getMethod("encodeImage",encodeParams).invoke(encoder,encodeObj);

            //com.bigfoot.bugar.image.PNGEncoder en= new com.bigfoot.bugar.image.PNGEncoder(image,of);
            //en.encodeImage();
            of.close();

            imgGraphics =null;
            image=null;
            of=null;

            }
            catch (Exception e) {
            System.out.println(e.getMessage());
            return false;}

            return true;

        }


}