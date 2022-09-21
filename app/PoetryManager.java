package app;

import static app.Export.*;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

// p¯idat
// tlaËÌtko Zobrazit ilustraci b·snÏ - otev¯e okno s ilustracÌ, p¯ÌpadnÏ moûnostÌ ilustraci p¯idat
// informaËnÌ ¯·dek, aù vim, co se stalo (nap¯. uloûenÌ) a kdy
// poËet zobrazen˝ch b·snÌ
// snazöÌ p¯id·v·nÌ û·nr˘ a tag˘
// filtrace podle û·nr˘ a tag˘
// Co to udÏlat p¯es DTB jako jsem dÏlal Warehouse?
// uËsat a zp¯ehlednit kÛd
// odstranit bug p¯i p¯esunu do Hotovo - p¯i zmÏnÏ nÏkterÈho z parametr˘ se zmÏnÌ na stejnou hodnotu i ostatnÌ

// z·lohov·nÌ
// dodÏlat aktivaci zkratek pro potvrzov·nÌ a ukl·d·nÌ (podle toho, co je focused)
public class PoetryManager extends Application {

    // main parametres
    boolean alreadyAsked = false;
    int nPoemsWithID;
    int nPoemsWithNoID;
    static String fileSeparator = System.getProperty("file.separator");
    private TableView<Poem> table;
    private TableView<Poem> table2;
    private ObservableList<Poem> poems;
    private ObservableList<Poem> poems2;
    private Text actionStatus;
    private Text logedAs = new Text();
    private Label searchLabel = new Label();
    private Label searchLabel2 = new Label();

    String datafolderPath = "src" + fileSeparator + "data" + fileSeparator;
    String appfolderPath = "src" + fileSeparator + "app" + fileSeparator;
    String creationsfolderPath = "src" + fileSeparator + "creations" + fileSeparator;
    String filesPath = datafolderPath;
    String settingsPath = datafolderPath + "settings.log";

    // user interface parametres
    MenuBar menubar = new MenuBar();
    MenuBar menubar2 = new MenuBar();
    private TextField poemAuthor;
    private TextField poemName;
    private TextArea poemText;
    private TextArea poemText2;
    private TextArea poemComment;
    private final String packageName = "app";
    String srcFolder = "src";
    private SortedList<Poem> sortedData;
    private SortedList<Poem> sortedData2;
    boolean permission = false;
    int userId = 8;
    boolean privateMode = true;
    Author user = new Author("TestovacÌ uûivatel", 1, 0);
    String userName = user.getName();
    Stage smallStage;
    Stage imageStage;
    Stage loadFileStage;
    Scene privateScene;
    Scene notPrivateScene;
    protected Stage programStage;
    int last = 0;
    boolean downloaded = false;
    Poem selectedBook;
    boolean imIsBlank = false;
    boolean imageInFront = false;
    boolean logedIn = false;
    String privateListPath = datafolderPath + "seznam private.lst";
    String publicListPath = datafolderPath + "seznam.lst";
    HTMLEditor htmlEditor = new HTMLEditor();
    SplitPane mainHb;

