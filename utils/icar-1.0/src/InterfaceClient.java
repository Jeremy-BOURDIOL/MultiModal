import java.util.Calendar;
public class InterfaceClient extends Interface {

    // CONSTRUCTEUR
    public InterfaceClient(String nomFichier) {
	super(nomFichier);

	// Retaille la fenetre au mieux
	pack();

	// Centre la frame sur l'ecran
	this.centrer();
      
	// Affiche la fenetre
	show();
    }

    // METHODES
    // Ajouter un message dans la fenetre de log
    public void ecrireFenetreLog(String texte) {
	/*
	Calendar date = Calendar.getInstance();
	// Insere le texte dans la fenetre de log suivi 
	// d'un retour de ligne à la ligne 0
	System.out.print( "["+date.get(Calendar.HOUR_OF_DAY ) 
			   + ":" + date.get(Calendar.MINUTE)
			   + ":" + date.get(Calendar.SECOND) + "]"
			   + " " + texte + "\n");
	*/
    }
}
