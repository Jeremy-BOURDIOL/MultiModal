public class IcarClientIvy {

    // La methode main
    public static void main(String[] args) {
	if (args.length >=1) {
	    new InterfaceClientIvy(args[0]);
	} else {
	    new InterfaceClientIvy("dictionnaire.dat");
	}
    }

}
