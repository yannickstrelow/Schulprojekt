package hts.projekt.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import hts.projekt.shared.Equity;
import hts.projekt.shared.User;
import hts.projekt.shared.Wallet;

public interface ServiceAsync {
	void login(String username, String password, AsyncCallback<Wallet> callback);

	void signUp(String username, String password, AsyncCallback<Wallet> callback);

	void getWallet(User user, AsyncCallback<Wallet> callback);

	void getAllEquities(AsyncCallback<List<Equity>> callback);

	void buyEquity(Wallet wallet, Equity equity, AsyncCallback<Wallet> callback);

	void sellEquity(Wallet wallet, Equity equity, AsyncCallback<Wallet> callback);

	void triggerUpdatePrices(AsyncCallback<Boolean> callback);

}
