package carmsreservationclient;

import ejb.session.stateless.CustomerEntitySessionBeanRemote;
import javax.ejb.EJB;

/**
 *
 * @author Zheng Yang
 */
public class Main {

    @EJB
    private static CustomerEntitySessionBeanRemote customerEntitySessionBeanRemote;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        MainApp mainApp = new MainApp(customerEntitySessionBeanRemote);
        mainApp.runApp();
    }
    
}
