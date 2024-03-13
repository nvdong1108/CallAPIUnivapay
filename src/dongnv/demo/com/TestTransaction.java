package dongnv.demo.com;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


public class TestTransaction {

	private Subscription[] subscription;
	
	private final String auth = "jhhlonEPLDQee4fZ9Yko.eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhcHBfdG9rZW4iLCJpYXQiOjE2NzA1NzMzMzMsIm1lcmNoYW50X2lkIjoiMTFlZDU1YWUtMGZmYi03YTMyLTkxMWMtYWZjZTg4ZTUxNTQ4Iiwic3RvcmVfaWQiOiIxMWVkNTVhZi04Y2VlLWE1YjgtOGMwNC1iMzgzMjYyY2ZjZTkiLCJkb21haW5zIjpbXSwibW9kZSI6ImxpdmUiLCJjcmVhdG9yX2lkIjoiMTFlZDU1YWUtMGZmYi03YTMyLTkxMWMtYWZjZTg4ZTUxNTQ4IiwidmVyc2lvbiI6MSwianRpIjoiMTFlZDc3OTgtYjhiZi1iODk1LWJkNWEtMzdiNGVlYTQwZWU2In0.CIEO3FJYbhybJkdbOvcgNu1FgdvV4QM2JCOqsXpC9pQ";

	public static void main(String[] args) {
		System.out.println("******************  BEGIN ***************** ");
		TestTransaction tt = new TestTransaction();
		tt.loadData();
		tt.callAPI();

	}

	public int callAPI() {

		if (subscription == null || subscription.length ==0) {
			return -1;
		}
		int length  = subscription.length;
		try {
			for (int i = 0; i <= length; i++) {
				Subscription sub   = subscription[i];
				if(i%10==0) {
					System.out.println(" process to ado : "+ sub.getAdo() + "  Index =  " + i );
					Thread.sleep(10000);
				}
				getApiCharges(sub);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return 0;

	}

	private void getApiCharges(Subscription sub) {
		try {
			
			Thread.sleep(2000);
			
			int numberOrder = sub.getCnt();
			
			String getCharges = "https://api.univapay.com/stores/11ed55af-8cee-a5b8-8c04-b383262cfce9/subscriptions/"
					+ sub.getSubscript().trim() + "/charges";
			URL obj2 = new URL(getCharges);

			HttpsURLConnection con2 = (HttpsURLConnection) obj2.openConnection();

			con2.setUseCaches(false);
			con2.setRequestProperty("Authorization", "Bearer " + auth);

			BufferedReader in2 = new BufferedReader(new InputStreamReader(con2.getInputStream()));
			String inputLine;
			StringBuffer response2 = new StringBuffer();
			while ((inputLine = in2.readLine()) != null) {
				response2.append(inputLine);
			}
			String resJsonData2 = "";
			resJsonData2 = response2.toString();
			
			Gson gson = new Gson();
			
			JsonObject jsonObject = gson.fromJson(resJsonData2, JsonObject.class);
			JsonArray jsonArray = jsonObject.getAsJsonArray("items");
			
			int successfulTransactionCount  = 0;
			
			
			for (JsonElement element : jsonArray) {
		        JsonObject transaction = element.getAsJsonObject();
		        if (transaction.get("status").getAsString().equals("successful")) {
		            successfulTransactionCount++;
		        }
		    }
			if(successfulTransactionCount  != numberOrder) {
				System.out.println( " *******  Error  : ADO = " + sub.getAdo()  + " payment Success != number Order  "+ successfulTransactionCount   + " : " + numberOrder);
			} else {
				System.out.println( " Pass : ADO =  " +sub.getAdo() + String.format(" payment = order = %d", successfulTransactionCount));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadData() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			File file = new File("C:\\SourceCode\\Workspace\\demo\\Demo\\subscription.json");
			subscription =mapper.readValue(file, Subscription[].class);
				
			System.out.println(" success : " + subscription.length);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static class Subscription {
		private String ado;
		private String subscript;
		private Integer cnt;

		public String getAdo() {
			return ado;
		}

		public void setAdo(String ado) {
			this.ado = ado;
		}

		public Integer getCnt() {
			return cnt;
		}

		public void setCnt(Integer cnt) {
			this.cnt = cnt;
		}

		public String getSubscript() {
			return subscript;
		}

		public void setSubscript(String subscript) {
			this.subscript = subscript;
		}

		@Override
		public String toString() {
			return "{ \"subscript\" : \"" + this.subscript + "\" , \"ado\": \"" + this.ado + "\" , \"cnt\" :" + this.cnt
					+ "}";
		}
	}

}
