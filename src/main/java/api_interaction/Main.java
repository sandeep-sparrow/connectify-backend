package api_interaction;


public class Main {
    public static void main(String[] args) {
        Client signup = new Client("noahsolomon2003", "password");
        Client.Validated user = signup.new Validated(signup.signUp());
        System.out.println(user.getAllPosts());
    }
}
