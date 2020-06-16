public class Main {

    public static void main(String[] args) throws Exception {
        HttpClient httpClient = new HttpClient();
        String payload = httpClient.get("139.162.149.158:13500/FSS", 10, 1000);
        assert (!payload.isEmpty());
        httpClient.close();


    }
}
