package aw.rmjtromp.RunicCore.utilities;

public final class HTTPRequest {

//	public static HTTPResponse get(URL url) throws Exception {
//		if(url != null) {
//			HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
//			
//			if(httpCon.getResponseCode() == HttpURLConnection.HTTP_OK) {
//				BufferedReader in = new BufferedReader(
//				new InputStreamReader(httpCon.getInputStream()));
//				String line, response = "";
//				while ((line = in.readLine()) != null) {
//					if (response.length() == 0) response = line;
//					else response += "\n" + line;
//				}
//				in.close();
//				
//				JSONObject json = (JSONObject) new JSONParser().parse(response);
//				System.out.print(json.keySet().toArray().toString());
//				
//				if(json.containsKey("instant_invite")) {
//					String inviteURL = json.get("instant_invite").toString();
//					new URL(inviteURL).toURI(); // if url is invalid, it will throw exception and not continue
//					
//					url = inviteURL;
//					last_check = System.currentTimeMillis();
//				}
//			}
//		}
//		return null;
//	}
//	
//	
//	public final class HTTPResponse {
//		
//		private HttpURLConnection httpCon;
//		
//		private HTTPResponse(HttpURLConnection httpCon) {
//			this.httpCon = httpCon;
//		}
//		
//		public int getResponseCode() {
//			return httpCon.getResponseCode();
//		}
//		
//	}
	
}
