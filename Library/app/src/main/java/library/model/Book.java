package library.model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.NavigableSet;

public class Book implements java.io.Serializable {

    private String title;
    private String author;
    private String callNumber;
    private static final long serialVersionUID = 1L; 
    
    public Book(String title, String author, String callNumber){
        this.title = title;
        this.author = author;
        this.callNumber = callNumber;
    }

    public String getTitle(){
        return title;
    }

    public String getAuthor(){
        return author;
    }

    public String getCallNumber(){
        return title;
    }
}

