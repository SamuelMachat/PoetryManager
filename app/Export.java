/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;

/**
 *
 * @author samac
 */
public class Export {

    public static void exportPoemsToTextFile(List<Poem> poems, int first, int last) throws UnsupportedEncodingException, FileNotFoundException {
        for (Poem poem : poems) {
            System.out.println("experimenting...");
            String poemTextText = "";
            BufferedReader br;
            String line;
            String path = poem.getPath();
            try {
                if (!path.endsWith(".poem")) { //path.endsWith(".pdf") || path.endsWith(".doc") || path.endsWith(".odt") || path.endsWith(".rtf") || path.endsWith(".docx")

                    poemTextText = "Báseò z daného souboru NEJDE ZOBRAZIT.";
                } else {
                    br = Export.loadFileUTF8(path);
                    try {
                        while ((line = br.readLine()) != null) {
                            poemTextText += line + "\n";
                        }
                        br.close();
                    } catch (IOException ioe) {
                        poemTextText = "BÁSEÒ NEJDE ZOBRAZIT.";
                    }
                }
            } catch (FileNotFoundException ex) {
                poemTextText = "BÁSEÒ NEJDE ZOBRAZIT.";
            } finally {
                String outputFile = "file.txt";
                attachTextToFile(poemTextText, outputFile);
            }
        }
    }

