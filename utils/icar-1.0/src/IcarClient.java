public class IcarClient {

    // La methode main
    public static void main(String[] args) {
	if (args.length >=1) {
	    new InterfaceClient(args[0]);
	} else {
	    new InterfaceClient("dictionnaire.dat");
	}
    }

}
