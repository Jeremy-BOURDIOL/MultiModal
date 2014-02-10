import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.util.Iterator;
import java.util.Set;
import icar.IcarComponent;
import icar.util.TimePrecision;
import icar.event.*;

public abstract class Interface extends Frame 
    implements IcarRapportAnalyseListener
{
    
    // CONSTANTES

    // Nom du logiciel
    protected final static String nomLogiciel = "ICAR";

    // Version du logiciel
    protected final static String versionLogiciel = "15/06/2005";

    // Titre de la fenetre
    private final static String titreFenetre = " ..:: " + nomLogiciel + " ::.. ";

    // ATTRIBUTS

    // La zone de trace
    protected IcarComponent zoneIcar;

    // Le composant awt contenant la zone de dessin
    protected Panel paneBas;

    // La zone de texte
    protected TextField champTexte;

    // Indicateur d'apprentissage ou non
    protected boolean isTraining;


    // CONSTRUCTEUR
    public Interface() {
	// Constructeur de la classe heritee
	super();

	// Valeurs par defaut
	isTraining = false;

	// Fixe l'intitule de la frame
	setTitle(titreFenetre);

	setResizable(false);

	// Fixe la disposition des élements dans la frame
	this.setLayout(new BorderLayout());

	// Permet de fermer la fenetre en cliquant sur la croix
	addWindowListener (new WindowAdapter () {
			     public void windowClosing(WindowEvent e) {
				 System.exit(0);
			     }
			 }
			 );


	// CONSTRUCTION DE L'INTERFACE

	//-- Ajoute la zone de test/apprentissage à la frame
	this.add(creerZoneTest(), BorderLayout.WEST);



    }

    
    public Interface(String nomFichier) {
	this();
	
	// Verifie la consistance de l'agument
	if (!nomFichier.equals("")) {
	    // Verifie si le chargement est bon
	    if (!zoneIcar.loadFromFile(nomFichier)) {
		System.out.println("Impossible de charger le fichier "
				   + nomFichier);
		System.exit(0);
	    }

	}
    }

    // METHODES PRIVESS
    
    // Renvoie un Panel contenant la zone d'apprentissage/test
    private Panel creerZoneTest() {

	// Le panel contenant l'ensemble zoneDessin + champTexte
	Panel resultat = new Panel();

	// Le champ de texte du haut
	champTexte = new TextField("", 10);

	// La zone de dessin
	zoneIcar = new IcarComponent("", 200, 200);
	zoneIcar.setTrainingMode(false);

	zoneIcar.addIcarRapportAnalyseListener(this);

	// Fixe l'echantillonage apprioprie
	zoneIcar.setEchantillonage(TimePrecision.getMinPrecision());

	// Pane du bas
	paneBas = new Panel();
	paneBas.setLayout(new BorderLayout());



	// Ajoute tout les éléments au panel
	resultat.setLayout(new BorderLayout());      
	resultat.add(champTexte,BorderLayout.NORTH);
	resultat.add(paneBas,BorderLayout.SOUTH);
	resultat.add(zoneIcar,BorderLayout.CENTER);


	return resultat;
    }

    // Centre la frame sur l'écran
    protected void centrer() {
	Dimension d = Toolkit.getDefaultToolkit().getScreenSize();;
	setLocation( (d.width-getWidth())/2 , 
		     (d.height-getHeight())/2);

    }

    // ACCESSEURS PUBLIC

    // Renvoir l'echantillonage courant
    public int getEchantillonage() {
	return zoneIcar.getEchantillonage();
    }

    // Fixe l'echantillonage courant
    public void setEchantillonage(int echantillonage) {
	zoneIcar.setEchantillonage(echantillonage);
    }

    // METHODES PUBLIQUES

    // Ajouter un message dans la fenetre de log
    public abstract void ecrireFenetreLog(String texte); 

    // Modifie le texte de la zone de texte
    public void modifierZoneTexte(String texte) {
	champTexte.setText(texte);
	zoneIcar.setMotif(texte);
    }


    // EVENEMENTS

    // --IcarRapportAnalyse

    // Cette methode est appelé lorsqu'un évenement de produit 
    // (nouveau tracé analysé)
    public void processIcarRapportAnalyseEvent(IcarRapportAnalyseEvent event) {
	// Information de deboguage
	ecrireFenetreLog(event.getMessage());

	// Mise à jour de la classe reconnue
	if (event.getMotif() == null) {
	    modifierZoneTexte("");

	} else {
	    modifierZoneTexte(event.getMotif());
	}
    }


}
