public class Main {

    public static void main(String[] args) throws Exception {
        HttpClient httpClient = new HttpClient();
        String payload = httpClient.get("http://139.162.149.158:13500/FSS", 10, 6000);
        if(payload.isEmpty()){
            System.exit(-1);
        }
        httpClient.close();


    }
}