    public static void main(String[] args) {

        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws UnsupportedEncodingException {

        htmlEditor.setHtmlText("Deutschland!");
        toOutLine("Launching app");
        nPoemsWithNoID = loadNPoemsWithNoID();
        programStage = primaryStage;

        toOutLine("Loading actualisations...");
        last = loadNActualisations();
        downloadActualisations();
        poems = getInitialTableData();
        
        System.out.println("foooook");
        System.out.println("strat expoer");
        try {
            Export.exportPoemsToTextFile(poems, 1, 2);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        extractAndUseActualisations();

        setPermissions();

        loadStage();

        setMenus();

        loadUserInterface();
        loadSettings();
        
    }

    //do¯eöit
    private void openSmallWindow() {
        VBox mainVBox = new VBox();
        Scene scene = new Scene(mainVBox, 500, 550);
        TextArea text = new TextArea();
        mainVBox.setPadding(new Insets(25, 25, 25, 25));
        mainVBox.getChildren().addAll(text);
        text.setEditable(false);
        text.setPrefHeight(1000);
        text.setText("B·snÏ jsou super! Jooooo\n"
                + "Kaûdej to ale nevÌ :(");
        try {
            text.setText(loadTextOfHelp());
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        smallStage.setScene(scene);
        smallStage.show();
    }

    private void switchModes() {
        privateMode = !privateMode;
        toOutLine("Switched");
        if (privateMode) {
            programStage.setScene(privateScene);
            toOutLine("Entering private mode");
        } else {
            programStage.setScene(notPrivateScene);
            toOutLine("Entering not private mode");
        }
    }

    public String loadTextOfPoem1(Poem work) throws UnsupportedEncodingException {
        return loadTextOfPoem(work, poemText);
    }

    protected String loadTextOfPoem(Poem work, TextArea poemTextArea) throws UnsupportedEncodingException {
        String poemTextText = "";
        BufferedReader br;
        String line;
        String path = work.getPath();
        try {
            if (!path.endsWith(".poem")) { //path.endsWith(".pdf") || path.endsWith(".doc") || path.endsWith(".odt") || path.endsWith(".rtf") || path.endsWith(".docx")

                poemTextText = "B·seÚ z danÈho souboru NEJDE ZOBRAZIT.";
            } else {
                br = Export.loadFileUTF8(path);
                try {
                    while ((line = br.readLine()) != null) {
                        poemTextText += line + "\n";
                    }
                    br.close();
                    poemTextArea.setEditable(permission);

                    poemName.setText(work.getTitle());
                    poemAuthor.setText(work.getAuthor());
                } catch (IOException ioe) {
                    poemTextText = "B¡SE“ NEJDE ZOBRAZIT.";
                }
            }
        } catch (FileNotFoundException ex) {
            poemTextText = "B¡SE“ NEJDE ZOBRAZIT.";
        }
        return poemTextText;
    }

    private void openRymyCZ() {
        URL url;
        try {
            url = new URL("https://rymy.cz/");
            openWebpage(url);
        } catch (MalformedURLException ex) {
            Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static boolean openWebpage(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean openWebpage(URL url) {
        try {
            return openWebpage(url.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return false;
    }

    private int loadNPoemsWithNoID() {
        BufferedReader br;
        String line;

        String path = datafolderPath + "data.log";

        try {
            br = loadFileUTF8(path);
            toOutLine("NaËÌtajÌ se hodnoty poslednÌch id");
            // chybnÏ inicializovanÌ id nehotovejch
            // 2 nehotov˝ moûn· p¯eps·ny
            // zjistit na z·kladÏ poslednÌcho id v soubrou, jak˝ bylo pouûit˝ poslednÌ
            try {
                line = br.readLine();
                int nWithNo = Integer.parseInt(line);
                line = br.readLine();
                nPoemsWithID = Integer.parseInt(line);
                toOutLine("naËteno poslednÌ id");
                br.close();
                return nWithNo;
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (IOException ioe) {

        }

        return 0;
    }

    private int loadNActualisations() {
        BufferedReader br;
        String line;
        String path = datafolderPath + "act.log";

        try {
            br = loadFileUTF8(path);
            try {
                line = br.readLine();
                br.close();
                return Integer.parseInt(line);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (IOException ioe) {

        }

        return 0;
    }

    private File zipData() {
        List<String> srcFiles;
        srcFiles = createZipList();

        try {
            Export.zipMultipleFiles(srcFiles, datafolderPath);
            toOutLine("Zazipov·no");
        } catch (IOException ex) {
            Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    //do¯eöit, aby i ty, co jim p¯i¯adim id, se zabalily
    private void sendToAdminEmail(File outputData) {
        toOutLine("Neimplementov·no");
    }

    private void sendEmailToSupport() {
        /*   // Recipient's email ID needs to be mentioned.
      String to = "machatsam@gmail.com";

      // Sender's email ID needs to be mentioned
      String from = "ejchuchujuchuchu@gmail.com";

      // Assuming you are sending email from localhost
      String host = "localhost";

      // Get system properties
      Properties properties = System.getProperties();

      // Setup mail server
      properties.setProperty("mail.smtp.host", host);

      // Get the default Session object.
      Session session = Session.getDefaultInstance(properties);

      try {
         // Create a default MimeMessage object.
         MimeMessage message = new MimeMessage(session);

         // Set From: header field of the header.
         message.setFrom(new InternetAddress(from));

         // Set To: header field of the header.
         message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

         // Set Subject: header field
         message.setSubject("This is the Subject Line!");

         // Now set the actual message
         message.setText("This is actual message");

         // Send message
         Transport.send(message);
         System.out.println("Sent message successfully....");
      } catch (MessagingException mex) {
         mex.printStackTrace();
      }*/
    }

    private String loadTextOfHelp() throws UnsupportedEncodingException {
        String poemTextText = "";
        BufferedReader br;
        String line;
        String path = datafolderPath + "hilfe.txt";
        String errorString = "Nejde naËÌst n·povÏda.";
        try {
            if (!path.endsWith(".txt")) { //path.endsWith(".pdf") || path.endsWith(".doc") || path.endsWith(".odt") || path.endsWith(".rtf") || path.endsWith(".docx")

                poemTextText = errorString;
                /*try {
                    Runtime.getRuntime().exec(path);..
                } catch (IOException ex) {
                    System.out.println("Nope");
                }*/
            } else {
                br = loadFileUTF8(path);
                try {
                    while ((line = br.readLine()) != null) {
                        poemTextText += line + "\n";
                    }
                    br.close();

                } catch (IOException ioe) {
                    poemTextText = errorString;
                }
            }
        } catch (FileNotFoundException ex) {
            poemTextText = errorString;
        }
        return poemTextText;
    }

    private void saveNewPoem(Poem newPoem) throws FileNotFoundException, UnsupportedEncodingException {
        savePoem(newPoem, "Unnamed\n" + userName + "\n");
    }

    private void savePoem(Poem poem, String text) throws FileNotFoundException, UnsupportedEncodingException {
        BufferedWriter bw;
        String path = poem.getPath();
        bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "UTF-8"));
        try {
            bw.append(text);
            bw.close();
            //OutputStream outputStream = new FileOutputStream("waka.dat");
            //outputStream.write(bytes);
            actionStatus.setText("B·seÚ uloûena");
        } catch (IOException ex) {
            Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        saveHTML();
        saveSettings();
    }

    private void savePoemToBinary(Poem poem, String text) throws FileNotFoundException, UnsupportedEncodingException {
        String path = poem.getPath();

        BufferedWriter bw2;
        String path2 = poem.getPath();
        bw2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path.replace(".txt", ".poem")), "UTF-8"));
        try {
            //System.out.println(poem.getText());
            //System.out.println("kvak");
            bw2.append(text);
            bw2.close();
            //OutputStream outputStream = new FileOutputStream("waka.dat");
            //outputStream.write(bytes);
            //actionStatus.setText("B·seÚ uloûena");
        } catch (IOException ex) {
            Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void unzipFile(String pathIn, String pathTo) throws FileNotFoundException, IOException {
        File destDir = new File(pathTo);
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(pathIn));
        ZipEntry zipEntry = zis.getNextEntry();
        //System.out.println(zipEntry.toString());
        while (zipEntry != null) {
            File newFile = new File(destDir, zipEntry.toString());
            //newFile.createNewFile();
            //System.out.println(newFile.getPath());
            FileOutputStream fos = new FileOutputStream(newFile);
            int len;
            while ((len = zis.read(buffer)) > 0) {
                System.out.println("zaps·no");
                fos.write(buffer, 0, len);
            }
            fos.close();
            //System.out.println(newFile.getPath());
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
    }

    private void loadNewFiles() throws UnsupportedEncodingException {
        ObservableList<Poem> newListOfPoems = getTableData(datafolderPath + "downloaded" + fileSeparator + "seznam.lst");
        boolean isReallyNew = true;
        for (Poem newPoem : newListOfPoems) {
            for (Poem oldPoem : poems) {
                // pokud autroem novÈ b·snÏ je user ,tak nic, jinak zkopÌrovat do sloûky s novou poeziÌ
                if (newPoem.getAuthor().toString() == userName) {
                    isReallyNew = false;
                    break;
                }
            }
            if (isReallyNew) {
                poems.add(newPoem);
            }
        }
        saveTheTable();
    }

    private void setMenus() {
        // menu 1 items
        Menu menu1 = new Menu("Moûnosti");

        MenuItem MIExportAsImage = new MenuItem("Exportovat jako obr·zek");
        MIExportAsImage.setOnAction(e -> {
            int ix = table.getSelectionModel().getSelectedIndex();
            Poem book = (Poem) table.getSelectionModel().getSelectedItem();
            Export.exportAsImage(book);
            actionStatus.setText("B·seÚ vyexportov·na");
            System.out.println("Image exported");
        });
        MenuItem MINewPoem = new MenuItem("Nov· b·seÚ");
        MINewPoem.setOnAction(e -> {
            addNewPoem();
        });
        MenuItem MISaveList = new MenuItem("Uloûit seznam");
        MISaveList.setOnAction(e -> {
            saveTheTable();
        });
        MenuItem MISwitchModes = new MenuItem("P¯epnout reûim");
        MISwitchModes.setOnAction(e -> {
            switchModes();
        });
        MenuItem MIOpenRymyCZ = new MenuItem("rymy.cz");
        MIOpenRymyCZ.setOnAction(e -> {
            openRymyCZ();
        });
        MenuItem MIOpenHelp = new MenuItem("Hilfe");
        MIOpenHelp.setOnAction(e -> {
            openSmallWindow();
        });
        MenuItem MIExit = new MenuItem("UkonËit existenci tÈto instance");
        MIExit.setOnAction(e -> {
            closeThings();
        });
        programStage.setOnCloseRequest(e -> {
            closeThings();
        });

        // menu 1 view
        Menu menuView = new Menu("Zobrazit");
        MenuItem MIViewImage = new MenuItem("Obr·zek");
        MIViewImage.setOnAction(e -> {
            imageStage.show();
        });
        MenuItem MIMoveImage = new MenuItem("Obr·zek do/z pop¯edÌ");
        MIMoveImage.setOnAction(e -> {
            //imageStage.toFront();
            //imageInFront = true;
            /*if (imageInFront) {
                imageStage.toBack();
            } else {
                imageStage.toFront();
            }*/
            imageInFront = !imageInFront;
            imageStage.setAlwaysOnTop(imageInFront);
            /* if (!imageInFront) {
                programStage.requestFocus();
                //imageStage.toBack();
            }*/
        });

        // menu 1 help
        Menu menuHelp = new Menu("Info");
        MenuItem MIAboutUs = new MenuItem("O n·s");
        MIAboutUs.setOnAction(e -> {
            showAbout();
        });

        // menu 1 (public)
        menu1.getItems().add(MIExportAsImage);
        menu1.getItems().add(new SeparatorMenuItem());
        menu1.getItems().add(MISaveList);
        menu1.getItems().add(MISwitchModes);
        menu1.getItems().add(new SeparatorMenuItem());
        menu1.getItems().add(MIOpenRymyCZ);
        menu1.getItems().add(MIOpenHelp);
        menu1.getItems().add(new SeparatorMenuItem());
        menu1.getItems().add(MIExit);

        menuView.getItems().add(MIViewImage);
        menuView.getItems().add(MIMoveImage);

        menuHelp.getItems().add(MIAboutUs);

        menubar.getMenus().addAll(menu1, menuView, menuHelp);

        // menu 2 items
        Menu menu2 = new Menu("Moûnosti");
        MenuItem MINewPoem2 = new MenuItem("Nov· b·seÚ");
        MINewPoem2.setOnAction(e -> {
            addNewPoem2();
        });
        MenuItem MISaveList2 = new MenuItem("Uloûit seznam");
        MISaveList2.setOnAction(e -> {
            saveTheTable2();
        });
        MenuItem MISwitchModes2 = new MenuItem("P¯epnout reûim");
        MISwitchModes2.setOnAction(e -> {
            switchModes();
        });
        MenuItem MIOpenRymyCZ2 = new MenuItem("rymy.cz");
        MIOpenRymyCZ2.setOnAction(e -> {
            openRymyCZ();
        });
        MenuItem MIOpenHelp2 = new MenuItem("Hilfe");
        MIOpenHelp2.setOnAction(e -> {
            openSmallWindow();
        });
        MenuItem MIExit2 = new MenuItem("UkonËit existenci tÈto instance");
        MIExit2.setOnAction(e -> {
            closeThings();
        });

        // menu 2 (private)
        menu2.getItems().add(MINewPoem2);
        menu2.getItems().add(MISaveList2);
        menu2.getItems().add(MISwitchModes2);
        menu2.getItems().add(new SeparatorMenuItem());
        menu2.getItems().add(MIOpenRymyCZ2);
        menu2.getItems().add(MIOpenHelp2);
        menu2.getItems().add(new SeparatorMenuItem());
        menu2.getItems().add(MIExit2);
        menubar2.getMenus().add(menu2);
    }

    private void toOutLine(String string) {
        System.out.println(string);
        //actionStatus.setText(string);
    }

    private void downloadActualisations() {
        try {
            int newestAct = loadIntFromInternetFile("http://samovo.funsite.cz/download/act.txt");
            if (last < newestAct) {
                try {
                    String downloadSite = "http://samovo.funsite.cz/download/PMact.zip";
                    downloadFileToDownloadFolder(new URL(downloadSite));
                    downloaded = true;
                } catch (MalformedURLException ex) {
                    Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (Exception ex) {
            toOutLine("Nastala chyba p¯i stahov·nÌ:");
            toOutLine(ex.getMessage());
        }
    }

    private void extractAndUseActualisations() {
        if (downloaded) {
            try {
                String zipFilePath = datafolderPath + "downloaded.zip";
                String extractionPath = datafolderPath + "downloaded";
                unzipFile(zipFilePath, extractionPath);
                loadNewFiles();
                deleteNewFiles();
            } catch (IOException ex) {
                toOutLine("Nejde rozzipovat.");
            }
        }
    }

    private void loadStage() {
        smallStage = new Stage();
        imageStage = new Stage();
        //imageStage.initOwner(programStage);
        toOutLine("Loading stage...");
        String iconPath = datafolderPath + "icon.png";
        File f = new File(iconPath);
        javafx.scene.image.Image img;
        try {
            img = new javafx.scene.image.Image(f.toURI().toURL().toString());
            programStage.getIcons().add(img);
        } catch (MalformedURLException ex) {
            Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        //String appTitle = "PAS - Poetick˝ AdministrativnÌ SystÈm";
        String appTitle = "Poetry Manager 0.3.3";
        programStage.setTitle(appTitle);
        programStage.setHeight(900);
        programStage.setWidth(900);
    }

    private void loadUserInterface() {
        displayLoginWindow();

        if (logedIn) {
            String appName = "Poetick˝ AdministrativnÌ SystÈm";
            Font appNameFont = Font.font("Calibri", FontWeight.BOLD, 36);
            Label label = new Label(appName);
            label.setTextFill(Color.DARKBLUE);
            label.setFont(appNameFont);

            Label label2 = new Label(appName);
            label2.setTextFill(Color.DARKBLUE);
            label2.setFont(appNameFont);

            HBox labelHb = new HBox();
            table = new TableView<>();
            HBox labelHb2 = new HBox();
            table2 = new TableView<>();

            labelHb.setAlignment(Pos.CENTER);
            labelHb.getChildren().add(label);
            //labelHb.setVisible(false);

            labelHb2.setAlignment(Pos.CENTER);
            labelHb2.getChildren().add(label2);
            //labelHb2.setVisible(false);

            System.out.println("Loading tables...");
            try {
                // Table view, data, columns and properties
                countNumberOfVerses(poems);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            //table.setItems(poems);
            table.setEditable(true);
            try {
                // Table view, data, columns and properties
                poems2 = getInitialTableData2();
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                countNumberOfVerses(poems2);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            //table.setItems(poems);
            table2.setEditable(true);

            TableColumn authorCol = new TableColumn("Autor") {
                boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            authorCol.setCellValueFactory(new PropertyValueFactory<Poem, String>("author"));
            authorCol.setCellFactory(TextFieldTableCell.forTableColumn());
            authorCol.setOnEditCommit(new EventHandler<CellEditEvent<Poem, String>>() {
                @Override
                public void handle(CellEditEvent<Poem, String> t) {

                    ((Poem) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())).setAuthor(t.getNewValue());
                }
            });
            authorCol.setEditable(false);

            TableColumn authorCol2 = new TableColumn("Autor") {
                boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            authorCol2.setCellValueFactory(new PropertyValueFactory<Poem, String>("author"));
            authorCol2.setCellFactory(TextFieldTableCell.forTableColumn());
            authorCol2.setOnEditCommit(new EventHandler<CellEditEvent<Poem, String>>() {
                @Override
                public void handle(CellEditEvent<Poem, String> t) {

                    ((Poem) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())).setAuthor(t.getNewValue());
                }
            });
            authorCol2.setEditable(false);

//
//
// tohle trochu nepobÌr·m
// ale napadlo mÏ dÌky tomu, ûe bych mohl vûdy p¯i kliknutÌ na parametr zjuknout autora a podle toho v ten moment nastavit editaci
            ObservableMap<Integer, Boolean> editable = FXCollections.observableHashMap();
            for (Poem poem : poems) {
                //System.out.println(userName + " vs " + poem.getAuthor());
                if (poem.getAuthor().toString().equals(userName.toString())) {
                    //System.out.println("ok");
                    editable.put(poem.getId(), Boolean.TRUE);
                }
            }

            TableColumn titleCol = new TableColumn("N·zev dÌla");
            titleCol.setCellValueFactory(new PropertyValueFactory<Poem, String>("title"));
            titleCol.setCellFactory(TextFieldTableCell.forTableColumn());
            titleCol.setOnEditCommit(new EventHandler<CellEditEvent<Poem, String>>() {
                @Override
                public void handle(CellEditEvent<Poem, String> t) {
                    //editable.remove(t.getTablePosition().getRow());
                    selectedBook = (Poem) table.getSelectionModel().getSelectedItem();
                    //titleCol.setEditable(false);

                    if (permission || selectedBook.getAuthor().equals(userName)) {
                        selectedBook.setTitle(t.getNewValue());
                    } else {
                        table.setItems(sortedData);
                    }

                }
            });
            TableColumn titleCol2 = new TableColumn("N·zev dÌla");
            titleCol2.setCellValueFactory(new PropertyValueFactory<Poem, String>("title"));
            titleCol2.setCellFactory(TextFieldTableCell.forTableColumn());
            titleCol2.setOnEditCommit(new EventHandler<CellEditEvent<Poem, String>>() {
                @Override
                public void handle(CellEditEvent<Poem, String> t) {
                    //editable.remove(t.getTablePosition().getRow());
                    ((Poem) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())).setTitle(t.getNewValue());

                }
            });

            //titleCol.setCellFactory(StateTextFieldTableCell.forTableColumn(i -> Bindings.valueAt(editable, i).isEqualTo(Boolean.TRUE)));
            TableColumn idCol = new TableColumn("ID");
            idCol.setCellValueFactory(new PropertyValueFactory<Poem, Integer>("id"));
            idCol.setResizable(false);
            idCol.setMaxWidth(30);

            TableColumn checkedCol = new TableColumn("CZ-OK");
            //checkedCol.setCellValueFactory(new PropertyValueFactory<Poem, Boolean>("isChecked"));
            //checkedCol.setCellFactory(CheckBoxTableCell.forTableColumn());
            /*checkedCol.setCellValueFactory(
                new Callback<CellDataFeatures<Poem, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(CellDataFeatures<Poem, Boolean> p) {
                return p.getValue().getCompleted();
            }
        });
        checkedCol.setCellFactory(
                new Callback<TableColumn<Poem, Boolean>, TableCell<Poem, Boolean>>() {
            @Override
            public TableCell<Poem, Boolean> call(TableColumn<Poem, Boolean> p) {
                return new CheckBoxTableCell<>();
            }
        });*/

 /*checkedCol.setCellValueFactory(new PropertyValueFactory<Poem, Integer>("isChecked"));
        checkedCol.setCellFactory(tc -> new CheckBoxTableCell<>());

        checkedCol.setResizable(false);
        checkedCol.setMaxWidth(60);
        checkedCol.setOnEditCommit(new EventHandler<CellEditEvent<Poem, Boolean>>() {
            @Override
            public void handle(CellEditEvent<Poem, Boolean> t) {

                ((Poem) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())).setIsChecked(true);
            }
        });*/
//TableColumn select = new TableColumn("CheckBox");
            //checkedCol.setMinWidth(200);
            checkedCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Poem, CheckBox>, ObservableValue<CheckBox>>() {

                @Override
                public ObservableValue<CheckBox> call(
                        TableColumn.CellDataFeatures<Poem, CheckBox> arg0) {
                    Poem user = arg0.getValue();

                    CheckBox checkBox = new CheckBox();
                    if (!permission) {
                        checkBox.setDisable(true);
                        checkBox.setOpacity(100);
                    }
                    checkBox.selectedProperty().setValue(user.isSelected());

                    checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                        public void changed(ObservableValue<? extends Boolean> ov,
                                Boolean old_val, Boolean new_val) {

                            user.setSelected(new_val);

                        }
                    });

                    return new SimpleObjectProperty<CheckBox>(checkBox);

                }

            });
            /*checkedCol.setEditable(false);
        if(!permission){
            checkedCol.setEditable(false);
        }*/

 /*idCol.setCellFactory(TextFieldTableCell.forTableColumn());
        idCol.setOnEditCommit(new EventHandler<CellEditEvent<Poem, String>>() {
            @Override
            public void handle(CellEditEvent<Poem, String> t) {

                ((Poem) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())).setId(t.getNewValue());
            }
        });
        idCol.setEditable(false);*/
            //String formatData1 = "Q yyyy";
            String formatData2 = "dd.MM.yyyy";//
            String formatData3 = "MMMM yyyy";//blb˝ p·d
            String formatData4 = "dd.MM.yy";//
            String formatData5 = "dd.MM.yy HH:mm";
            String formatData6 = "yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(formatData6);
            Date date;
            try {
                //date = sdf.parse("22.01.98");
                System.out.println(sdf.format(new Date()));
                //System.out.println(date);
            } catch (Exception ex) {
                Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            //TableColumn dateCol = new TableColumn("Datum vzniku");
            //dateCol.setCellValueFactory(new PropertyValueFactory<Poem, Date>("dateAsDate"));
            TableColumn dateCol = new TableColumn("Datum vzniku");
            dateCol.setCellValueFactory(new PropertyValueFactory<Poem, String>("date"));
            dateCol.setCellFactory(TextFieldTableCell.forTableColumn());
            dateCol.setOnEditCommit(new EventHandler<CellEditEvent<Poem, String>>() {
                @Override
                public void handle(CellEditEvent<Poem, String> t) {
                    selectedBook = (Poem) table.getSelectionModel().getSelectedItem();

                    if (permission || selectedBook.getAuthor().equals(userName)) {
                        selectedBook.setDate(t.getNewValue());
                        System.out.println("povedlo se");
                    } else {
                        table.setItems(sortedData);
                        System.out.println("nepovedlo se");
                        //System.out.println(userName);
                        //System.out.println(selectedBook.getAuthor());
                        /*System.out.println(((Poem) t.getTableView().getItems().get(t.getTablePosition().getRow())).getAuthor().toString());

                    int ix = table.getSelectionModel().getSelectedIndex();
                    System.out.println(book.getAuthor());*/
                    }
                }
            });
            //Comparator dateComparator = new StringDateComparator();
            //dateCol.setComparator(dateComparator);
            dateCol.setMaxWidth(150);

            TableColumn dateCol2 = new TableColumn("Datum vzniku");
            dateCol2.setCellValueFactory(new PropertyValueFactory<Poem, String>("date"));
            dateCol2.setCellFactory(TextFieldTableCell.forTableColumn());
            dateCol2.setOnEditCommit(new EventHandler<CellEditEvent<Poem, String>>() {
                @Override
                public void handle(CellEditEvent<Poem, String> t) {
                    if (((Poem) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())).getAuthor().toString() == userName) {
                        ((Poem) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())).setDate(t.getNewValue());
                    } else {
                        table2.setItems(sortedData2);
                    }
                }
            });
            //Comparator dateComparator = new StringDateComparator();
            //dateCol.setComparator(dateComparator);
            dateCol.setMaxWidth(150);

            TableColumn pathCol = new TableColumn("Soubor");
            pathCol.setCellValueFactory(new PropertyValueFactory<Poem, String>("path"));
            if (permission) {
                pathCol.setCellFactory(TextFieldTableCell.forTableColumn());
            }
            pathCol.setOnEditCommit(new EventHandler<CellEditEvent<Poem, String>>() {
                @Override
                public void handle(CellEditEvent<Poem, String> t) {

                    ((Poem) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())).setPath(t.getNewValue());
                }
            });
            if (!permission) {
                pathCol.setVisible(false);
            }

            TableColumn nVersesCol = new TableColumn("Verö˘");
            nVersesCol.setCellValueFactory(new PropertyValueFactory<Poem, Integer>("numberOfVerses"));
            nVersesCol.setEditable(false);
            nVersesCol.setResizable(false);
            nVersesCol.setMaxWidth(50);
            TableColumn nVersesCol2 = new TableColumn("Verö˘");
            nVersesCol2.setCellValueFactory(new PropertyValueFactory<Poem, Integer>("numberOfVerses"));
            nVersesCol2.setEditable(false);
            nVersesCol2.setResizable(false);
            nVersesCol2.setMaxWidth(50);
            /*nVersesCol.setCellFactory(TextFieldTableCell.forTableColumn());
        nVersesCol.setOnEditCommit(new EventHandler<CellEditEvent<Poem, String>>() {
            @Override
            public void handle(CellEditEvent<Poem, String> t) {

                ((Poem) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())).setNumberOfVerses(t.getNewValue());
            }
        });*/

            TableColumn languageCol = new TableColumn("Jazyk");
            languageCol.setCellValueFactory(new PropertyValueFactory<Poem, String>("language"));
            languageCol.setCellFactory(TextFieldTableCell.forTableColumn());
            languageCol.setOnEditCommit(new EventHandler<CellEditEvent<Poem, String>>() {
                @Override
                public void handle(CellEditEvent<Poem, String> t) {

                    ((Poem) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())).setLanguage(t.getNewValue());
                }
            });
            languageCol.setResizable(false);
            languageCol.setMaxWidth(80);

            TableColumn languageCol2 = new TableColumn("Jazyk");
            languageCol2.setCellValueFactory(new PropertyValueFactory<Poem, String>("language"));
            languageCol2.setCellFactory(TextFieldTableCell.forTableColumn());
            languageCol2.setOnEditCommit(new EventHandler<CellEditEvent<Poem, String>>() {
                @Override
                public void handle(CellEditEvent<Poem, String> t) {

                    ((Poem) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())).setLanguage(t.getNewValue());
                }
            });
            languageCol2.setResizable(false);
            languageCol2.setMaxWidth(80);

            TableColumn genreCol = new TableColumn("é·nr");
            genreCol.setCellValueFactory(new PropertyValueFactory<Poem, String>("genre"));
            genreCol.setCellFactory(TextFieldTableCell.forTableColumn());
            genreCol.setOnEditCommit(new EventHandler<CellEditEvent<Poem, String>>() {
                @Override
                public void handle(CellEditEvent<Poem, String> t) {

                    ((Poem) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())).setGenre(t.getNewValue());
                }
            });
            genreCol.setMaxWidth(200);

            TableColumn genreCol2 = new TableColumn("é·nr");
            genreCol2.setCellValueFactory(new PropertyValueFactory<Poem, String>("genre"));
            genreCol2.setCellFactory(TextFieldTableCell.forTableColumn());
            genreCol2.setOnEditCommit(new EventHandler<CellEditEvent<Poem, String>>() {
                @Override
                public void handle(CellEditEvent<Poem, String> t) {

                    ((Poem) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())).setGenre(t.getNewValue());
                }
            });
            genreCol2.setMaxWidth(200);

            TableColumn themeCol = new TableColumn("TÈma");
            themeCol.setCellValueFactory(new PropertyValueFactory<Poem, String>("theme"));
            themeCol.setCellFactory(TextFieldTableCell.forTableColumn());
            themeCol.setOnEditCommit(new EventHandler<CellEditEvent<Poem, String>>() {
                @Override
                public void handle(CellEditEvent<Poem, String> t) {

                    ((Poem) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())).setTheme(t.getNewValue());
                }
            });
            themeCol.setMaxWidth(200);

            TableColumn themeCol2 = new TableColumn("TÈma");
            themeCol2.setCellValueFactory(new PropertyValueFactory<Poem, String>("theme"));
            themeCol2.setCellFactory(TextFieldTableCell.forTableColumn());
            themeCol2.setOnEditCommit(new EventHandler<CellEditEvent<Poem, String>>() {
                @Override
                public void handle(CellEditEvent<Poem, String> t) {

                    ((Poem) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())).setTheme(t.getNewValue());
                }
            });
            themeCol2.setMaxWidth(200);

            /*TableColumn numberCol = new TableColumn("Experiment");
        numberCol.setCellValueFactory(new PropertyValueFactory<Poem, Integer>("number"));
        /*numberCol.setCellValueFactory(new Callback<CellDataFeatures<Poem, Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue<Integer> call(CellDataFeatures<Poem, Integer> c) {
                return new SimpleIntegerProperty(c.getValue().getIntegerAsInt())
            
        );
        }
});
                /*
            
            protected void TableCell<Poem, Integer> call(TableColumn<Poem, Integer> col){
                
            }*/
 /*numberCol.setOnEditCommit(new EventHandler<CellEditEvent<Poem, String>>() {
            @Override
            public void handle(CellEditEvent<Poem, String> t) {

                ((Poem) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())).setNumber(Integer.parseInt(t.getNewValue()));
            }
        });*/
            searchLabel.setText("Vyhledat:");
            searchLabel2.setText("Vyhledat:");
            HBox searchHb = new HBox(20);
            HBox searchHb2 = new HBox(20);
            TextField filterField = new TextField();
            TextField filterField2 = new TextField();
            // 1. Wrap the ObservableList in a FilteredList (initially display all data).
            FilteredList<Poem> filteredData = new FilteredList<>(poems, p -> true);
            filteredData.setPredicate(person -> {
                // If filter text is empty, display all persons.
                if (person.getIsPrublic() || permission || person.getAuthor().equals(userName)) {
                    //System.out.println(person.getAuthor() + person.getIsPrublic());
                    return true;
                }
                return false; // Does not match.
            });
            // 2. Set the filter Predicate whenever the filter changes.
            filterField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(person -> {
                    // If filter text is empty, display all persons.
                    if (person.getIsPrublic() || permission) {
                        //System.out.println(person.getAuthor() + person.getIsPrublic());
                        if (newValue == null || newValue.isEmpty()) {
                            return true;
                        }

                        // Compare first name and last name of every person with filter text.
                        String lowerCaseFilter = newValue.toLowerCase();

                        try {

                            if (person.getAuthor().toLowerCase().contains(lowerCaseFilter) || (loadTextOfPoem1(person).toLowerCase().contains(lowerCaseFilter) && loadTextOfPoem1(person).toLowerCase() != "B·seÚ z danÈho souboru NEJDE ZOBRAZIT.")) {
                                return true; // Filter matches first name.
                                //} else if((loadTextOfPoem1(person).toLowerCase() != "B·seÚ z danÈho souboru NEJDE ZOBRAZIT.") && (loadTextOfPoem1(person).toLowerCase().matches(lowerCaseFilter))){
                            } else if (person.getTitle().toLowerCase().contains(lowerCaseFilter)) {
                                return true; // Filter matches last name.
                            }

                        } catch (UnsupportedEncodingException ex) {
                            Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    return false; // Does not match.
                });
            });

            // 3. Wrap the FilteredList in a SortedList. 
            sortedData = new SortedList<>(filteredData);

            // 4. Bind the SortedList comparator to the TableView comparator.
            sortedData.comparatorProperty().bind(table.comparatorProperty());

            // 5. Add sorted (and filtered) data to the table.
            table.setItems(sortedData);

            table.getColumns().setAll(authorCol, titleCol, idCol, checkedCol, dateCol, pathCol, nVersesCol, languageCol, genreCol, themeCol);
            table.setPrefWidth(450);
            table.setPrefHeight(300);
            //table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

            table.getSelectionModel().selectedIndexProperty().addListener(
                    new RowSelectChangeListener());

//begin of shit        
// 1. Wrap the ObservableList in a FilteredList (initially display all data).
            FilteredList<Poem> filteredData2 = new FilteredList<>(poems2, p -> true);
            /*filteredData2.setPredicate(person -> {
            // If filter text is empty, display all persons.
            if (person.getIsPrublic()) {
                //System.out.println(person.getAuthor() + person.getIsPrublic());
                return true;
            }
            return false; // Does not match.
        });*/
            // 2. Set the filter Predicate whenever the filter changes.
            System.out.println("Loading private data...");
            filterField2.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData2.setPredicate(person -> {
                    // If filter text is empty, display all persons.
                    if (person.getIsPrublic()) {
                        //System.out.println(person.getAuthor() + person.getIsPrublic());
                        if (newValue == null || newValue.isEmpty()) {
                            return true;
                        }

                        // Compare first name and last name of every person with filter text.
                        String lowerCaseFilter2 = newValue.toLowerCase();

                        try {

                            if (person.getAuthor().toLowerCase().contains(lowerCaseFilter2) || (loadTextOfPoem1(person).toLowerCase().contains(lowerCaseFilter2) && loadTextOfPoem1(person).toLowerCase() != "B·seÚ z danÈho souboru NEJDE ZOBRAZIT.")) {
                                return true; // Filter matches first name.
                                //} else if((loadTextOfPoem1(person).toLowerCase() != "B·seÚ z danÈho souboru NEJDE ZOBRAZIT.") && (loadTextOfPoem1(person).toLowerCase().matches(lowerCaseFilter))){
                            } else if (person.getTitle().toLowerCase().contains(lowerCaseFilter2)) {
                                return true; // Filter matches last name.
                            }

                        } catch (UnsupportedEncodingException ex) {
                            Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    return false; // Does not match.
                });
            });

            // 3. Wrap the FilteredList in a SortedList. 
            sortedData2 = new SortedList<>(filteredData2);

            // 4. Bind the SortedList comparator to the TableView comparator.
            sortedData2.comparatorProperty().bind(table2.comparatorProperty());

            // 5. Add sorted (and filtered) data to the table.
            table2.setItems(sortedData2);

            table2.getColumns().setAll(/*authorCol2,*/titleCol2/*, idCol, checkedCol*/, dateCol2/*, pathCol*/, nVersesCol2, languageCol2, genreCol2, themeCol2);
            table2.setPrefWidth(450);
            table2.setPrefHeight(300);
            //table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

            table2.getSelectionModel().selectedIndexProperty().addListener(
                    new RowSelectChangeListener2());
//end of shit

            // Buttons
            System.out.println("Loading buttons...");
            Button addbtn = new Button("P¯idat b·seÚ");
            addbtn.setOnAction(new AddButtonListener());
            Button savebtn = new Button("Uloûit seznam");
            savebtn.setOnAction(new SaveButtonListener());
            Button openRymyCZbtn = new Button("rymy.cz");
            openRymyCZbtn.setOnAction(new openRymyCZButtonListener());
            Button openImage = new Button("Nahr·t obr·zek");
            openImage.setOnAction(new openImageButtonListener());

            Button addbtn2 = new Button("P¯idat b·seÚ");
            addbtn2.setOnAction(new AddButtonListener2());
            Button savebtn2 = new Button("Uloûit seznam");
            savebtn2.setOnAction(new SaveButtonListener2());
            Button openRymyCZbtn2 = new Button("rymy.cz");
            openRymyCZbtn2.setOnAction(new openRymyCZButtonListener());
            Button saveToDatabasebtn = new Button("-> Hotovo");
            saveToDatabasebtn.setOnAction(new saveToDatabaseButtonListener());

            Button saveFilebtn1 = new Button("Uloûit ˙pravy");
            saveFilebtn1.setOnAction(new SaveFileButtonListener1());
            saveFilebtn1.setVisible(permission);
            Button giveIdButton = new Button("P¯idÏlit ID");
            giveIdButton.setOnAction(new giveIdButtonListener());
            giveIdButton.setVisible(permission);

            Button saveFilebtn = new Button("Uloûit ˙pravy");
            saveFilebtn.setOnAction(new SaveFileButtonListener());
            Button resetFilebtn = new Button("Resetovat text");
            resetFilebtn.setOnAction(new ResetButtonListener());
            Button delbtn = new Button("-> Koö");
            delbtn.setOnAction(new DeleteButtonListener());
            HBox buttonHb = new HBox(10);
            buttonHb.setAlignment(Pos.CENTER);
            buttonHb.getChildren().addAll(/*addbtn,*/savebtn, saveFilebtn1, giveIdButton, openImage, logedAs);
            HBox buttonHb2 = new HBox(10);
            buttonHb2.setAlignment(Pos.CENTER);
            buttonHb2.getChildren().addAll(addbtn2, savebtn2);

            mainHb = new SplitPane();
            SplitPane mainHb2 = new SplitPane();
            BorderPane bubuHb = new BorderPane();
            BorderPane bubuHb2 = new BorderPane();
            //poemHb.setLayoutY(500);
            mainHb.setPrefHeight(1000);
            mainHb2.setPrefHeight(1000);
            poemAuthor = new TextField("Autor");
            poemName = new TextField("N·zev dÌla");
            poemText = new TextArea("nenÌ vybr·na û·dn· b·seÚ.");
            poemComment = new TextArea("Koment·¯");
            poemText.setMinWidth(200);
            poemText.setPrefWidth(2000);
            //poemText.setMaxWidth(400);
            poemText.setWrapText(true);
            table.setPrefWidth(2000);
            HBox poemHb = new HBox(20);
            HBox poemHb2 = new HBox(20);
            poemAuthor.setMinWidth(400);
            //poemAuthor.setMaxHeight(100);
            poemName.setMinWidth(400);
            poemComment.setMinWidth(400);

            //poemAuthor = new TextField("Autor");
            //poemName = new TextField("N·zev dÌla");
            poemText2 = new TextArea("NenÌ zvolena û·dn· b·seÚ.");
            //poemComment = new TextArea("Koment·¯");
            poemText2.setMinWidth(200);
            poemText2.setPrefWidth(2000);
            //poemText.setMaxWidth(400);
            poemText2.setWrapText(true);
            table2.setPrefWidth(1500);
            //poemAuthor.setMinWidth(400);
            //poemAuthor.setMaxHeight(100);
            //poemName.setMinWidth(400);
            //poemComment.setMinWidth(400);

            BorderPane borderPane = new BorderPane();
            BorderPane borderPane2 = new BorderPane();
            //borderPane.setTop(bubuHb);
            borderPane.setCenter(poemText);
            // borderPane.setCenter(htmlEditor);

            htmlEditor.getHtmlText();

            borderPane2.setCenter(poemText2);
            HBox editPoemButtons = new HBox(10);
            //editPoemButtons.setVisible(false);
            editPoemButtons.getChildren().addAll(saveFilebtn, resetFilebtn, openRymyCZbtn, saveToDatabasebtn, delbtn);
            borderPane2.setBottom(editPoemButtons);
            //borderPane.setBottom(poemComment);

            //HBox poemTextHB = new HBox(20);
            //poemTextHB.setAlignment(Pos.BOTTOM_CENTER);
            //poemTextHB.getChildren().addAll(poemText);
            //poemTextHB.setAlignment(Pos.BOTTOM_CENTER);
            bubuHb.setTop(poemAuthor);
            bubuHb.setBottom(poemName);
            poemHb.getChildren().addAll(borderPane);
            poemHb2.getChildren().addAll(borderPane2);
            //poemHb2.getChildren().addAll(borderPane2);
            //mainHb.setAlignment(Pos.CENTER);
            //mainHb2.setAlignment(Pos.CENTER);
            mainHb.getItems().addAll(table, poemHb);
            mainHb2.getItems().addAll(table2, poemHb2);

            // Status message text
            actionStatus = new Text();
            actionStatus.setFill(Color.FIREBRICK);

            // Vbox
            VBox mainVBox = new VBox();
            VBox mainVBox2 = new VBox();
            VBox innerVBox = new VBox(20);
            VBox innerVBox2 = new VBox(20);
            innerVBox.setPadding(new Insets(25, 25, 25, 25));
            innerVBox2.setPadding(new Insets(25, 25, 25, 25));
            searchHb.getChildren().addAll(searchLabel, filterField, actionStatus);
            searchHb2.getChildren().addAll(searchLabel2, filterField2);
            //HBox searchHb2 = searchHb;
            //TextField filterField2 = filterField;
            //Label searchLabel2 = searchLabel;
            //searchHb2.getChildren().addAll(searchLabel2, filterField2);
            innerVBox.getChildren().addAll(/*labelHb,*/searchHb, mainHb, buttonHb/*, actionStatus*/); //
            innerVBox2.getChildren().addAll(/*labelHb,*/searchHb2, mainHb2, buttonHb2/*, actionStatus*/); //
            mainVBox.getChildren().addAll(menubar, innerVBox); //
            mainVBox2.getChildren().addAll(menubar2, innerVBox2); //

            // Scene
            notPrivateScene = new Scene(mainVBox, 500, 550); // w x h
            privateScene = new Scene(mainVBox2, 500, 550); // w x h
            programStage.setScene(notPrivateScene);
            programStage.show();
            imageStage.setWidth(600);
            imageStage.setHeight(600);
            imageStage.setX(0);
            imageStage.setY(0);
            imageStage.show();
            //openSmallWindow();
            switchModes();
            // Select the first row
            table.getSelectionModel().select(0);
            Poem book = table.getSelectionModel().getSelectedItem();
            actionStatus.setText(book.toString());
            logedAs.setText("Jsi p¯ihl·öen(a) jako: " + userName);
        }
    }

    private void setPermissions() {

        if (user.getPermissions() == 1) {
            permission = true;
        }
    }

    private void deleteNewFiles() {
        String pathToFolder = datafolderPath + "downloaded";
        boolean deleted;
        deleted = deleteFilesInFolder(pathToFolder);
        if (deleted) {
            toOutLine("Files deleted succesfully");
        } else {
            toOutLine("Files not deleted");
        }
    }

    private void displayLoginWindow() {
        Stage loginStage = new Stage();
        loginStage.initOwner(programStage);
        loginStage.initModality(Modality.APPLICATION_MODAL);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Scene scene = new Scene(grid, 350, 275);
        loginStage.setScene(scene);
        Text scenetitle = new Text("VÌtej!");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label userNameLabel = new Label("UûivatelskÈ jmÈno:");
        grid.add(userNameLabel, 0, 1);

        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        Label pw = new Label("Heslo:");
        grid.add(pw, 0, 2);

        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 2);

        Button btn = new Button("P¯ihl·sit se");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);

        final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);

        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                actiontarget.setFill(Color.FIREBRICK);
                actiontarget.setText("äpatnÏ zadanÈ ˙daje!");

                ArrayList<Author> users = new ArrayList();
                users.add(new Author("TestovacÌ uûivatel", 1, 1, "kab·t123"));
                users.add(new Author("TestovacÌ uûivatel 2", 2, 0, "poeticdrama3"));
                for (Author user1 : users) {
                    if (user1.getName().equals(userTextField.getText())) {
                        if (user1.getPassword().equals(pwBox.getText())) {
                            logedIn = true;
                            if (user1.getPermissions() == 1) {
                                permission = true;
                            }
                            loginStage.close();
                            break;
                        }
                    }
                }
                userName = userTextField.getText();

            }
        });
        /*loginStage.setOnCloseRequest((e -> {
            if (!logedIn) {
                programStage.close();
                smallStage.close();
                imageStage.close();
            }
        }));*/

        loginStage.showAndWait();
    }

