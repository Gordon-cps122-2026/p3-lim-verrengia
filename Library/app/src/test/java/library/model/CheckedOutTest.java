package library.model;

public class CheckedOutTest {


    public static void main(String[] args) {
        CheckedOut instance = new CheckedOut(null, null);
        System.out.println(instance.getDueDate());

        instance.renewDueDate();
        System.out.println(instance.getDueDate());
    }
    
}
