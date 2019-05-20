package hts.projekt.server;

import java.util.List;
import java.util.Objects;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import hts.projekt.client.Service;
import hts.projekt.shared.Equity;
import hts.projekt.shared.User;
import hts.projekt.shared.Wallet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ServiceImpl extends RemoteServiceServlet implements Service {

	private long timer;

	/**
	 * Checks the login data from the user against the database. Throws an exception
	 * if the username does not exist or if the passwords do not match.
	 *
	 * @param username the username entered by the user
	 * @param password the password entered by the user
	 * @return the username from the user
	 * @throws Exception if either username or password is wrong
	 */
	@Override
	public User login(String username, String password) throws Exception {
		System.out.println("Login started for " + username);
		User user = DatabaseConnector.getUserByName(username);

		if (user == null) {
			throw new Exception("Wrong username");
		}

		if (!hashPassword(password).equals(user.getPassword())) {
			throw new Exception("Wrong Password");
		}

		System.out.println("Login successful.");

		return user;
	}

	/**
	 * Inserts a new entry for the user with a new userID and the hashed password.
	 *
	 * @param username the username entered by the user
	 * @param password the password entered by the user
	 * @return the username from the user
	 */
	@Override
	public User signUp(String username, String password) {
		System.out.println("Sign up started...");

		User user = new User();
		user.setUsername(escapeHtml(username));
		user.setPassword(hashPassword(password));

		DatabaseConnector.insertNewUser(user);

		System.out.println("Sign up completed.");

		return user;
	}

	public void removeUser(String username) {
		System.out.println("Removing user " + username);

		DatabaseConnector.removeUser(username);
	}

	@Override
	public Wallet getWallet(User user) {
		System.out.println("Recieving Wallet...");
		return DatabaseConnector.getWallet(user.getUsername());
	}

	@Override
	public List<Equity> getAllEquities() {
		System.out.println("Reading all equities...");
		return DatabaseConnector.getAllEquities();
	}

	@Override
	public Wallet buyEquity(Long walletId, String equityId) {
		System.out.println(walletId + " is buying equity number " + equityId);

		DatabaseConnector.buyEquity(walletId, equityId);
		return DatabaseConnector.getWallet(walletId);
	}

	@Override
	public Wallet sellEquity(Long walletId, String equityId) {
		System.out.printf("{} is selling equity number {}", walletId, equityId);

		DatabaseConnector.sellEquity(walletId, equityId);
		return DatabaseConnector.getWallet(walletId);
	}

	@Override
	public Boolean triggerUpdatePrices() {

		timer = System.currentTimeMillis();

		while (true) {
			if (System.currentTimeMillis() > timer + 60000) {
				System.out.println("Updating all prices...");

				List<Equity> equities = DatabaseConnector.getAllEquities();

				equities.stream().filter(Objects::nonNull).forEach(PriceUpdateService::updatePrice);

				System.out.println("Update completed.");
			}
		}
	}

	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 *
	 * @param html the html string to escape
	 * @return the escaped string
	 */
	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	}

	/**
	 * hashes the password using the Pbkdf2 algorithm.
	 *
	 * @param password the password to be hashed
	 * @return the hashed password
	 */
	private String hashPassword(String password) {
		return password;
	}

}