    private void showAbout() {
        Stage helpStage = new Stage();
        helpStage.initOwner(programStage);
        helpStage.initModality(Modality.WINDOW_MODAL);
        Text textOfHelp = new Text("Aplikaci vytvo¯il Samuel Machat.\nemail: machatsam@gmail.com");
        HBox hbox = new HBox(20);
        
        hbox.setPadding(new Insets(25, 25, 25, 25));
        
        Scene scene = new Scene(hbox, 250, 200);
        hbox.getChildren().add(textOfHelp);
        helpStage.setScene(scene);
        helpStage.show();
    }

    private List<String> createZipList() {
        List<String> srcFiles = new ArrayList<String>();
        srcFiles.add(filesPath + "actions.log");
        for (Poem poem : poems ) {
           File f = new File(poem.getPath());
           if(f.getName().startsWith("-")){
               srcFiles.add(f.getPath());
           }
        }

        File folder = new File(creationsfolderPath);
        for (final File fileEntry : folder.listFiles()) {
            if (!fileEntry.isDirectory() && fileEntry.getName().startsWith("-")) {
                srcFiles.add(fileEntry.getPath());
            }
        }
        return srcFiles;
    }

    private void saveTheExactTableTo(ObservableList<Poem> list, String publicListPath) throws FileNotFoundException, UnsupportedEncodingException {
        BufferedWriter bw;
        bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(publicListPath), "UTF-8"));
        try {
            String genre;
            for (Poem work1 : list) {
//                    savePoemToBinary(work1, loadTextOfPoem1(work1));
                //bw.append(getSaveString(work1));
                bw.append(work1.getAuthor());
                bw.append("\t");
                bw.append(work1.getTitle());
                bw.append("\t");
                bw.append(work1.getPath());
                bw.append("\t");
                bw.append(work1.getDate());
                bw.append("\t");
                bw.append(work1.getId().toString());
                bw.append("\t");
                bw.append(work1.getKardio1());
                bw.append("\t");
                bw.append(work1.getKardio2());
                bw.append("\t");
                bw.append(work1.getOther());
                bw.append("\t");
                bw.append(work1.getNumberOfVerses().toString());
                bw.append("\t");
                bw.append(work1.getLanguage());
                bw.append("\t");
                bw.append(work1.getGenre());
                bw.append("\t");
                bw.append(work1.getLyricOrEpic());
                bw.append("\t");
                bw.append(work1.getHumorous());
                bw.append("\t");
                bw.append(work1.getWay());
                bw.append("\t");
                bw.append(work1.getTheme());
                bw.append("\t");
                bw.append(work1.getCanBeUsed());
                bw.append("\t");
                bw.append(work1.getNote());
                bw.append("\t");
                bw.append(work1.getIsChecked() ? "ano" : "ne");
                bw.append("\t");
                bw.append(work1.getIsPrublic() ? "ano" : "ne");
                bw.append("\t");
                bw.append(work1.getImagePath());
                bw.append("\n");

            }
            bw.close();
            zipData();
            System.out.println("Table saved.");
            actionStatus.setText("Tabulka uloûena");
        } catch (IOException ioe) {
            System.out.println("Chyba pri cteni ze souboru.");
        }
    }

    private void saveHTML() throws FileNotFoundException, UnsupportedEncodingException {
        BufferedWriter bw;
        String path = "duranga.html";
        bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "UTF-8"));
        try {
            bw.append(htmlEditor.getHtmlText());
            bw.close();
            //OutputStream outputStream = new FileOutputStream("waka.dat");
            //outputStream.write(bytes);
            actionStatus.setText("B·seÚ uloûena");
        } catch (IOException ex) {
            Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void saveDoubleFieldToFile(double[] field) {
        BufferedWriter bw;
        //System.out.println("Dddd");
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(settingsPath), "UTF-8"));
            try {
                String genre;
                for (int i = 0; i < field.length; i++) {
                    bw.append("" + field[i]);
                    bw.append("\n");
                }
                bw.close();
            } catch (IOException ioe) {
                System.out.println("Chyba pri cteni ze souboru.");
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void saveSettings() {

        double[] field = new double[13];
        field[0] = mainHb.getDividerPositions()[0];
        field[1] = programStage.getX();
        field[2] = programStage.getY();
        field[3] = programStage.getWidth();
        field[4] = programStage.getHeight();
        field[5] = imageStage.getX();
        field[6] = imageStage.getY();
        field[7] = imageStage.getWidth();
        field[8] = imageStage.getHeight();
        field[9] = smallStage.getX();
        field[10] = smallStage.getY();
        field[11] = smallStage.getWidth();
        field[12] = smallStage.getHeight();
        saveDoubleFieldToFile(field);
    }

    private void loadSettings() {

        BufferedReader br;
        String line;

        String path = settingsPath;

        try {
            br = loadFileUTF8(path);
            toOutLine("NaËÌtajÌ se nastavenÌ");
            // chybnÏ inicializovanÌ id nehotovejch
            // 2 nehotov˝ moûn· p¯eps·ny
            // zjistit na z·kladÏ poslednÌcho id v soubrou, jak˝ bylo pouûit˝ poslednÌ
            try {
                mainHb.setDividerPositions(Double.parseDouble(br.readLine()));
                programStage.setX(Double.parseDouble(br.readLine()));
                programStage.setY(Double.parseDouble(br.readLine()));
                programStage.setWidth(Double.parseDouble(br.readLine()));
                programStage.setHeight(Double.parseDouble(br.readLine()));
                imageStage.setX(Double.parseDouble(br.readLine()));
                imageStage.setY(Double.parseDouble(br.readLine()));
                imageStage.setWidth(Double.parseDouble(br.readLine()));
                imageStage.setHeight(Double.parseDouble(br.readLine()));
                smallStage.setX(Double.parseDouble(br.readLine()));
                smallStage.setY(Double.parseDouble(br.readLine()));
                smallStage.setWidth(Double.parseDouble(br.readLine()));
                smallStage.setHeight(Double.parseDouble(br.readLine()));
                toOutLine("naËteno");
                br.close();
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (IOException ioe) {

        }
    }

    private void closeThings() {
        System.out.println("Closing...");
        saveSettings();
        programStage.close();
        smallStage.close();
        imageStage.close();
    }

    private class openImageButtonListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            toOutLine("Opening image");
            openImageWindow();
        }

    }

    private void openImageWindow() {
        VBox mainVBox = new VBox();
        ImageView imageView = new ImageView();
        Pane pane = new Pane();
        Scene scene = new Scene(mainVBox, 500, 550);
        mainVBox.setPadding(new Insets(25, 25, 25, 25));
        mainVBox.getChildren().addAll(pane);
        pane.getChildren().addAll(imageView);
        pane.setPrefHeight(5000);
        toOutLine("Loading image");

        int ix = table.getSelectionModel().getSelectedIndex();
        Poem book = (Poem) table.getSelectionModel().getSelectedItem();
        File f = new File(creationsfolderPath + book.getImagePath());
        System.out.println("Cesta k obr·zku: " + book.getImagePath());
        Image image;
        try {
            image = new Image(f.toURI().toURL().toString());
            imageView.setImage(image);
            //imageView.setPreserveRatio(false);
            /*if (imageView.isPreserveRatio()) {
                if (imageStage.getHeight() > imageStage.getWidth()) {
                    imageView.setFitWidth(imageStage.getWidth());
                    imageView.setFitHeight(0);
                } else {
                    imageView.setFitWidth(0);
                    imageView.setFitHeight(imageStage.getHeight());
                }
            } else {
                imageView.setFitWidth(imageStage.getWidth());
                imageView.setFitHeight(imageStage.getHeight());
            }*/
            //imageView.fitHeightProperty().bind(pane.heightProperty());
            //imageView.fitWidthProperty().bind(imageView.fitHeightProperty());
            //imageView.set 

            //imageView.setFitWidth(100);
            System.out.println(f.getAbsolutePath());
            imIsBlank = false;
        } catch (MalformedURLException ex) {
            //Logger.getLogger(MyWorksManager.class.getName()).log(Level.SEVERE, null, ex);

            try {
                File f2 = new File(datafolderPath + "noImage.png");
                image = new Image(f2.toURI().toURL().toString());
                imageView.setImage(image);
                //imageView.setPreserveRatio(false);
                //imageView.fitHeightProperty().bind(pane.heightProperty());
                //imageView.fitWidthProperty().bind(imageView.fitHeightProperty());

            } catch (MalformedURLException ex1) {
                Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
        imageView.fitWidthProperty().bind(pane.widthProperty());
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);
        toOutLine("Image loaded");
        imageStage.setScene(scene);
        /*imageStage.setFocused(false);

        if (!imageStage.isShowing()) {
            imageStage.show();
        }
        programStage.setFocused(true);*/
        if (book.getImagePath() == null || book.getImagePath().length() <= 4) {
            Button button;
            FileChooser fc = new FileChooser();
            File newImageFile = fc.showOpenDialog(loadFileStage);
            String newName = numberToFivedigit(book.getId()) + "_" + newImageFile.getName();
            Path newPath = Paths.get(creationsfolderPath + newName);

            try {
                Files.copy(newImageFile.toPath(), newPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex) {
                Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println(newPath.toString());
            book.setImagePath(newName);
            System.out.println("hotovo");
        }
        programStage.requestFocus();
        /*if (imageInFront) {
            imageStage.toFront();
        }*/
    }

    private class saveToDatabaseButtonListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            saveTheCurrentToDatabase();
        }

    }

    private void saveTheCurrentToDatabase() {
        int ix = table2.getSelectionModel().getSelectedIndex();
        Poem chosenPoem = sortedData2.get(ix);
        moveFileToFinished(chosenPoem);
        poems.add(chosenPoem);
        saveTheTable();
        poems2.remove(chosenPoem);
        saveTheTable2();
    }

    private class openRymyCZButtonListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            openRymyCZ();
        }
    }

    private class ResetButtonListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            int ix = table2.getSelectionModel().getSelectedIndex();
            Poem chosenPoem = sortedData2.get(ix);
            try {
                poemText2.setText(loadTextOfPoem1(chosenPoem));
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    private class RowSelectChangeListener implements ChangeListener<Number> {

        @Override
        public void changed(ObservableValue<? extends Number> ov,
                Number oldVal, Number newVal) {

            int ix = newVal.intValue();

            if ((ix < 0) || (ix >= sortedData.size())) {

                return; // invalid data
            }

            Poem book = sortedData.get(ix);
            actionStatus.setText(book.toString());
            try {
                poemText.setText(loadTextOfPoem1(book));
                if (book.getImagePath() != null && book.getImagePath().length() > 4) {
                    System.out.println("NenÌ null");
                    System.out.println(book.getImagePath());
                    changeImage();
                } else if (!imIsBlank) {
                    loadBackupImage();
                }
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        private void changeImage() {
            Image image;
            //try {
            /*int ix = table.getSelectionModel().getSelectedIndex();
                Poem book = (Poem) table.getSelectionModel().getSelectedItem();
                File f = new File(creationsfolderPath + book.getImagePath());
                image = new Image(f.toURI().toURL().toString());
                imageView.setImage(image);
                //imageView.setPreserveRatio(false);
                imageView.fitHeightProperty().bind(pane.heightProperty());
                imageView.fitWidthProperty().bind(imageView.fitHeightProperty());
                System.out.println(f.getAbsolutePath());*/
            openImageWindow();
            /*} catch (MalformedURLException ex) {
                Logger.getLogger(MyWorksManager.class.getName()).log(Level.SEVERE, null, ex);
            }*/
        }

        private void loadBackupImage() {
            //changeImage();
            VBox mainVBox = new VBox();
            ImageView imageView = new ImageView();
            Pane pane = new Pane();
            Scene scene = new Scene(mainVBox, 500, 550);
            mainVBox.setPadding(new Insets(25, 25, 25, 25));
            mainVBox.getChildren().addAll(pane);
            pane.getChildren().addAll(imageView);
            pane.setPrefHeight(5000);
            toOutLine("Loading image");

            File f = new File(datafolderPath + "noImage.png");
            Image image;
            try {
                image = new Image(f.toURI().toURL().toString());
                imageView.setImage(image);
                //imageView.setPreserveRatio(false);
                imageView.fitHeightProperty().bind(pane.heightProperty());
                imageView.fitWidthProperty().bind(imageView.fitHeightProperty());
                System.out.println(f.getAbsolutePath());
                imIsBlank = true;
            } catch (MalformedURLException ex) {

                Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);

            }
            toOutLine("Image loaded");
            imageStage.setScene(scene);
            programStage.requestFocus();
        }
    }

    private class RowSelectChangeListener2 implements ChangeListener<Number> {

        @Override
        public void changed(ObservableValue<? extends Number> ov,
                Number oldVal, Number newVal) {

            int ix = newVal.intValue();

            if ((ix < 0) || (ix >= sortedData2.size())) {

                return; // invalid data
            }

            Poem book = sortedData2.get(ix);
            //actionStatus2.setText(book.toString());
            try {
                poemText2.setText(loadTextOfPoem1(book));
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private ObservableList<Poem> getInitialTableData() throws UnsupportedEncodingException {
        ArrayList<Poem> listOfWorks = new ArrayList();
        try {
            listOfWorks = loadWorks1FromCSV(publicListPath);
        } catch (ParseException ex) {
            Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        /*list.add(new Poem("The Thief", "Fuminori Nakamura"));
        list.add(new Poem("Of Human Bondage", "Somerset Maugham"));
        list.add(new Poem("The Bluest Eye", "Toni Morrison"));
        list.add(new Poem("I Am Ok You Are Ok", "Thomas Harris"));
        list.add(new Poem("Magnificent Obsession", "Lloyd C Douglas"));
        list.add(new Poem("100 Years of Solitude", "Gabriel Garcia Marquez"));
        list.add(new Poem("What the Dog Saw", "Malcolm Gladwell"));*/
        ObservableList<Poem> data = FXCollections.observableList(listOfWorks);

        return data;
    }

    private ObservableList<Poem> getTableData(String file) throws UnsupportedEncodingException {

        List<Poem> list = new ArrayList<>();
        ArrayList<Poem> listOfWorks = new ArrayList();
        try {
            listOfWorks = loadWorks1FromCSV(file);
        } catch (ParseException ex) {
            Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        /*list.add(new Poem("The Thief", "Fuminori Nakamura"));
        list.add(new Poem("Of Human Bondage", "Somerset Maugham"));
        list.add(new Poem("The Bluest Eye", "Toni Morrison"));
        list.add(new Poem("I Am Ok You Are Ok", "Thomas Harris"));
        list.add(new Poem("Magnificent Obsession", "Lloyd C Douglas"));
        list.add(new Poem("100 Years of Solitude", "Gabriel Garcia Marquez"));
        list.add(new Poem("What the Dog Saw", "Malcolm Gladwell"));*/
        ObservableList<Poem> data = FXCollections.observableList(listOfWorks);

        return data;
    }

    private ObservableList<Poem> getInitialTableData2() throws UnsupportedEncodingException {

        //List<Poem> list = new ArrayList<>();
        ArrayList<Poem> listOfWorks = new ArrayList();
        try {
            listOfWorks = loadWorks1FromCSV(privateListPath);
        } catch (ParseException ex) {
            Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        /*list.add(new Poem("The Thief", "Fuminori Nakamura"));
        list.add(new Poem("Of Human Bondage", "Somerset Maugham"));
        list.add(new Poem("The Bluest Eye", "Toni Morrison"));
        list.add(new Poem("I Am Ok You Are Ok", "Thomas Harris"));
        list.add(new Poem("Magnificent Obsession", "Lloyd C Douglas"));
        list.add(new Poem("100 Years of Solitude", "Gabriel Garcia Marquez"));
        list.add(new Poem("What the Dog Saw", "Malcolm Gladwell"));*/
        ObservableList<Poem> data = FXCollections.observableList(listOfWorks);

        return data;
    }

    private class AddButtonListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {

            addNewPoem();
        }

    }

    private class AddButtonListener2 implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {

            addNewPoem2();
        }

    }

    private void addNewPoem() {
        // Create a new row after last row
        nPoemsWithNoID = nPoemsWithNoID + 1;
        Poem newPoem = new Poem(userName, "Unnamed", nPoemsWithNoID);
        System.out.println("borec");
        poems.add(newPoem);
        System.out.println("kunda");
        int row = poems.size() - 1;

        /*File f = new File(newPoem.getPath());
                        try {
                        f.createNewFile();
                        } catch (IOException ex) {
                        Logger.getLogger(MyWorksManager.class.getName()).log(Level.SEVERE, null, ex);
                        }*/
        try {
            saveNewPoem(newPoem);

            saveToLogFile("Saved new poem: Unnamed, id: " + nPoemsWithNoID);
            saveTheTable();
            saveData();
            // Select the new row
            table.requestFocus();
            table.getSelectionModel().select(row);
            table.getFocusModel().focus(row);
            //table.refresh();
            actionStatus.setText("Nov· b·seÚ. Zadejte jmÈno autora a n·zev dÌla a obojÌ potvrÔte enterem.");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void addNewPoem2() {
        // Create a new row after last row
        nPoemsWithNoID = nPoemsWithNoID + 1;
        Poem newPoem = new Poem(userName, "Unnamed", nPoemsWithNoID, true);
        System.out.println("borec");
        poems2.add(newPoem);
        System.out.println("kunda");
        int row = poems2.size() - 1;

        /*File f = new File(newPoem.getPath());
                        try {
                        f.createNewFile();
                        } catch (IOException ex) {
                        Logger.getLogger(MyWorksManager.class.getName()).log(Level.SEVERE, null, ex);
                        }*/
        BufferedWriter bw;
        String path = newPoem.getPath();
        try {
            saveNewPoem(newPoem);

            saveToLogFile("Saved new unfinished poem: Unnamed, id: " + nPoemsWithNoID);
            saveTheTable2();
            saveData();
            // Select the new row
            table2.requestFocus();
            table2.getSelectionModel().select(row);
            table2.getFocusModel().focus(row);
            //table.refresh();
            //actionStatus2.setText("Nov· b·seÚ. Zadejte jmÈno autora a n·zev dÌla a obojÌ potvrÔte enterem.");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private class DeleteButtonListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {

            // Get selected row and delete
            int ix = table2.getSelectionModel().getSelectedIndex();
            Poem book = (Poem) table2.getSelectionModel().getSelectedItem();
            // Select a row
            if (table2.getItems().size() == 0) {

                //actionStatus2.setText("No data in table !");
                return;
            }

            if (ix != 0) {

                ix = ix - 1;
            }

            table2.requestFocus();
            table2.getSelectionModel().select(ix);
            table2.getFocusModel().focus(ix);

            moveFileToTrashbin(book);
            // table.setSelectionModel(null);
            poems2.remove(book);
            saveTheTable2();
            //actionStatus2.setText("Deleted: " + book.toString());
        }

    }

    private void moveFileToFinished(Poem poem) {
        try {
            savePoem(poem, poemText2.getText());
            saveTheTable2();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        String oldPath = poem.getPath();
        //System.out.println(oldPath);
        // rename file unnamed to wanted name
        String newPath;
        //newPath = oldPath.replace("notfinished", "");
        String userNameEdited = userName.replace(" ", "");
        newPath = creationsfolderPath + poem.getId() + "_" + userNameEdited + "_poetry_" + poem.getTitle() + ".poem";
        //String userNameEdited = userName.replace(" ", "");
        newPath = newPath.replace("Unnamed", poem.getTitle());
        try {
            Path temp = Files.move(Paths.get(oldPath), Paths.get(newPath));
            if (temp != null) {
                System.out.println("File moved.");
                poem.setPath(newPath);
            } else {
                System.out.println("File not moved.");
            }
        } catch (IOException ex) {
            Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void moveFileToFinished(Poem poem, String from) {
        from = datafolderPath + "downloaded";
        String oldPath = poem.getPath();
        //System.out.println(oldPath);
        String newPath = oldPath.replace(from, creationsfolderPath);
        try {
            Path temp = Files.move(Paths.get(oldPath), Paths.get(newPath));
            if (temp != null) {
                System.out.println("New File moved.");
                poem.setPath(newPath);
            } else {
                System.out.println("File not moved.");
            }
        } catch (IOException ex) {
            Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void moveFileToTrashbin(Poem poem) {
        String oldPath = poem.getPath();
        System.out.println(oldPath);
        String newPath = oldPath.replace("notfinished", "trashbin");
        try {
            Path temp = Files.move(Paths.get(oldPath), Paths.get(newPath));
            if (temp != null) {
                System.out.println("File moved.");
            } else {
                System.out.println("File not moved.");
            }
        } catch (IOException ex) {
            Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private class SaveFileButtonListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            try {
                BufferedWriter bw;
                String line;
                int ix = table2.getSelectionModel().getSelectedIndex();
                Poem chosenPoem = sortedData2.get(ix);
                String path = chosenPoem.getPath();
                try {
                    //OutputStream os = new;
                    savePoem(chosenPoem, poemText2.getText());
                    saveToLogFile("poem saved: " + chosenPoem.getId());
                    /*bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "UTF-8"));
                    try {
                        //System.out.println(poem.getText());
                        //System.out.println("kvak");
                        bw.append(poemText2.getText());
                        bw.close();
                        System.out.println("Uloûeno: " + path);
                        //actionStatus2.setText("B·seÚ uloûena");
                    } catch (IOException ex) {
                        Logger.getLogger(MyWorksManager.class.getName()).log(Level.SEVERE, null, ex);
                    }*/

                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    private class SaveFileButtonListener1 implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            try {
                BufferedWriter bw;
                String line;
                int ix = table.getSelectionModel().getSelectedIndex();
                Poem chosenPoem = sortedData.get(ix);
                String path = chosenPoem.getPath();
                try {
                    //OutputStream os = new;
                    savePoem(chosenPoem, poemText.getText());
                    saveToLogFile("poem saved: " + chosenPoem.getId());
                    /*bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "UTF-8"));
                    try {
                        //System.out.println(poem.getText());
                        //System.out.println("kvak");
                        bw.append(poemText2.getText());
                        bw.close();
                        System.out.println("Uloûeno: " + path);
                        //actionStatus2.setText("B·seÚ uloûena");
                    } catch (IOException ex) {
                        Logger.getLogger(MyWorksManager.class.getName()).log(Level.SEVERE, null, ex);
                    }*/

                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    private class SaveButtonListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {
            saveTheTable();
        }

    }

    private class SaveButtonListener2 implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {
            saveTheTable2();
        }

    }

    private void saveTheTable() {
        System.out.println("saving the table...");
        try {
            saveTheExactTableTo(poems, publicListPath);
        } catch (IOException ex) {
            Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void saveTheTable2() {
        System.out.println("saving the private table...");
        try {
            saveTheExactTableTo(poems2, privateListPath);
        } catch (IOException ex) {
            Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private ArrayList<Poem> loadWorks1FromCSV(String path) throws ParseException, FileNotFoundException, UnsupportedEncodingException {
        ArrayList<Poem> works = new ArrayList();

        BufferedReader br;
        String line;
        String[] data;

        br = loadFileUTF8(path);
        try {
            int sizeOfData = 20;
            String[] dataInGoodCondition;
            while ((line = br.readLine()) != null) {
                dataInGoodCondition = new String[sizeOfData];
                data = line.split("\t");
                for (int i = 0; i < data.length; i++) {
                    dataInGoodCondition[i] = data[i];
                    /*if(i==18){
                        System.out.println(dataInGoodCondition[18]);
                    }*/
                }
                for (int i = data.length; i < sizeOfData - 1; i++) {
                    dataInGoodCondition[i] = "";
                    /*if(i==18){
                        System.out.println("ne");
                    }*/
                }
                //System.out.println(dataInGoodCondition[18]);
                //Date date = new SimpleDateFormat("dd. MM. yyyy").parse("15. 03. 1999"); //data[3]
                Poem newWork = new Poem(dataInGoodCondition[0], dataInGoodCondition[1], dataInGoodCondition[4], dataInGoodCondition[3], dataInGoodCondition[2], dataInGoodCondition[8], dataInGoodCondition[9], dataInGoodCondition[10], dataInGoodCondition[14], dataInGoodCondition[11], dataInGoodCondition[12], dataInGoodCondition[13], dataInGoodCondition[15], dataInGoodCondition[16], dataInGoodCondition[5], dataInGoodCondition[6], dataInGoodCondition[7], dataInGoodCondition[17], dataInGoodCondition[18], dataInGoodCondition[19]); //, data[9], data[10]
                works.add(newWork);
            }
            br.close();
        } catch (IOException ioe) {
            System.out.println("Chyba pri cteni ze souboru.");
        }
        return works;
    }

    private void countNumberOfVerses(ObservableList<Poem> works) throws UnsupportedEncodingException {
        for (Poem work : works) {
            countNumberOfVerses(work);
        }
    }

    private void countNumberOfVerses(Poem work) throws UnsupportedEncodingException {
        if (!"".equals(work.getPath()) & work.getPath().endsWith(".poem")) {
            try {
                BufferedReader br;
                String line;
                int number = 0;
                br = loadFileUTF8(work.getPath());
                try {
                    while ((line = br.readLine()) != null) {
                        if (line.startsWith("<")) {
                            break;
                        }
                        if (line.isEmpty() == false) {
                            number++;
                        }
                    }
                    work.setNumberOfVerses(String.valueOf(number - 2));
                    br.close();
                } catch (IOException ioe) {
                    System.out.println("Chyba pri cteni ze souboru.");
                }

                /*File f = new File(work.getPath());
                    System.out.println(f.getName());
                    if (f.getName().charAt(0) != '0') {
                        String ending = f.getName();
                        String idString = work.getId().toString();
                        if (work.getId() < 100) {
                            idString = "000" + idString;
                        } else if (work.getId() < 100) {
                            idString = "00" + idString;
                        }
                        String newPath = srcFolder + fileSeparator + "creations" + fileSeparator + idString + "_" + work.getAuthor() + "_poetry_" + ending;
                        BufferedWriter bw;

                        try {
                            //OutputStream os = new;
                            String poemText = "";
                            BufferedReader br2;
                            String line2;
                            String path = work.getPath();
                            String poemTextJou = "";
                            try {
                                if (!path.endsWith(".txt")) { //path.endsWith(".pdf") || path.endsWith(".doc") || path.endsWith(".odt") || path.endsWith(".rtf") || path.endsWith(".docx")

                                    poemTextJou = "B·seÚ z danÈho souboru NEJDE ZOBRAZIT.";
                                    
                                } else {
                                    //br2 = new BufferedReader(new InputStreamReader(new FileInputStream(work.getPath()), "UTF-8"));
                                    br2 = loadFileUTF8(path);
                                    try {
                                        while ((line = br2.readLine()) != null) {
                                            poemTextJou += line + "\n";
                                        }
                                    } catch (IOException ioe) {
                                        poemTextJou = "B¡SE“ NEJDE ZOBRAZIT.";
                                    }
                                }
                            } catch (FileNotFoundException ex) {
                                poemTextJou = "B¡SE“ NEJDE ZOBRAZIT.";
                            }
                            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newPath), "UTF-8"));
                            try {
                                //System.out.println(poem.getText());
                                //System.out.println("kvak");
                                bw.append(poemTextJou);
                                bw.close();
                                //actionStatus.setText("B·seÚ uloûena");
                            } catch (IOException ex) {
                                Logger.getLogger(MyWorksManager.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        } catch (UnsupportedEncodingException ex) {
                            Logger.getLogger(MyWorksManager.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        work.setPath(newPath);
                    }*/
            } catch (FileNotFoundException ex) {
                Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void sortColByDate() {

    }

    private void saveToLogFile(String s) {
        try {
            BufferedWriter bw;
            String line;
            String path = datafolderPath + "actions.log";
            try {
                //OutputStream os = new;

                bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path, true), "UTF-8"));
                try {
                    //System.out.println(poem.getText());
                    //System.out.println("kvak");
                    Date d = new Date();
                    bw.append(d.toString());
                    bw.append("\n");
                    bw.append(s);
                    bw.append("\n");
                    bw.append("-\n");
                    bw.close();
                    System.out.println("Logged: " + s);
                } catch (IOException ex) {
                    Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
                }

            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void saveData() {
        try {
            BufferedWriter bw;
            String line;
            String path = datafolderPath + "data.log";
            try {
                //OutputStream os = new;

                bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path, false), "UTF-8"));
                try {
                    bw.write("" + nPoemsWithNoID + "\n");
                    bw.write("" + nPoemsWithID);
                    bw.close();
                    System.out.println("Data saved.");
                } catch (IOException ex) {
                    Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
                }

            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendChangesToCentralUnit() {
        File outputData = zipData();
        //sendToAdminEmail(outputData);
    }

    public void downloadFileToDownloadFolder(URL url) throws Exception {
        downloadFile(url, datafolderPath + "downloaded.zip");
    }

    //p¯i zav¯enÌ aplikace a p¯i p¯ekliknutÌ na jinou b·seÚ zkontrolovat uloûenÌ upravovanÈ b·snÏ
    private void checkEdits() {
        /*
        if(loadTextOfPoem1(work) != poemText2){
            viewCheckSaveWindow();
        }*/
    }

    private void viewCheckSaveWindow() {

    }

    private class giveIdButtonListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            int ix = table.getSelectionModel().getSelectedIndex();
            Poem book = (Poem) table.getSelectionModel().getSelectedItem();
            giveIdToPoem(book);
            String path = book.getPath();

            System.out.println("Adding new id to new name of file");
            String zeros = "";
            int bookId = book.getId();
            if (bookId < 100) {
                zeros = "000";
            } else if (bookId < 1000) {
                zeros = "00";
            } else if (bookId < 10000) {
                zeros = "0";
            }
            String path1 = path.replaceAll("(-[0-9]+_)", zeros + bookId + "_");
            //path1 = path1.replaceAll("0_", zeros + bookId + "_");

            actionStatus.setText("ID se nepoda¯ilo p¯idÏlit.");
            try {
                System.out.println("renaming file");
                Files.move(Paths.get(path), Paths.get(path1));
                toOutLine("Soubor p¯ejmenov·n");
                book.setPath(path1);
                toOutLine("Cesta k souboru zmÏnÏna");
                saveTheTable();
                actionStatus.setText("ID ˙spÏönÏ p¯idÏleno.");
            } catch (IOException ex) {
                Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("Soubor zmÏnÏn");
            //table.setItems(poems);
        }
    }

    private void giveIdToPoem(Poem poem) {
        if (poem.getId() < 1) {
            poem.setId("" + (nPoemsWithID + 1));
            nPoemsWithID = nPoemsWithID + 1;
            saveData();
            saveTheTable();
            System.out.println("ID p¯idÏleno");
        }
    }

    public String numberToFivedigit(int number) {
        String numberWith5Digits = Integer.toString(number);
        while (number < 10000) {
            number = number * 10;
            numberWith5Digits = "0" + numberWith5Digits;
        }
        return numberWith5Digits;
    }

}
