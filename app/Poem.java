package app;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public class Poem {

    private SimpleStringProperty author;
    private SimpleStringProperty title;
    private SimpleStringProperty id;
    private SimpleStringProperty date;
    private SimpleStringProperty path;
    private SimpleStringProperty numberOfVerses;
    private SimpleStringProperty language;
    private SimpleStringProperty genre;
    private SimpleStringProperty theme;
    private SimpleStringProperty lyricOrEpic;
    private SimpleStringProperty humorous;
    private SimpleStringProperty way;
    private SimpleStringProperty canBeUsed;
    private SimpleStringProperty note;
    private SimpleStringProperty Kardio1;
    private SimpleStringProperty Kardio2;
    private SimpleStringProperty other;
    private Integer number = 3;
    private SimpleBooleanProperty isChecked;
    private SimpleBooleanProperty isPrublic;
    private SimpleStringProperty imagePath;

    public Poem() {
    }

    public void setImagePath(String imagePath) {
        this.imagePath.set(imagePath);
    }

    public String getImagePath() {
        return imagePath.get();
    }

    public Poem(String s1, String s2, int nPoemsWithNoID) {
        SimpleStringProperty empty = new SimpleStringProperty("");
        author = new SimpleStringProperty(s1);
        title = new SimpleStringProperty(s2);
        this.id = empty;
        this.date = empty;
        this.path = new SimpleStringProperty("src" + PoetryManager.fileSeparator + "creations" + PoetryManager.fileSeparator + "NO_ID_" + nPoemsWithNoID + s1.replace(" ", "") + "_poetry_Unnamed.poem");
        this.numberOfVerses = empty;
        this.language = empty;
        this.genre = empty;
        theme = empty;
        lyricOrEpic = empty;
        humorous = empty;
        way = empty;
        canBeUsed = empty;
        note = empty;
        Kardio1 = empty;
        Kardio2 = empty;
        other = empty;
        isChecked = new SimpleBooleanProperty();
        isChecked.set(false);
        isPrublic = new SimpleBooleanProperty();
        isPrublic.set(true);

    }

    public Poem(String s1, String s2, int nPoemsWithNoID, boolean isPrivate) {
        SimpleStringProperty empty = new SimpleStringProperty("");
        author = new SimpleStringProperty(s1);
        title = new SimpleStringProperty(s2);

        this.id = new SimpleStringProperty(Integer.toString(-nPoemsWithNoID));
        this.date = empty;
        if (isPrivate) {
            this.path = new SimpleStringProperty("src" + PoetryManager.fileSeparator + "creations" + PoetryManager.fileSeparator + "notfinished" + PoetryManager.fileSeparator + "NO_ID_" + nPoemsWithNoID + "_Unnamed.poem");
        } else {
            this.path = new SimpleStringProperty("src" + PoetryManager.fileSeparator + "creations" + PoetryManager.fileSeparator + "NO_ID_" + nPoemsWithNoID + "_Unnamed.poem");
        }
        this.numberOfVerses = empty;
        this.language = empty;
        this.genre = empty;
        theme = empty;
        lyricOrEpic = empty;
        humorous = empty;
        way = empty;
        canBeUsed = empty;
        note = empty;
        Kardio1 = empty;
        Kardio2 = empty;
        other = empty;
        isChecked = new SimpleBooleanProperty();
        isChecked.set(false);
        isPrublic = new SimpleBooleanProperty();
        isPrublic.set(true);
        imagePath = new SimpleStringProperty("");
    }

    public Poem(String s1, String s2, String pathjou, int nPoemsWithNoID) {
        SimpleStringProperty empty = new SimpleStringProperty("");
        author = new SimpleStringProperty(s1);
        title = new SimpleStringProperty(s2);
        this.id = new SimpleStringProperty("-1");
        this.date = empty;
        this.path = new SimpleStringProperty(pathjou);
        this.numberOfVerses = empty;
        this.language = empty;
        this.genre = empty;
        theme = empty;
        lyricOrEpic = empty;
        humorous = empty;
        way = empty;
        canBeUsed = empty;
        note = empty;
        Kardio1 = empty;
        Kardio2 = empty;
        other = empty;
        isChecked = new SimpleBooleanProperty();
        isChecked.set(false);
        isPrublic = new SimpleBooleanProperty();
        isPrublic.set(true);
    }

    public Poem(String s1, String s2, String id3) {

        author = new SimpleStringProperty(s1);
        title = new SimpleStringProperty(s2);
        id = new SimpleStringProperty(id3);
    }

    public Poem(String author, String title, String id, String date, String path, String numberOfVerses) {
        this.title = new SimpleStringProperty(title);
        this.author = new SimpleStringProperty(author);
        this.id = new SimpleStringProperty(id);
        this.date = new SimpleStringProperty(date);
        this.path = new SimpleStringProperty(path);
        this.numberOfVerses = new SimpleStringProperty(numberOfVerses);
    }

    public Poem(String author, String title, String id, String date, String path, String numberOfVerses, String language, String genre) { //, String theme, String lyricOrEpic, String humorous
        this.title = new SimpleStringProperty(title);
        this.author = new SimpleStringProperty(author);
        this.id = new SimpleStringProperty(id);
        this.date = new SimpleStringProperty(date);
        this.path = new SimpleStringProperty(path);
        this.numberOfVerses = new SimpleStringProperty(numberOfVerses);
        this.language = new SimpleStringProperty(language);
        this.genre = new SimpleStringProperty(genre);
        /*this.theme = new SimpleStringProperty(theme);
        this.lyricOrEpic = new SimpleStringProperty(lyricOrEpic);
        this.humorous = new SimpleStringProperty(humorous);*/
    }

    Poem(String authorS, String titleS, String idS, String dateS, String pathS, String nVersesS, String languageS, String genreS, String themeS, String string8, String string9, String string10, String string11, String string12, String string13, String string14, String string15) {
        this.title = new SimpleStringProperty(titleS);
        this.author = new SimpleStringProperty(authorS);
        this.id = new SimpleStringProperty(idS);
        this.date = new SimpleStringProperty(dateS);
        this.path = new SimpleStringProperty(pathS);
        this.numberOfVerses = new SimpleStringProperty(nVersesS);
        this.language = new SimpleStringProperty(languageS);
        this.genre = new SimpleStringProperty(genreS);
        theme = new SimpleStringProperty(themeS);
        lyricOrEpic = new SimpleStringProperty(string8);
        humorous = new SimpleStringProperty(string9);
        way = new SimpleStringProperty(string10);
        canBeUsed = new SimpleStringProperty(string11);
        note = new SimpleStringProperty(string12);
        Kardio1 = new SimpleStringProperty(string13);
        Kardio2 = new SimpleStringProperty(string14);
        other = new SimpleStringProperty(string15);
    }

    Poem(String authorS, String titleS, String idS, String dateS, String pathS, String nVersesS, String languageS, String genreS, String string7, String string8, String string9, String string10, String string11, String string12, String string13, String string14, String string15, String string16, String string17, String string18) {
        this.title = new SimpleStringProperty(titleS);
        this.author = new SimpleStringProperty(authorS);
        this.id = new SimpleStringProperty(idS);
        this.date = new SimpleStringProperty(dateS);
        this.path = new SimpleStringProperty(pathS);
        this.numberOfVerses = new SimpleStringProperty(nVersesS);
        this.language = new SimpleStringProperty(languageS);
        this.genre = new SimpleStringProperty(genreS);
        theme = new SimpleStringProperty(string7);
        lyricOrEpic = new SimpleStringProperty(string8);
        humorous = new SimpleStringProperty(string9);
        way = new SimpleStringProperty(string10);
        canBeUsed = new SimpleStringProperty(string11);
        note = new SimpleStringProperty(string12);
        Kardio1 = new SimpleStringProperty(string13);
        Kardio2 = new SimpleStringProperty(string14);
        other = new SimpleStringProperty(string15);
        isChecked = new SimpleBooleanProperty("ano".equals(string16));
        isPrublic = new SimpleBooleanProperty("ano".equals(string17));
        imagePath = new SimpleStringProperty(string18);
        System.out.println(string18);
        //System.out.println(isPrublic);
    }

    public String getTitle() {

        return title.get();
    }

    public void setTitle(String s) {

        title.set(s);
    }

    public String getAuthor() {

        return author.get();
    }

    public void setAuthor(String s) {

        author.set(s);
    }

    public Integer getId() {
        if (id.get() == "") {
            return -1;
        }
        try {
            return Integer.parseInt(id.get());
        } catch (Exception e) {
            return 0;
        }
    }

    public void setId(String s) {

        id.set(s);
    }

    public String getDate() {

        return date.get();
    }

    public void setDate(String s) {

        date.set(s);
    }

    public String getPath() {

        return path.get();
    }

    public void setPath(String s) {

        path.set(s);
    }

    public Integer getNumberOfVerses() {
        if (numberOfVerses.get() == "") {
            return 0;
        }
        try {
            return Integer.parseInt(numberOfVerses.get());
        } catch (Exception e) {
            return 0;
        }
    }

    public void setNumberOfVerses(String s) {

        numberOfVerses.set(s);
    }

    public String getLanguage() {

        return language.get();
    }

    public void setLanguage(String s) {

        language.set(s);
    }

    public String getGenre() {

        return genre.get();
    }

    public void setGenre(String s) {

        genre.set(s);
    }

    public String getTheme() {

        return theme.get();
    }

    public void setTheme(String s) {

        theme.set(s);
    }

    public String getLyricOrEpic() {

        return lyricOrEpic.get();
    }

    public void setLyricOrEpic(String s) {

        lyricOrEpic.set(s);
    }

    public String getHumorous() {

        return humorous.get();
    }

    public void setHumorous(String s) {

        humorous.set(s);
    }

    public String getWay() {

        return way.get();
    }

    public void setWay(String s) {

        way.set(s);
    }

    public String getCanBeUsed() {

        return canBeUsed.get();
    }

    public void setCanBeUsed(String s) {

        canBeUsed.set(s);
    }

    public String getNote() {

        return note.get();
    }

    public void setNote(String s) {

        note.set(s);
    }

    public String getKardio1() {

        return Kardio1.get();
    }

    public void setKardio1(String s) {

        Kardio1.set(s);
    }

    public String getKardio2() {

        return Kardio2.get();
    }

    public void setKardio2(String s) {

        Kardio2.set(s);
    }

    public String getOther() {

        return other.get();
    }

    public void setOther(String s) {

        other.set(s);
    }

    public int getNumber() {

        return number;
    }

    public void setNumber(int n) {

        number = n;
    }

    public boolean getIsChecked() {
        return isChecked.get();
    }

    public void setIsChecked(boolean b) {
        isChecked.set(b);
        if (b) {
            System.out.println("true");
        } else {
            System.out.println("false");
        }
    }

    public boolean getIsPrublic() {
        return isPrublic.get();
    }

    public void setIsPrublic(boolean b) {
        isPrublic.set(b);
        if (b) {
            System.out.println("true");
        } else {
            System.out.println("false");
        }
    }

    @Override
    public String toString() {

        return (title.get() + ", napsal(a) " + author.get());
    }

    public Date getDateAsDate() {
        String formatData2 = "dd.MM.yyyy";//
        String formatData3 = "MMMM yyyy";//blbý pád
        String formatData4 = "dd.MM.yy";//
        String formatData5 = "dd.MM.yy HH:mm";
        String formatData6 = "yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(formatData5);
        Date date;
        if (this.date.get() == "") {
            return null;
        }
        try {
            //date = sdf.parse("22.01.98");
            date = sdf.parse(this.date.get());
            return date;
            //System.out.println(date);
        } catch (Exception ex) {
            Logger.getLogger(PoetryManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new Date();
    }

    Boolean isSelected() {
        return getIsChecked();
    }

    void setSelected(Boolean new_val) {
        setIsChecked(new_val);
    }
}