    private static void attachTextToFile(String text, String file) throws FileNotFoundException, UnsupportedEncodingException {
        BufferedWriter bw;
        bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8"));
        try {
            text = text + "\n\n<durangavoe>\n\n";
            bw.append(text);
            //bw.append("\n\n<durangavoe>\n\n");
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void exportAsImageOneLine(Poem book) {
        // vytvoøit obrázek
        String text = "";
        String path = book.getPath();
        try {
            if (!path.endsWith(".poem")) { //path.endsWith(".pdf") || path.endsWith(".doc") || path.endsWith(".odt") || path.endsWith(".rtf") || path.endsWith(".docx")

                text = "Báseò z daného souboru NEJDE ZOBRAZIT.";
            } else {
                String line;
                BufferedReader br;
                try {
                    br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
                    try {
                        while ((line = br.readLine()) != null) {
                            text += line + "\n";
                        }
                        br.close();
                    } catch (IOException ioe) {
                        text = "BÁSEÒ NEJDE ZOBRAZIT.";
                    }
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(Export.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        } catch (FileNotFoundException ex) {
            text = "BÁSEÒ NEJDE ZOBRAZIT.";
        }

        /*
           Because font metrics is based on a graphics context, we need to create
           a small, temporary image so we can ascertain the width and height
           of the final image
         */
        //System.out.println("sdgfsdf");
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        Font font = new Font("Arial", Font.PLAIN, 48);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int width = fm.stringWidth(text) + 20;
        int height = fm.getHeight() + 20;
        g2d.dispose();

        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        //g2d.setColor(Color.yellow);
        g2d.setFont(font);
        fm = g2d.getFontMetrics();
        g2d.setColor(Color.yellow);
        g2d.drawString(text, 3, fm.getAscent());
        g2d.dispose();
        //System.out.println("dfssdf");
        try {
            System.out.println("Starting export");
            File f = new File("src" + PoetryManager.fileSeparator + "creations" + PoetryManager.fileSeparator + "exported" + PoetryManager.fileSeparator + book.getId() + "_" + book.getTitle() + ".png");
            ImageIO.write(img, "png", f);
            System.out.println(f.getAbsolutePath());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void exportAsImage(Poem book) {
        String text = "Hello";
        String path = book.getPath();

        /*
           Because font metrics is based on a graphics context, we need to create
           a small, temporary image so we can ascertain the width and height
           of the final image
         */
        int width = 1000;
        int longestLine = 1;
        int nextLinePosition = 50;
        int height = 1000;
        int poemLines = 16;
        int fontsize = 48;
        String currline;
        int length;
        BufferedReader breader;
        try {
            breader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
            while ((currline = breader.readLine()) != null) {
                poemLines++;
                length = currline.length();
                if (length > longestLine) {
                    longestLine = length;
                }
            }
            breader.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Export.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Export.class.getName()).log(Level.SEVERE, null, ex);
        }
        width = fontsize * longestLine * 4 / 9 + 10;// 4/7
        height = fontsize * (poemLines - 14);

        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();

        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("src" + PoetryManager.fileSeparator + "data" + PoetryManager.fileSeparator + "CrimsonText-Roman.ttf")));
        } catch (IOException | FontFormatException e) {
            //Handle exception
        }

        Font font = new Font("Crimson Text", Font.PLAIN, fontsize); //Georgia
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();

        g2d.dispose();

        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setFont(font);
        //fm = g2d.getFontMetrics();
        g2d.setColor(new Color(241, 98, 69));
        g2d.setPaint(new Color(150, 150, 150));
        g2d.fillRect(0, 0, width, height);

        g2d.setColor(Color.BLACK);
        text = "";
        fontsize = 48;
        try {
            if (!path.endsWith(".poem")) { //path.endsWith(".pdf") || path.endsWith(".doc") || path.endsWith(".odt") || path.endsWith(".rtf") || path.endsWith(".docx")

                text = "Báseò z daného souboru NEJDE ZOBRAZIT.";
            } else {
                String line;
                BufferedReader br;
                try {
                    br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
                    try {
                        //String line;
                        while ((line = br.readLine()) != null) {
                            g2d.drawString(line, 20, nextLinePosition);
                            nextLinePosition = nextLinePosition + fontsize;
                        }
                        br.close();
                    } catch (IOException ioe) {
                        text = "BÁSEÒ NEJDE ZOBRAZIT.";
                    }
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(Export.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        } catch (FileNotFoundException ex) {
            text = "BÁSEÒ NEJDE ZOBRAZIT.";
        }
        fontsize = 24;
        font = new Font("Crimson Text", Font.PLAIN, fontsize); //Georgia
        g2d.setFont(font);
        String pmText = "   made with love of PoetryManager";
        g2d.setColor(Color.DARK_GRAY);
        int position = width - fontsize * pmText.length() * 4 / 9;
        g2d.drawString(pmText, position, nextLinePosition + 20);
        /*File file = new File("C:\\Read.text");

        BufferedReader br = null;
        //int nextLinePosition = 100;
        //int fontSize = 48;
        try {
            br = new BufferedReader(new FileReader(file));

            String line;
            while ((line = br.readLine()) != null) {
                g2d.drawString(line, 0, nextLinePosition);
                nextLinePosition = nextLinePosition + fontSize;
            }
            br.close();
        } catch (FileNotFoundException ex) {
            
        } catch (IOException ex) {
        }*/
        g2d.dispose();
        try {
            File f = new File("src" + PoetryManager.fileSeparator + "creations" + PoetryManager.fileSeparator + "exported" + PoetryManager.fileSeparator + book.getId() + "_" + book.getTitle() + ".png");
            ImageIO.write(img, "png", f);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void zipMultipleFiles(List<String> srcFiles, String filesPath) throws FileNotFoundException, IOException {

        FileOutputStream fos = new FileOutputStream(filesPath + "upload.zip");
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        for (String srcFile : srcFiles) {
            File fileToZip = new File(srcFile);
            FileInputStream fis = new FileInputStream(fileToZip);
            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
            zipOut.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            fis.close();
        }
        zipOut.close();
        fos.close();

    }

    public static boolean deleteFilesInFolder(String pathToFolder) {
        File file = new File(pathToFolder);
        String[] entries = file.list();
        File f;
        for (String entry : entries) {
            f = new File(pathToFolder, entry);
            f.delete();
        }
        if (file.delete()) {
            return true;
        } else {
            return false;
        }
    }

    public static int loadIntFromInternetFile(String urls) throws MalformedURLException, IOException {
        int numberInFile = 0;
        URL url = new URL(urls);
        Scanner s = new Scanner(url.openStream());
        String str = s.next();
        numberInFile = Integer.parseInt(str);
        return numberInFile;
    }

    public static void downloadFile(URL url, String fileName) throws Exception {
        try (InputStream in = url.openStream()) {
            Files.copy(in, Paths.get(fileName));
        }
    }

    public static BufferedReader loadFileUTF8(String path) throws FileNotFoundException, UnsupportedEncodingException {
        return new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
    }
}
