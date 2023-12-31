package racine;

import java.io.Console;
import java.sql.*;
import java.util.*;

public class Racine {
	
	private static String menu = "Usage: \n"                          // racine usages
			+ "\t racine [option] site/app [option] user/key\n"
			+ "\n"
			+ "options         :"
			+ "\t-n site name or app\n"
			+ "\t\t	-u user name, email or phone number\n"
			+ "\t\t	-k key\n"
			+ "\t\t	-d delete info\n"
			+ ""
			+ "registering     :   racine -n <site/app> -u <user>\n"
			+ "read            :   racine -r <site/app> -k <key>\n"
			+ "delete user     :   racine -d <site/app> -k <key>\n"
			+ "delete site/app :   racine -d <site/app>";
	
	private static Connection connect = null;
	private static Statement state = null;

	public static void main(String[] args) {
		
		int largs = args.length;
		Scanner entry = new Scanner(System.in);
		Console console = System.console();
		
		// treatment
		switch (largs) {
		
		case 4:
			
			if (args[0].equals("-n") && args[2].equals("-u")) {
				
				while (true) {
					// entrer le mot de passe sans affichage a l'ecran normally
					System.out.print("Password: ");
					char[] password = console.readPassword();
					// System.out.println(password);
			
					// confirmation
					System.out.print("Confirm:  ");
					char[] confirm = console.readPassword();
					// System.out.println(confirm);
					
					
					if (Arrays.equals(password, confirm)) {
						Register_r register = new Register_r(args[1], args[3], confirm);
						if (register.register_r() == true) {
							
							System.out.println("KEY : " + register.key_r());
							System.out.println("Take a photo of the key.");
						}
						else
							System.out.println("ERROR: ...");
						break;
					}
					else {
						System.out.println("ERROR: passwords are not the same.");
						continue;
					}
					
				}
			}
			else if (args[0].equals("-r") && args[2].equals("-k")) {
				if (read_r(args[1], args[3]) == true) {
					System.out.println("Identifiants relatifs a: " + args[0]);
					read_r(args[1], args[3]);
				}
			}
			else if (args[0].equals("-d") && args[2].equals("-k")) {
				System.out.print("Are you sure you want to delete info (y/n)? ");
				String yn = entry.nextLine();
				
				if (yn.equals("y") || yn.equals("Y")) {
					if (delete_r(args[1], args[3]) == true) { // supprimer si possible
						System.out.println("DELETE : ok");
					}
				}
				else 
					System.out.println("Bye");
				
				
			}
			else
				System.out.println(Racine.menu);
			
			break;
			
		case 2:
			
			if (args[0].equals("-d")) {
				System.out.print("Are you sure to delete info (y/n)? ");
				String yn = entry.nextLine();
				
				if (yn.equals("y") || yn.equals("Y")) {
					if (delete_r(args[1], null) == true) {
						System.out.println("DELETE : ok");
					}
				}
				else 
					System.out.println("Bye");
			}
			else {
				System.out.println(Racine.menu);
			}
			break;
			
		default:
			System.out.println(Racine.menu);
		}
		
		entry.close();
	}
	
	// fonction d'affichage des renseignements
	// relatifs a l'utilisateur
	
	public static boolean read_r(String appId, String key) {
		
		// doit retourner les renseignements relatifs
		// au site ou a l'application de l'utilisateur
		// essentiellement
		try {
			Class.forName("org.sqlite.JDBC");
			connect = DriverManager.getConnection("jdbc:sqlite:racine.db");
			connect.setAutoCommit(false);
			
			state = connect.createStatement();
			String query = "SELECT user, password WHERE app=" + appId + "AND key=" + key + ";";
			ResultSet result = state.executeQuery(query);
			String userId = result.getString("user");
			String password = result.getString("password");
			String answer = "User: " + userId + "\n"
					+ "Password: " + password;
			System.out.println(answer);
			
			result.close();
			state.close();
			connect.close();
			return true;
		}
		catch (Exception e) {
			// System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.out.println("ERROR: data not found");
			System.exit(0);
			return false;
		} 
	}
	
	public static boolean delete_r(String appId, String key) {
		
		// doit supprimer des renseignements 
		// - nom du site ou du processus
		// - renseignements retournes par la clee
		try {
			Class.forName("org.sqlite.JDBC");
			connect = DriverManager.getConnection("jdbc:sqlite:racine.db");
			connect.setAutoCommit(false);
			
			state = connect.createStatement();
			if (key == null) {
				String query = "DELETE FROM racine WHERE app=" + appId;
				state.executeUpdate(query);
			}
			else {
				String query = "DELETE FROM racine WHERE app=" + appId + "AND key=" + key + ";";
				state.executeUpdate(query);
			}
			connect.commit();
			state.close();
			connect.close();
			return true;
		}
		catch (Exception e) {
			// System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.out.println("ERROR: data not found");
			System.exit(0);
			return false;
		} 
		
	}
}
